package cz.zcu.kiv.osgi.demo.parking.lane.statistics.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.lane.statistics.ILaneStatistics;

public class LaneStatistics implements ILaneStatistics, ILaneUpdate
{
	private static LaneStatistics instance = null;
	private Logger logger = null;
	
	private int vehicleCount = 0;
	private int secondsElapsed = 0;
	private long timerStart = 0L;
	
	/**
     * Create service instance.
     */
	public static LaneStatistics getInstance()
	{
		if (instance == null) {
			instance = new LaneStatistics();
		}
		return instance;		
	}

	protected LaneStatistics()
	{
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(getIdentification()+" <init>");
		clear();
	}
	
	@Override
	public int getCountVehiclesPassed()
	{
		logger.info(getIdentification()+": vehicles passed count {}", vehicleCount);
		return vehicleCount;
	}
	
    @Override
    public int getVehiclesPerInterval(int seconds)
    {
        secondsElapsed = (int)((System.currentTimeMillis() - timerStart) / 1000);
        int freq;
        if (secondsElapsed == 0)
            freq = vehicleCount;
        else
            freq = (int) (1.0 * seconds / secondsElapsed * vehicleCount);
        if (freq == 0)
            freq = vehicleCount;
        logger.warn(getIdentification()+": getVehPerInterval() UNEXPECTEDLY CALLED!  (returning vehicle freq for {}-sec interval after {} secs of run time)",seconds,secondsElapsed);
        return freq;
    }

    @Override
    public String getIdentification()
    {
        return "LaneStatistics.r3";
    }

    @Override
    public int getEventCount()
    {
        logger.info(getIdentification()+": vehicles passed count {}", vehicleCount); 
        return vehicleCount;
    }

	@Override
	public void clear()
	{
		vehicleCount = 0;
		secondsElapsed = 0;
		timerStart = System.currentTimeMillis();
		logger.info(getIdentification()+": counters cleared");
	}

	@Override
    public void vehiclesPassing(int cnt)
	{
		vehicleCount += cnt;
		logger.info(getIdentification()+": increased vehicles passed count by {} to {}", cnt, vehicleCount);
	}

}
