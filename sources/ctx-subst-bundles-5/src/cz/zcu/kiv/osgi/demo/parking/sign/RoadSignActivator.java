package cz.zcu.kiv.osgi.demo.parking.sign;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.sign.roadsign.IRoadSign;
import cz.zcu.kiv.osgi.demo.parking.sign.roadsign.impl.RoadSign;


public class RoadSignActivator implements BundleActivator
{

    private Logger logger;
    private static final String lid = "RoadSignActivator.r5";
    
    private ServiceRegistration roadSignReg;

    public RoadSignActivator()
    {
        this.logger = LoggerFactory.getLogger("parking-demo");
    }

    @Override
    public void start(BundleContext context) throws Exception
    {
        logger.info("RoadSign.r1 activator: start");
        
		// register provided service
		
		IRoadSign roadSignImpl = RoadSign.getInstance();
        String[] laneIds = new String[] {
                IRoadSign.class.getName()
        };
        roadSignReg = context.registerService(laneIds, roadSignImpl, null);
        if (null == roadSignReg)
            throw new ServiceException(lid + ": lane svc registration failed");
        logger.info(lid + ": registered lane statistics svc {}", context.getService(roadSignReg.getReference()).getClass());
    }

    @Override
    public void stop(BundleContext context) throws Exception
    {
        logger.info("RoadSign activator: stop");
        roadSignReg.unregister();
    }

}
