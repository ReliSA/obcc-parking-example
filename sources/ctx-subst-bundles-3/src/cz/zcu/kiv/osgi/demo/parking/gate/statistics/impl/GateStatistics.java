package cz.zcu.kiv.osgi.demo.parking.gate.statistics.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.zcu.kiv.osgi.demo.parking.carpark.flow.IVehicleFlow;
import cz.zcu.kiv.osgi.demo.parking.carpark.status.IParkingStatus;
import cz.zcu.kiv.osgi.demo.parking.gate.statistics.IGateStatistics;


/**
 * Extended version of GateStatistics, depends on both VehicleFlow and
 * ParkingStatus.  Simulation of traffic flow moved to TrafficLane
 * started from Gate activator.
 * 
 * @author brada
 * 
 */
public class GateStatistics implements IGateStatistics, IGateStatisticsUpdate
{
    private static GateStatistics instance;

    private Logger logger = null;
    private static final String lid = "GateStats.r3";

    // dependencies
    private IParkingStatus parkingStatus = null;

    private int entered = 0;
    private int leaved = 0;

    /**
     * Create service instance.
     */
    public static GateStatistics getInstance(IVehicleFlow parking, IParkingStatus status)
    {
        if (instance == null) {
            instance = new GateStatistics(parking, status);
        }
        return instance;
    }


    protected GateStatistics(IVehicleFlow parking, IParkingStatus status)
    {
        logger = LoggerFactory.getLogger("parking-demo");
        logger.info(getIdentification() + ": <init>");

        parkingStatus = status;
        clear();
    }

    @Override
    public void vehiclesArrived(int cntArrived)
    {
        entered += cntArrived;
        logger.info(getIdentification()+": increased new vehicles entered count by {} to {}", 
        		cntArrived, entered);
    }

    @Override
    public void vehiclesDeparted(int cntDeparted)
    {
        leaved += cntDeparted;	// FIXME can lead to inconsistent state when
                               // entered < leaved
        logger.info(getIdentification()+": increased new vehicles left count by {} to {}", 
        		cntDeparted, leaved);
    }

    @Override
    public int getNumberOfVehiclesEntering()
    {
        logger.info(getIdentification() + ": {} total vehicles entered, full? {}", entered, parkingStatus.isFull());
        return entered;
    }

    @Override
    public int getNumberOfVehiclesLeaving()
    {
        logger.info(getIdentification() + ": {} total vehicles left, full? {}", leaved, parkingStatus.isFull());
        return leaved;
    }


    @Override
    public int getEventCount()
    {
        int cnt = getNumberOfVehiclesEntering() + getNumberOfVehiclesLeaving();
        logger.info(getIdentification() + ": {} total enter/leave events", cnt);
        return cnt;
    }


    @Override
    public String getIdentification()
    {
        return lid;
    }

    @Override
    public void clear()
    {
        leaved = 0;
        entered = 0;
        logger.info(getIdentification() + ": counters cleared");
    }


}
