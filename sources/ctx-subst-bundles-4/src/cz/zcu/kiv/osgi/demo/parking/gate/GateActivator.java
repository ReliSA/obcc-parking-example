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

	private BundleContext context;
	private Logger logger;
	private static final String lid = "Gate.r4 Activator";
	
	private VehicleSink sink;

    // published services
    private ServiceRegistration gateSvcReg;
    private GateStatistics gateStatsImpl;
    private ServiceRegistration gateCtlReg;
    private GateControl gateCtlImpl;
    

	public GateActivator()
	{
		this.logger = LoggerFactory.getLogger("parking-demo");
		this.gateStatsImpl = null;
		this.gateCtlImpl = null;
		this.sink = null;
	}

	
	@Override
	public void start(BundleContext context) throws Exception
	{
		logger.info(lid + ": starting");
		this.context = context;

        // required services

		IVehicleFlow parking = getVehicleFlowService();
		IParkingStatus status = getParkingStatusService();
		ILaneStatus lane = getLaneStatusService();

        if ((parking == null) || (status == null) || (lane == null)) {
            logger.warn(lid + ": parking and/or status services unavailable on bundle startup...");
        }
        else {
        	// provided services can only be started once we have the dependencies
    		registerGateStatsSvc(parking, status);
    		registerGateCtlSvc(parking);
        }
        
        // start traffic simulator
        TrafficSimulation traffic = new TrafficSimulation(this, parking, lane, status);
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

	
	// needed by TrafficSimulation
	VehicleSink getSink() 
	{
		return this.sink;
	}

	
	void registerGateCtlSvc(IVehicleFlow parking) throws ServiceException 
	{
		if (parking == null) {
			logger.error(lid + ": some dependencies missing, cannot publish Gate Ctl service");
			throw new IllegalStateException(lid + ": some dependencies missing, cannot publish Gate Ctl service");
		}

		if (gateCtlImpl == null) {
			sink = (sink == null) ? VehicleSink.getInstance(parking, gateStatsImpl) : sink;
			gateCtlImpl = GateControl.getInstance(sink);

			String[] ctlIds = new String[] {
					IGateControl.class.getName()
			};
			gateCtlReg = context.registerService(ctlIds, gateCtlImpl, null);
			if (null == gateCtlReg)
				throw new ServiceException(lid + ": gate control svc registration failed");
			logger.info(lid + ": registered gate control svc {}", context.getService(gateCtlReg.getReference()).getClass());
		}
	}


	void registerGateStatsSvc(IVehicleFlow parking, IParkingStatus status) throws ServiceException 
	{
		if ((parking == null) || (status == null)) {
			logger.error(lid + ": some dependencies missing, cannot publish Gate Stats service");
			throw new IllegalStateException(lid + ": some dependencies missing, cannot publish Gate Stats service");
		}

		if (gateStatsImpl == null) {
			gateStatsImpl = GateStatistics.getInstance(parking, status);
        	gateStatsImpl.clear();

	        String[] gateIds = new String[] {
	                ICountingStatistics.class.getName(),
	                IGateStatistics.class.getName()
	        };
	        gateSvcReg = context.registerService(gateIds, gateStatsImpl, null);
	        if (null == gateSvcReg)
	            throw new ServiceException(lid + ": gate stats svc registration failed");
	        logger.info(lid + ": registered gate stats svc {}", context.getService(gateSvcReg.getReference()).getClass());
		}
	}
	

	ILaneStatus getLaneStatusService() 
	{
		ILaneStatus res = null;
		ServiceReference sr;
		sr = context.getServiceReference(ILaneStatus.class.getName());
        if (sr == null) {
        	logger.warn(lid + ": no lane status service registered");
        }
        else {
            res = (ILaneStatus) context.getService(sr);
            if (res == null) {
            	logger.warn(lid + ": no lane status service available");
            }
            else {
            	logger.info(lid + ": got lane status service");
            }
        }
        return res;
	}
	

	IParkingStatus getParkingStatusService() 
	{
		IParkingStatus res = null;
		ServiceReference sr;
		sr = context.getServiceReference(IParkingStatus.class.getName());
        if (sr == null) {
            logger.warn(lid + ": no parking status registered");
        }
        else {
        	res = (IParkingStatus) context.getService(sr);
            if (res == null) {
            	logger.warn(lid + ": no parking status service available");
            }
            else {
            	logger.info(lid + ": got parking status service");
            }
        }
        return res;
	}

	
	IVehicleFlow getVehicleFlowService() 
	{
		IVehicleFlow res = null;
		ServiceReference sr;
        sr = context.getServiceReference(IVehicleFlow.class.getName());
        if (sr == null) {
        	logger.warn(lid + ": no parking service registered");
        }
        else {
            res = (IVehicleFlow) context.getService(sr);
            if (res == null) {
            	logger.warn(lid + ": no parking service available");
            }
            else {
            	logger.info(lid + ": got parking service");
            }
        }
        return res;
	}

}
