package cz.zcu.kiv.osgi.demo.parking.carpark.statistics.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.carpark.statistics.IParkingStatistics;
import cz.zcu.kiv.osgi.demo.parking.statsbase.CountingStatisticsAbstractBaseImpl;

public class ParkingStatistics extends CountingStatisticsAbstractBaseImpl 
	implements IParkingStatistics
{
	private static ParkingStatistics instance = null;
	private Logger logger;
	private static final String lid = "ParkingStatistics.r5";

	int cntArrived;
	int cntDeparted;
	
	public ParkingStatistics()
	{
		super();
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		clear();
	}
	
	/**
	 * Get singleton instance.
	 * 
	 */
	public static ParkingStatistics getInstance()
	{
		if (instance == null) {
			instance = new ParkingStatistics();
		}
		return instance;
	}
	
	@Override
	public String getIdentification()
	{
		return lid;
	}

	@Override
	public int getCountVehiclesArrived()
	{
		logger.info(lid+": getArrived {}",cntArrived);
		return cntArrived;
	}
	
	public void vehiclesArrived(int cnt)
	{
		logger.info(lid+": newly arrived {}",cnt);
		cntArrived += cnt;
		addToEventCount(cnt);
	}

	@Override
	public int getCountVehiclesDeparted()
	{
		logger.info(lid+": getDeparted {}",cntDeparted);
		return cntDeparted;
	}
	
	public void vehiclesDeparted(int cnt)
	{
		logger.info(lid+": newly departed {}",cnt);
		cntDeparted += cnt;
		addToEventCount(cnt);
	}

	@Override
	public void clear()
	{
		cntArrived = 0;
		cntDeparted = 0;
	}

}
