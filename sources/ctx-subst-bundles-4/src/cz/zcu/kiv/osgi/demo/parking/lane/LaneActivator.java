package cz.zcu.kiv.osgi.demo.parking.lane;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.lane.statistics.ILaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.statistics.impl.LaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.status.ILaneStatus;
import cz.zcu.kiv.osgi.demo.parking.lane.status.impl.LaneStatus;
import cz.zcu.kiv.osgi.demo.parking.statsbase.ICountingStatistics;

public class LaneActivator implements BundleActivator
{
	
	private Logger logger;
	private static final String lid = "LaneActivator.r4";
	
	private ServiceRegistration laneStatisReg;
	private ServiceRegistration laneStatusReg;
	
	public LaneActivator()
	{
		this.logger = LoggerFactory.getLogger("parking-demo");
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		logger.info(lid + ": starting");
		
		// get requirements
		
		ILaneStatistics laneStatisImpl = LaneStatistics.getInstance();
        String[] laneIds = new String[] {
                ICountingStatistics.class.getName(),
                ILaneStatistics.class.getName()
        };
        laneStatisReg = context.registerService(laneIds, laneStatisImpl, null);
        if (null == laneStatisReg)
            throw new ServiceException(lid + ": lane svc registration failed");
        logger.info(lid + ": registered lane statistics svc {}", context.getService(laneStatisReg.getReference()).getClass());
		
        ILaneStatus laneStatusImpl = LaneStatus.getInstance();
        laneIds = new String[] {
                ILaneStatus.class.getName()
        };
        laneStatusReg = context.registerService(laneIds, laneStatusImpl, null);
        if (null == laneStatisReg)
            throw new ServiceException(lid + ": lane status svc registration failed");
        logger.info(lid + ": registered lane status svc {}", context.getService(laneStatusReg.getReference()).getClass());

        // do startup sequence
        
        laneStatisImpl.clear();

		logger.info(lid + ": started");
	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		logger.info(lid + ": stoping");
		
        laneStatisReg.unregister();
        logger.info(lid + ": unreg lane statistics svc");
        laneStatusReg.unregister();
        logger.info(lid + ": unreg lane status svc");

        logger.info(lid + ": stopped.");
	}

}

