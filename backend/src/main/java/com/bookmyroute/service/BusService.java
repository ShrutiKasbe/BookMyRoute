package com.bookmyroute.service;

import com.bookmyroute.entity.Bus;
import com.bookmyroute.entity.Seat;

import java.util.List;

public interface BusService {
    Bus createBus(Bus bus);
    Bus getBusById(Long id);
    List<Bus> getAllActiveBuses();
    Bus updateBus(Long id, Bus bus);
    void deactivateBus(Long id);
    List<Seat> getSeatsByBus(Long busId);
}
