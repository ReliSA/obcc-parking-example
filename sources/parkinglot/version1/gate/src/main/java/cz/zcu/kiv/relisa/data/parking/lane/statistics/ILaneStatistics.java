package cz.zcu.kiv.relisa.data.parking.lane.statistics;

import cz.zcu.kiv.relisa.data.parking.statsbase.ICountingStatistics;

public interface ILaneStatistics extends ICountingStatistics {
    int getCountVehiclesPassed();
}
