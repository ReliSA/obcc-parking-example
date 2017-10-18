package cz.zcu.kiv.osgi.demo.parking.carpark.status;

/**
 * No change in this revision.
 */
public interface IParkingStatus
{	
	public boolean isFull();
	public int getCapacity();
	public int getNumFreePlaces();
	public void reset();
}
