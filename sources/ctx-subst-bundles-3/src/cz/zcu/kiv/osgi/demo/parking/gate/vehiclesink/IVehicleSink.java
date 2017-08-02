package cz.zcu.kiv.osgi.demo.parking.gate.vehiclesink;

public interface IVehicleSink
{
	public void exchangeVehicles(int numIn, int numOut);
	public void setOpen(boolean open);
	public boolean isOpen();
}
