package cz.zcu.kiv.relisa.data.parking.statsbase;

public interface ICountingStatistics {
    String getIdentification();

    int getEventCount();

    void clear();
}
