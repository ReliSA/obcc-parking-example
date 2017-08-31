package cz.zcu.kiv.osgi.demo.parking.gate.control.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.gate.VehicleSink;
import cz.zcu.kiv.osgi.demo.parking.gate.control.IGateControl;

/**
 * Provides methods to control the gate operation, i.e. to open and close the gate.
 *  
 * @author brada
 *
 */
public class GateControl implements IGateControl {

	private static GateControl instance = null;
	private static final String lid = "GateControl.r4";

	VehicleSink sink = null;
	
	private Logger logger = null;

	/**
	 * Get singleton instance.
	 */
	public static GateControl getInstance(VehicleSink sink) {
		if (instance == null) {
			instance = new GateControl(sink);
		}
		return instance;
	}
	
	public GateControl(VehicleSink sink) {
		this.sink = sink;
		logger = LoggerFactory.getLogger("parking-demo");
		logger.info(lid+": <init>");
	}
	
	
	@Override
	public void openGate() {
		sink.setOpen(true);
		logger.info(lid + ": gate set to open");
	}

	@Override
	public void closeGate() {
		sink.setOpen(false);
		logger.info(lid + ": gate set to closed");
	}

}
