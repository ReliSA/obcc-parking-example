package cz.zcu.kiv.osgi.demo.parking.carpark.flow.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.carpark.flow.IVehicleFlow;
import cz.zcu.kiv.osgi.demo.parking.carpark.status.impl.ParkingStatus;


/**
 * TODO - what does this mean? >> The implementation of VehicleSink in Gate substitutes a missing vehicle departure thread 
 * in car park by a 50:50 chance of departure on consumeVehicle(), invoking this.leave().
 * 
 * @author brada
 *
 */
public class VehicleFlow implements IVehicleFlow
{
	
	private static VehicleFlow instance = null;
	private Logger logger = null;
	private static final String lid = "VehicleFlow.r5";
	
	private ParkingStatus status = null;
		
	/** 
	 * Return singleton instance.
	 */
	public static IVehicleFlow getInstance()
	{
		if (instance == null) {
			instance = new VehicleFlow();
		}
		return instance;		
	}

	
	protected VehicleFlow()
	{
		this.logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		this.status = (ParkingStatus) ParkingStatus.getInstance();
	}	

	
	@Override
	public void arrive() throws IllegalStateException
	{
		if (status.isFull()) {
			logger.error(lid+": arrive(): No places left free for parking");
			throw new IllegalStateException("No places left free for parking");
		}
		logger.info(lid+": arrive");
		status.decreaseFreePlaces(1);		
	}

	@Override
	public void leave() throws IllegalStateException
	{
		if (status.getNumFreePlaces() == status.getCapacity()) {
			logger.error(lid+": leave(): No car can leave an empty car park");
			throw new IllegalStateException("No car can leave an empty car park");
		}
		logger.info(lid+": leave");
		status.increaseFreePlaces(1);
	}

}
