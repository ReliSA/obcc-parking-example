package cz.zcu.kiv.relisa.data.parking.gate.statistics;


import cz.zcu.kiv.relisa.data.parking.statsbase.ICountingStatistics;

public interface IGateStatistics extends ICountingStatistics {
    int getNumberOfVehiclesEntering();

    int getNumberOfVehiclesLeaving();
}
