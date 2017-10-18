package cz.zcu.kiv.osgi.demo.parking.lane.status.impl;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.lane.statistics.impl.LaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.status.ILaneStatus;

/**
 * Provides traffic lane status handling.
 * 
 * @author brada
 *
 */
public class LaneStatus implements ILaneStatus {

	private LaneStatistics stats = null;
	
	private static final int MAX_VEHICLES_IN_BATCH = 7;
	private Random r;
	
	private Logger logger;
	private static final String lid = "LaneStatus.r4";
	
	private static LaneStatus instance = null;
	
	/**
	 * Singleton instance return.
	 */
	public static ILaneStatus getInstance() {
		if (instance == null) {
			instance = new LaneStatus();
		}
		return instance;
	}
	
	protected LaneStatus() {
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		r = new Random();
		stats = (LaneStatistics) LaneStatistics.getInstance();
	}
	
	@Override
	public int getNumVehiclesLeaving() {
		// the number of vehicles is randomly generated and added to the statistics counter
		int vehiclesLeaving = r.nextInt(MAX_VEHICLES_IN_BATCH);
		logger.info(lid+": generating {} vehicles to leave the lane for parking",vehiclesLeaving);
		stats.vehiclesPassing(vehiclesLeaving);
		return vehiclesLeaving;
	}

}
