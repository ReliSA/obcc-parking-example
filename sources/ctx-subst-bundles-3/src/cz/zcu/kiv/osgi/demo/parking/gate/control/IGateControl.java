package cz.zcu.kiv.osgi.demo.parking.gate.control;

/**
 * Enables gate opening and closing.
 * 
 * @author brada
 *
 */
public interface IGateControl {
	public void openGate();		// @pre: gate is closed, @post: gate is open
	public void closeGate();	// @pre: gate is open, @post: gate is closed
}
