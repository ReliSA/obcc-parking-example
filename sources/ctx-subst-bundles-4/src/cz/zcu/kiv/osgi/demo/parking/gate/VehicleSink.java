package cz.zcu.kiv.osgi.demo.parking.gate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.gate.statistics.impl.GateStatistics;
import cz.zcu.kiv.osgi.demo.parking.gate.statistics.impl.IGateStatisticsUpdate;
import cz.zcu.kiv.osgi.demo.parking.carpark.flow.IVehicleFlow;


/**
 * VehicleSink is the "gate" between the road lane (TrafficLane) and the parking lot (VehicleFlow).
 * 
 * @author brada
 *
 */
public class VehicleSink
{

	private static VehicleSink instance = null;
	private Logger logger = null;
	private static final String lid = "VehicleSink.r3";
	
    private IGateStatisticsUpdate gate;
    private boolean isOpen;
    
	// dependencies
	private IVehicleFlow parkingPlace = null;
	
	/** 
	 * Create singleton instance.
	 */
	public static VehicleSink getInstance(IVehicleFlow flow, GateStatistics gate) 
	{
		if (instance == null) {
			instance = new VehicleSink(flow, gate);
		}
		return instance;
	}
	
	
	protected VehicleSink(IVehicleFlow flow, IGateStatisticsUpdate gate)
	{
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
		parkingPlace = flow;
		this.gate = gate;
		this.isOpen = false;
	}
	

	public void exchangeVehicles(int numIn, int numOut)
	{
		// precondition check
		if (! isOpen) {
			throw new IllegalStateException(lid+": Gate not open, cannot let vehicles in/out");
		}
		logger.info(lid+": about to exchange {}/{} in/out vehicles at gate", numIn, numOut);
		// simulate vehicle departure
		for (int i=0; i<numOut; ++i) {
			parkingPlace.leave();
		}
		gate.vehiclesDeparted(numOut);
		// simulate vehicle arrival
		for (int i=0; i<numIn; ++i) {
			parkingPlace.arrive();
		}
		gate.vehiclesArrived(numIn);
	}


	public void setOpen(boolean open) {
		this.isOpen = open;
		logger.info(lid+": is now {}", isOpen ? "open" : "closed");
	}


	public boolean isOpen() {
		return this.isOpen;
	}

}
