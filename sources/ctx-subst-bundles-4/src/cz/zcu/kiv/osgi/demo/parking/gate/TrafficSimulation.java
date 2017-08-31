package cz.zcu.kiv.osgi.demo.parking.gate;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.carpark.status.IParkingStatus;
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
	private static final int MAX_VEHICLES_IN_BATCH = 10;
	
	private ILaneStatus lane;
	private IParkingStatus status;
	
	private Logger logger;
	private static final String lid = "TrafficSimulation@Gate.r4";
	
	// dependencies
	private VehicleSink vehicleSink;

	public TrafficSimulation(VehicleSink sink, ILaneStatus lane, IParkingStatus status)
	{
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		this.vehicleSink = sink;
		this.lane = lane;
		this.status = status;
	}
	
	/**
	 * Simulates traffic by "injecting" vehicles into the VehicleSink. Run by Gate 
	 * activator in r3 since Gate is the stats provider.
	 */
	@Override
	public void run()
	{
		logger.info("(!) "+lid+": traffic simulation thread starting ({} cycles)",NUM_CYCLES);
		Random r = new Random();
		vehicleSink.setOpen(true);
		
		int vehiclesIn, vehiclesOut;
		for (int i = 0; i < NUM_CYCLES; ++i) {
			logger.info(lid+": loop #{}", i);
			vehiclesIn  = lane.getNumVehiclesLeaving();
			vehiclesOut = r.nextInt(status.getCapacity() - status.getNumFreePlaces() + 1);
			logger.info(lid+": simulate {} entering and {} leaving vehicles, {} free for parking",
					vehiclesIn, vehiclesOut, status.getNumFreePlaces());
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

}
