package cz.zcu.kiv.osgi.demo.parking.carpark.status;

/**
 * Evolved in this version, SPE-cialization diff.
 */
public interface IParkingStatus
{	
	public boolean isFull();
	// added in this version
	public int getCapacity();
	public int getNumFreePlaces();
	public void reset();
}
