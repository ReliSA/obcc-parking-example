package cz.zcu.kiv.osgi.demo.parking.carpark;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;

import cz.zcu.kiv.osgi.demo.parking.carpark.flow.IVehicleFlow;
import cz.zcu.kiv.osgi.demo.parking.carpark.flow.impl.VehicleFlow;
import cz.zcu.kiv.osgi.demo.parking.carpark.status.IParkingStatus;
import cz.zcu.kiv.osgi.demo.parking.carpark.status.impl.ParkingStatus;

public class CarParkActivator implements BundleActivator
{
	
	private Logger logger;
	private static final String lid = "CarPark.r1 Activator";
	
	private ServiceRegistration statusSvcReg;
	private ServiceRegistration flowSvcReg;

	public CarParkActivator()
	{
		// Since the CarPark is a leaf component (not depending on any functional one), we initialize
		// the logging backend for the whole application here. 
		String lf = "[%t] %d{HH:mm:ss,SSS} %-5p - %m%n";
		// Log4J 1.2 provides a simple programmatic configuration (unlike log4j2), and the SLF4J logger picks it up.
		org.apache.log4j.BasicConfigurator.configure(new ConsoleAppender(new PatternLayout(lf)));
		this.logger = LoggerFactory.getLogger("parking-demo");
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		logger.info(lid+": starting");

		statusSvcReg = context.registerService(IParkingStatus.class.getName(), ParkingStatus.getInstance(), null);
		if (statusSvcReg == null)
			throw new ServiceException("Carpark.r1: IParkingStatus svc registration failed");
		logger.info(lid+"{}: registered svc ", context.getService(statusSvcReg.getReference()).getClass());
		
		flowSvcReg = context.registerService(IVehicleFlow.class.getName(), VehicleFlow.getInstance(), null);
		if (null == flowSvcReg) 
			throw new ServiceException("Carpark.r1: IVehicleFlow svc registration failed");
		logger.info(lid+": registered svc ", context.getService(flowSvcReg.getReference()).getClass());
		
		logger.info(lid+": started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		logger.info(lid+": stopping");
		flowSvcReg.unregister();
		logger.info(lid+": unregistered flow svc");
		statusSvcReg.unregister();
		logger.info(lid+": unregistered status svc");
		logger.info(lid+": stopped.");
	}

}
