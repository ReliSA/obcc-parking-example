package cz.zcu.kiv.osgi.demo.parking.gate;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.carpark.flow.IVehicleFlow;
import cz.zcu.kiv.osgi.demo.parking.carpark.status.IParkingStatus;
import cz.zcu.kiv.osgi.demo.parking.gate.statistics.impl.GateStatistics;
import cz.zcu.kiv.osgi.demo.parking.gate.statistics.IGateStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.statistics.ILaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.statistics.impl.LaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.statsbase.ICountingStatistics;


public class GateActivator implements BundleActivator
{

    private Logger logger;
    private static final String lid = "Gate.r3 Activator";

    private ServiceRegistration gateSvcReg;
    private ServiceRegistration laneSvcReg;

    // dependencies
    private IVehicleFlow parking = null;
    private IParkingStatus status = null;

    
    public GateActivator()
    {
        this.logger = LoggerFactory.getLogger("parking-demo");
    }

    
    @Override
    public void start(BundleContext context) throws Exception
    {
        logger.info(lid + ": starting");

        // required services

        ServiceReference sr;
        sr = context.getServiceReference(IVehicleFlow.class.getName());
        if (sr == null) {
            logger.error(lid + ": no parking service registered");
        }
        else {
            parking = (IVehicleFlow) context.getService(sr);
            if (parking == null) {
                logger.error(lid + ": no parking service available");
            }
            else {
                logger.info(lid + ": got parking service");
            }
        }
        sr = context.getServiceReference(IParkingStatus.class.getName());
        if (sr == null) {
            logger.error(lid + ": no parking registered");
        }
        else {
            status = (IParkingStatus) context.getService(sr);
            if (status == null) {
                logger.error(lid + ": no parking service available");
            }
            else {
                logger.info(lid + ": got parking service");
            }
        }

        if ((parking == null) || (status == null)) {
            logger.error(lid + ": parking and/or status service unavailable, exiting");
            throw new BundleException(lid + ": parking and/or status service unavailable, exiting");
        }

        // provided services

        GateStatistics gateStatsImpl = GateStatistics.getInstance(parking, status);
        LaneStatistics laneStatsImpl = LaneStatistics.getInstance();
        VehicleSink sinkImpl = VehicleSink.getInstance(parking,gateStatsImpl);
        
        String[] gateIds = new String[] {
                ICountingStatistics.class.getName(),
                IGateStatistics.class.getName()
        };
        gateSvcReg = context.registerService(gateIds, gateStatsImpl, null);
        if (null == gateSvcReg)
            throw new ServiceException(lid + ": gate svc registration failed");
        logger.info(lid + ": registered gate svc {}", context.getService(gateSvcReg.getReference()).getClass());

        String[] laneIds = new String[] {
                ICountingStatistics.class.getName(),
                ILaneStatistics.class.getName()
        };
        laneSvcReg = context.registerService(laneIds, laneStatsImpl, null);
        if (null == laneSvcReg)
            throw new ServiceException(lid + ": lane svc registration failed");
        logger.info(lid + ": registered lane svc {}", context.getService(laneSvcReg.getReference()).getClass());

        // start traffic simulator ('coz lane stats still provided by this
        // bundle, not the already added TrafficLane)
        TrafficSimulation lane = new TrafficSimulation(sinkImpl, laneStatsImpl);
        Thread t = new Thread(lane);
        logger.info("(!) " + lid + ": spawning traffic lane thread");
        t.start();
        logger.info("(!) " + lid + ": traffic lane thread spawned");
    }

    
    @Override
    public void stop(BundleContext context) throws Exception
    {
        logger.info(lid + ": stopping");
        gateSvcReg.unregister();
        logger.info(lid + ": unreg gate svc");
        laneSvcReg.unregister();
        logger.info(lid + ": unreg lane svc");
        logger.info(lid + ": stopped.");
    }

}
