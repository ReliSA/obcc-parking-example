package cz.zcu.kiv.osgi.demo.parking.gate.statistics.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.statsbase.CountingStatisticsAbstractBaseImpl;
import cz.zcu.kiv.osgi.demo.parking.carpark.flow.IVehicleFlow;
import cz.zcu.kiv.osgi.demo.parking.carpark.status.IParkingStatus;
import cz.zcu.kiv.osgi.demo.parking.gate.statistics.IGateStatistics;

/**
 * Extended version of GateStatistics, depends on both VehicleFlow and ParkingStatus.
 * 
 * @author brada
 *
 */
public class GateStatistics extends CountingStatisticsAbstractBaseImpl 
	implements IGateStatistics, IGateStatisticsUpdate, IParkingStatus
{
	private static GateStatistics instance;
	
	private Logger logger = null;
	private static final String lid = "GateStats.r4";
	
	// dependencies
	private IParkingStatus parkingStatus = null;

	private int entered = 0;
	private int leaved = 0;
	
	/** 
	 * Create service instance.
	 */
	public static GateStatistics getInstance(IVehicleFlow parking, IParkingStatus status) 
	{
		if (instance == null) {
			instance = new GateStatistics(parking, status);
		}
		return instance;
	}
	
	
	protected GateStatistics(IVehicleFlow parking, IParkingStatus status)
	{
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid + ": <init>");
		parkingStatus = status;
		clear();
	}
	
	@Override
	public void vehiclesArrived(int cntArrived)
	{
		entered += cntArrived;
		addToEventCount(cntArrived);
	    logger.info(lid + ": increased new vehicles entered count by {} to {}", 
        		cntArrived, entered);
	}
	
	@Override
	public void vehiclesDeparted(int cntDeparted)
	{
		leaved += cntDeparted;	// FIXME can lead to inconsistent state when  entered < leaved 
		addToEventCount(cntDeparted);
        logger.info(lid + ": increased new vehicles left count by {} to {}", 
        		cntDeparted, leaved);
	}
	
	@Override
	public int getNumberOfVehiclesEntering()
	{
		logger.info(getIdentification() + ": {} total vehicles entered, full? {}", entered, parkingStatus.isFull());
		return entered;
	}

	@Override
	public int getNumberOfVehiclesLeaving()
	{
		logger.info(getIdentification() + ": {} total vehicles left, full? {}", leaved, parkingStatus.isFull());
		return leaved;
	}

	@Override
	public String getIdentification()
	{
		return "GateStatistics,r4";
	}
	
	@Override
	public void clear()
	{
		super.clear();
		leaved = 0;
		entered = 0;
		parkingStatus.reset();
		logger.info(getIdentification()+": counters cleared");
	}


	@Override
	public boolean isFull()
	{
		logger.info(getIdentification()+": isFull {}", parkingStatus.isFull());
		return parkingStatus.isFull();
	}


	@Override
	public int getCapacity()
	{
		logger.info(getIdentification()+": getCapacity {}", parkingStatus.getCapacity());
		return parkingStatus.getCapacity();
	}


	@Override
	public int getNumFreePlaces()
	{
		logger.info(getIdentification()+": getNumFreePlaces {}", parkingStatus.getNumFreePlaces());
		return parkingStatus.getNumFreePlaces();
	}


	@Override
	public void reset() {
		logger.info(getIdentification()+": reset");
		parkingStatus.reset();
	}

}
