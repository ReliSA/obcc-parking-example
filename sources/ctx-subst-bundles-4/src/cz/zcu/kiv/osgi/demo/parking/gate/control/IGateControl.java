package cz.zcu.kiv.osgi.demo.parking.gate.control;

/**
 * Enables gate opening and closing.
 * 
 * @author brada
 *
 */
public interface IGateControl {
	public void openGate() throws IllegalStateException;		// @pre: gate is closed, @post: gate is open
	public void closeGate() throws IllegalStateException;	// @pre: gate is open, @post: gate is closed
}
