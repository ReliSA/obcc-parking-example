package cz.zcu.kiv.osgi.demo.parking.gate;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.carpark.flow.IVehicleFlow;
import cz.zcu.kiv.osgi.demo.parking.carpark.status.IParkingStatus;
import cz.zcu.kiv.osgi.demo.parking.gate.statistics.impl.GateStatistics;
import cz.zcu.kiv.osgi.demo.parking.gate.control.IGateControl;
import cz.zcu.kiv.osgi.demo.parking.gate.control.impl.GateControl;
import cz.zcu.kiv.osgi.demo.parking.gate.statistics.IGateStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.statistics.ILaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.statistics.impl.LaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.lane.status.ILaneStatus;
import cz.zcu.kiv.osgi.demo.parking.statsbase.ICountingStatistics;

/**
 * Gate bundle evolved, provides extended GateStatistics, removed LaneStatistics: MUT-ation diff.
 * 
 * @author brada
 *
 */
public class GateActivator implements BundleActivator
{

	private Logger logger;
	private static final String lid = "Gate.r4 Activator";

    // published services
    private ServiceRegistration gateSvcReg;
    private ServiceRegistration laneSvcReg;
    private ServiceRegistration gateCtlReg;

    // dependencies
    private IVehicleFlow parking = null;
    private IParkingStatus status = null;
    private ILaneStatus lane = null;


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
            logger.error(lid + ": no parking status registered");
        }
        else {
            status = (IParkingStatus) context.getService(sr);
            if (status == null) {
                logger.error(lid + ": no parking status service available");
            }
            else {
                logger.info(lid + ": got parking status service");
            }
        }
        
        sr = context.getServiceReference(ILaneStatus.class.getName());
        if (sr == null) {
            logger.error(lid + ": no lane status service registered");
        }
        else {
            lane = (ILaneStatus) context.getService(sr);
            if (lane == null) {
                logger.error(lid + ": no lane status service available");
            }
            else {
                logger.info(lid + ": got lane status service");
            }
        }


        if ((parking == null) || (status == null) || (lane == null)) {
            logger.error(lid + ": parking and/or status services unavailable, exiting");
            throw new BundleException(lid + ": parking and/or status services unavailable, exiting");
        }

        // provided services

        GateStatistics gateStatsImpl = GateStatistics.getInstance(parking, status);
        VehicleSink sink = VehicleSink.getInstance(parking, gateStatsImpl);
        GateControl gateCtlImpl = GateControl.getInstance(sink);
        
        String[] gateIds = new String[] {
                ICountingStatistics.class.getName(),
                IGateStatistics.class.getName()
        };
        gateSvcReg = context.registerService(gateIds, gateStatsImpl, null);
        if (null == gateSvcReg)
            throw new ServiceException(lid + ": gate svc registration failed");
        logger.info(lid + ": registered gate svc {}", context.getService(gateSvcReg.getReference()).getClass());

        String[] ctlIds = new String[] {
                IGateControl.class.getName()
        };
        gateCtlReg = context.registerService(ctlIds, gateCtlImpl, null);
        if (null == gateCtlReg)
            throw new ServiceException(lid + ": gate control svc registration failed");
        logger.info(lid + ": registered gate control svc {}", context.getService(gateCtlReg.getReference()).getClass());

        // bundle start sequence
        gateStatsImpl.clear();
        
        // start traffic simulator
        TrafficSimulation traffic = new TrafficSimulation(sink, lane);
        Thread t = new Thread(traffic, "traffic");
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
        gateCtlReg.unregister();
        logger.info(lid + ": unreg gate ctl svc");
        logger.info(lid + ": stopped.");
	}

}
