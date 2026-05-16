package com.bookmyroute.service.impl;

import com.bookmyroute.entity.Bus;
import com.bookmyroute.entity.Seat;
import com.bookmyroute.enums.SeatType;
import com.bookmyroute.exception.ResourceNotFoundException;
import com.bookmyroute.repository.BusRepository;
import com.bookmyroute.repository.SeatRepository;
import com.bookmyroute.service.BusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        Bus saved = busRepository.save(bus);
        ensureSeatsExist(saved);
        return saved;
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

    private void ensureSeatsExist(Bus bus) {
        List<Seat> existingSeats = seatRepository.findAllByBusId(bus.getId());
        if (existingSeats.size() >= bus.getTotalSeats()) {
            return;
        }

        Set<String> existingSeatNumbers = existingSeats.stream()
                .map(Seat::getSeatNumber)
                .collect(Collectors.toSet());

        List<Seat> seatsToCreate = new ArrayList<>();
        for (int i = 1; i <= bus.getTotalSeats(); i++) {
            String seatNumber = "S" + i;
            if (existingSeatNumbers.contains(seatNumber)) {
                continue;
            }

            seatsToCreate.add(Seat.builder()
                    .bus(bus)
                    .seatNumber(seatNumber)
                    .seatType(resolveSeatType(i))
                    .build());
        }

        if (!seatsToCreate.isEmpty()) {
            seatRepository.saveAll(seatsToCreate);
        }
    }

    private SeatType resolveSeatType(int seatIndex) {
        return seatIndex % 4 == 1 || seatIndex % 4 == 0
                ? SeatType.WINDOW
                : SeatType.AISLE;
    }
}
