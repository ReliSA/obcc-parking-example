package cz.zcu.kiv.osgi.demo.parking.lane;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.gate.statistics.IGateStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.statistics.ILaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.statistics.impl.LaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.status.ILaneStatus;
import cz.zcu.kiv.osgi.demo.parking.lane.status.impl.LaneStatus;
import cz.zcu.kiv.osgi.demo.parking.sign.roadsign.IRoadSign;
import cz.zcu.kiv.osgi.demo.parking.statsbase.ICountingStatistics;

public class LaneActivator implements BundleActivator
{

    private Logger logger;
	private static final String lid = "LaneActivator.r5";
	private BundleContext context = null;
	
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
		this.context = context;
		
		// get requirements
		
		IGateStatistics gateStats = getGateStatsService();
		IRoadSign sign = getRoadSignService();

		// register provided services
		
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

        // start traffic simulator
        TrafficLane lane = new TrafficLane(this, gateStats, sign);
        Thread t = new Thread(lane, "sign");
        logger.info("(!) " + lid + ": spawning traffic lane sign thread");
        t.start();
        logger.info("(!) " + lid + ": traffic lane sign thread spawned");	


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

	
	IGateStatistics getGateStatsService() {
		IGateStatistics res = null;
		ServiceReference sr;
		sr = context.getServiceReference(IGateStatistics.class.getName());
        if (sr == null) {
            logger.warn(lid + ": no gate statistics registered");
        }
        else {
        	res = (IGateStatistics) context.getService(sr);
            if (res == null) {
            	logger.warn(lid + ": no gate statistics service available");
            }
            else {
            	logger.info(lid + ": got gate statistics service");
            }
        }
        return res;
	}

	
	IRoadSign getRoadSignService() {
		IRoadSign res = null;
		ServiceReference sr;
		sr = context.getServiceReference(IRoadSign.class.getName());
        if (sr == null) {
            logger.warn(lid + ": no road sign registered");
        }
        else {
        	res = (IRoadSign) context.getService(sr);
            if (res == null) {
            	logger.warn(lid + ": no road sign service available");
            }
            else {
            	logger.info(lid + ": got road sign service");
            }
        }
        return res;
	}

}
