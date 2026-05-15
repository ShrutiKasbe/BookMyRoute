package com.bookmyroute.service.impl;

import com.bookmyroute.entity.Bus;
import com.bookmyroute.entity.Seat;
import com.bookmyroute.exception.ResourceNotFoundException;
import com.bookmyroute.repository.BusRepository;
import com.bookmyroute.repository.SeatRepository;
import com.bookmyroute.service.BusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BusServiceImpl implements BusService {
    public BusServiceImpl(BusRepository busRepository, SeatRepository seatRepository) {
        this.busRepository = busRepository;
        this.seatRepository = seatRepository;
    }


    private final BusRepository busRepository;
    private final SeatRepository seatRepository;

    @Override
    @Transactional
    public Bus createBus(Bus bus) {
        return busRepository.save(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public Bus getBusById(Long id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> getAllActiveBuses() {
        return busRepository.findAllByIsActiveTrue();
    }

    @Override
    @Transactional
    public Bus updateBus(Long id, Bus updated) {
        Bus bus = getBusById(id);
        bus.setBusName(updated.getBusName());
        bus.setBusType(updated.getBusType());
        bus.setAmenities(updated.getAmenities());
        return busRepository.save(bus);
    }

    @Override
    @Transactional
    public void deactivateBus(Long id) {
        Bus bus = getBusById(id);
        bus.setIsActive(false);
        busRepository.save(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Seat> getSeatsByBus(Long busId) {
        return seatRepository.findAllByBusId(busId);
    }
}
