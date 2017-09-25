package cz.zcu.kiv.osgi.demo.parking.dashboard;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.lane.statistics.ILaneStatistics;
import cz.zcu.kiv.osgi.demo.parking.statsbase.ICountingStatistics;

public class DashboardActivator implements BundleActivator
{
	
	private Logger logger;
	private static final String lid = "Dashboard.r1 Activator";
	
	private Thread t;
	private BundleContext context;
			
	// service dependencies
	private ICountingStatistics gateStats = null;	// intentionally using superinterface
	private ILaneStatistics laneStats = null; 		

	private Dashboard dashboard;

	public DashboardActivator()
	{
		this.logger = LoggerFactory.getLogger("parking-demo");
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		logger.info(lid+": starting");
		
		this.context = context;
		
		// required services

		gateStats = getGateStatsService();
		laneStats = getLaneStatsService();

		if (gateStats == null || laneStats == null) {
			logger.warn(lid+": gate and/or lane stats service unavailable on bundle startup...");
		}


		// requirements ok, go ahead
		
		dashboard = new Dashboard(this, gateStats, laneStats);
		t = new Thread(dashboard, "dashboard");
		logger.info(lid+": spawning thread");
		t.start();
		logger.info(lid+": thread spawned");

		logger.info(lid+": started.");

	}

	
	@Override
	public void stop(BundleContext context) throws Exception
	{
		logger.info(lid+": stopped.");
	}

	
	ICountingStatistics getGateStatsService() 
	{
		ICountingStatistics res = null;
		ServiceReference sr;
		sr = context.getServiceReference(ICountingStatistics.class.getName());
		if (sr == null) {
			logger.warn(lid+": no gate stats service registered");
		}
		else {
			res = (ICountingStatistics) context.getService(sr);
			if (res == null) {
				logger.warn(lid+": gate stats service unavailable, exiting");
			}
			else {
				logger.info(lid+": got gate stats service");
			}
		}
		return res;
	}

	
	ILaneStatistics getLaneStatsService() 
	{
		ILaneStatistics res = null;
		ServiceReference sr;
		sr = context.getServiceReference(ILaneStatistics.class.getName());
		if (sr == null) {
			logger.warn(lid+": no lane stats service registered");
		}
		else {
			res = (ILaneStatistics) context.getService(sr);
			if (res == null) {
				logger.warn(lid+": no lane stats service available");
			}
			else {
				logger.info(lid+": got lane stats service");
			}
		}
		return res;
	}
	
}
