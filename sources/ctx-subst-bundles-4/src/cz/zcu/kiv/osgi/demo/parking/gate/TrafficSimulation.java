package cz.zcu.kiv.osgi.demo.parking.gate;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.carpark.flow.IVehicleFlow;
import cz.zcu.kiv.osgi.demo.parking.carpark.status.IParkingStatus;
import cz.zcu.kiv.osgi.demo.parking.dashboard.DashboardActivator;
import cz.zcu.kiv.osgi.demo.parking.lane.status.ILaneStatus;

/**
 * Traffic simulator, run by Gate activator.
 * 
 * @author Premek Brada (brada@kiv.zcu.cz)
 */
public class TrafficSimulation implements Runnable
{

	private static final int NUM_CYCLES = 10;
	private static final long PAUSE_TIME = 300;
	
	// dependencies - services needed from other bundles
	private IVehicleFlow flow;
	private ILaneStatus lane;
	private IParkingStatus parking;
	
	private GateActivator activator;
	private Logger logger;
	private static final String lid = "TrafficSimulation@Gate.r4";
	private static final long WAIT_TIME = 100;
	
	private VehicleSink vehicleSink;

	public TrafficSimulation(GateActivator gateActivator, IVehicleFlow flow, ILaneStatus lane, IParkingStatus status)
	{
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		this.activator = gateActivator;
		this.flow = flow;
		this.lane = lane;
		this.parking = status;
	}
	
	/**
	 * Simulates traffic by "injecting" vehicles into the VehicleSink.
	 */
	@Override
	public void run()
	{
		logger.info("(!) "+lid+": traffic simulation thread starting ({} cycles)",NUM_CYCLES);
		Random r = new Random();
		
		ensureServicesAvailable();
		vehicleSink = activator.getSink();
		vehicleSink.setOpen(true);
		
		logger.info("*** "+lid+": all set, let's go simulating the traffic now ***");
		int vehiclesIn, vehiclesOut;
		for (int i = 0; i < NUM_CYCLES; ++i) {
			logger.info(lid+": loop #{}", i);
			ensureServicesAvailable();
			vehicleSink = activator.getSink();
			
			vehiclesIn  = lane.getNumVehiclesLeaving();
			vehiclesOut = r.nextInt(parking.getCapacity() - parking.getNumFreePlaces() + 1);
			logger.info(lid+": simulate {} entering and {} leaving vehicles, {} free for parking",
					vehiclesIn, vehiclesOut, parking.getNumFreePlaces());
			vehicleSink.exchangeVehicles(vehiclesIn, vehiclesOut);
			try {
				Thread.sleep(PAUSE_TIME);
			}
			catch (InterruptedException e) {
			    logger.warn("(!)"+lid+": thread interrupted");
				e.printStackTrace();
			}
			Thread.yield();
		}
		logger.info("(!) "+lid+": traffic simulation thread ended");
	}

	
	private void ensureServicesAvailable() 
	{
		// check and wait for dependencies
		// Warning: fragile -- does not handle services disappearing during runtime
		while ((flow == null) || (lane == null) || (parking == null)) {
			logger.warn(lid+": some required service not set, waiting and trying again...");
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				logger.warn("(!)"+lid+": thread interrupted");
				e.printStackTrace();
			}
			flow = (flow == null) ? activator.getVehicleFlowService() : flow;
			lane = (lane == null) ? activator.getLaneStatusService() : lane;
			parking = (parking == null) ? activator.getParkingStatusService() : parking;
		}
		
		// once we have all dependencies, make sure bundle's provisions are exported
		activator.registerGateStatsSvc(flow,parking);
		activator.registerGateCtlSvc(flow);
	}

}
