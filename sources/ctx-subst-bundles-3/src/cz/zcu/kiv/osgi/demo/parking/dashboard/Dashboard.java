package cz.zcu.kiv.osgi.demo.parking.dashboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.lane.statistics.ILaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.gate.statistics.IGateStatistics;

/**
 * Displays statistics of the underlying simulation.
 * 
 * @author brada
 *
 */
public class Dashboard implements Runnable
{
	// TODO learn how to use config admin service to set these values
	private static final int NUM_CYCLES = 12;
	private static final long WAIT_TIME = 100;
	private static final long PAUSE_TIME = 300;
	
	Logger logger = null;
	private DashboardActivator activator;
	
    private static final String lid = "Dashboard.r3";

	// dependencies, full gate stats now
	IGateStatistics gateStats = null;
	ILaneStatistics laneStats = null;
	
	public Dashboard(DashboardActivator activator, IGateStatistics gate, ILaneStatistics lane) 
	{
		this.logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		
		this.activator = activator;
		gateStats = gate;
		laneStats = lane;
	}
	
	@Override
	public void run()
	{
		int gateNum;
		int laneNum;
		
		logger.info("(!)"+lid+": thread starting");
		
		ensureServicesAvailable();
		
		gateNum = gateStats.getEventCount();
		laneNum = laneStats.getCountVehiclesPassed();
		logger.info("*** "+lid+": STARTING RUN ({} cycles)", NUM_CYCLES);
		logger.info("*** "+lid+": initial stats -- lane vehicles passed {}, gate events {}" , laneNum, gateNum );
		for (int i=0; i<NUM_CYCLES; ++i) {
			logger.info("*** "+lid+": loop {}",i);
			ensureServicesAvailable();
			gateNum = gateStats.getEventCount();
			laneNum = laneStats.getCountVehiclesPassed();
			logger.info("*** "+lid+" stats: lane vehicles passed {}, gate events {}" , laneNum, gateNum );
			try {
				Thread.sleep(PAUSE_TIME);
			}
			catch (InterruptedException e) {
				logger.warn("(!)"+lid+": thread interrupted");
				e.printStackTrace();
			}
			Thread.yield();
		}

		logger.info("(!)"+lid+": thread stopping");
		
		logger.info("*** "+lid+": FINISHED RUN");
		logger.info("-----");
		logger.info(lid+": final stats: lane events {}" , laneStats.getEventCount() );
		logger.info(lid+": final stats: lane vehicles passed {}", laneStats.getCountVehiclesPassed() );
		logger.info(lid+": final stats: gate events {}" , gateStats.getEventCount() );
		logger.info(lid+": final stats: gate entered {}", gateStats.getNumberOfVehiclesEntering() );
		logger.info(lid+": final stats: gate leaved  {}",  gateStats.getNumberOfVehiclesLeaving() );
		logger.info("-----");
	}

	
	private void ensureServicesAvailable() 
	{
		while ((gateStats == null) || (laneStats == null)) {
			logger.warn(lid+": some required service not set, waiting and trying again...");
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				logger.warn("(!)"+lid+": thread interrupted");
				e.printStackTrace();
			}
			gateStats = (gateStats == null) ? activator.getGateStatsService() : gateStats;
			laneStats = (laneStats == null) ? activator.getLaneStatsService() : laneStats;
		}
	}

}
