package cz.zcu.kiv.osgi.demo.parking.lane;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.gate.statistics.IGateStatistics;

import cz.zcu.kiv.osgi.demo.parking.lane.statistics.ILaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.sign.roadsign.IRoadSign;
import cz.zcu.kiv.osgi.demo.parking.sign.roadsign.impl.RoadSign;


public class TrafficLane implements Runnable
{

    private static final int NUM_CYCLES = 10;
    private static final long PAUSE_TIME = 300;
    private static final long WAIT_TIME = 100;

    private Logger logger;
	private String lid = "TrafficLane@Lane.r5";
	
	LaneActivator activator = null;

    // dependencies
    private IGateStatistics gateStats;
    private IRoadSign sign;

    public TrafficLane(LaneActivator activator, IGateStatistics gateStats, IRoadSign sign)
    {
        logger = LoggerFactory.getLogger("parking-demo");
        logger.info("TrafficLane.r5 <init>");
        this.activator = activator;
        this.gateStats = gateStats;
        this.sign = sign;
    }

    /**
     * Periodically polls carpark statistics and makes the RoadSign show the free space info.
     */
    @Override
    public void run()
    {
        logger.info("(!) TrafficLane: thread starting");

        ensureServicesAvailable();
        
        sign.switchOn();
        
        // better replace by "while carpark gate is open"?
        for (int i = 0; i < NUM_CYCLES; ++i) {
            logger.info("TrafficLane: loop #{}", i);
            sign.showMessage("PARKING FREE " + gateStats.getNumFreePlaces());
            try {
                Thread.sleep(PAUSE_TIME);
            }
            catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Thread.yield();
        }
        
        sign.switchOff();
        
        logger.info("(!) TrafficLane: thread stopping");
    }

    
	private void ensureServicesAvailable() 
	{
		// check and wait for dependencies
		// Warning: fragile -- does not handle services disappearing during runtime
		while ((gateStats == null) || (sign == null)) {
			logger.warn(lid+": some required service not set, waiting and trying again...");
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				logger.warn("(!)"+lid+": thread interrupted");
				e.printStackTrace();
			}
			gateStats = (gateStats == null) ? activator.getGateStatsService() : gateStats;
			sign = (sign == null) ? activator.getRoadSignService() : sign;
		}
		
	}

}
