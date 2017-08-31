package cz.zcu.kiv.osgi.demo.parking.gate;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private Logger logger;
	private static final String lid = "TrafficSimulation@Gate.r3";
	
	// dependencies
	private VehicleSink vehicleSink;

	public TrafficSimulation(VehicleSink sink, ILaneStatus lane)
	{
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		this.vehicleSink = sink;
		this.lane = lane;
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
		
		for (int i = 0; i < NUM_CYCLES; ++i) {
			logger.info(lid+": loop #{}", i);
			int vehiclesIn  = lane.getNumVehiclesLeaving();
			int vehiclesOut = r.nextInt(MAX_VEHICLES_IN_BATCH);
			logger.info(lid+": simulate {} entering and {} leaving vehicles",
					vehiclesIn, vehiclesOut);
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
