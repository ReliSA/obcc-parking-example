package cz.zcu.kiv.osgi.demo.parking.carpark.status.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.carpark.status.IParkingStatus;


/**
 * Evolved in this version, see IParkingStatus: SPE-cialization diff.
 */
public class ParkingStatus implements IParkingStatus, IParkingStatusUpdate
{
	private static final int CARPARK_CAPACITY = 10;
	
	private static ParkingStatus instance = null;
	private Logger logger = null;
	private static final String lid = "ParkingStatus.r5";
	
	private int numPlacesFree;
	
	/** 
	 * Provide singleton instance.
	 */
	public static IParkingStatus getInstance() 
	{
		if (instance == null) {
			instance = new ParkingStatus();
		}
		return instance;
	}
	
	
	protected ParkingStatus()
	{
		this.logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		this.reset();
	}	

	@Override
	public boolean isFull()
	{
		boolean isFull = (getNumFreePlaces() <= 0);
		logger.info(lid+": isFull {} (free {} places)", isFull, getNumFreePlaces());
		return isFull;
	}
	
	@Override
	public int getCapacity()
	{
		return CARPARK_CAPACITY;
	}

	@Override
	public int getNumFreePlaces()
	{
		return numPlacesFree;
	}

	@Override
	public void decreaseFreePlaces(int amount)
	{
		if (amount > numPlacesFree)
			numPlacesFree = 0;
		else
			numPlacesFree -= amount;
		logger.info(lid+": decreased free places by {} to {}", amount, numPlacesFree);
	}
	
	@Override
	public void increaseFreePlaces(int amount)
	{
		numPlacesFree += amount;
		if (numPlacesFree > CARPARK_CAPACITY)
			numPlacesFree = CARPARK_CAPACITY;
		logger.info(lid+": increased free places by {} to {}", amount, numPlacesFree);
	}

	@Override
	public void reset()
	{
		numPlacesFree = CARPARK_CAPACITY;
		logger.info(lid+": reset");
	}

}
