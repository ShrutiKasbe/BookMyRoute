package com.bookmyroute.service.impl;

import com.bookmyroute.dto.request.ScheduleSearchRequest;
import com.bookmyroute.dto.response.ScheduleResponse;
import com.bookmyroute.entity.Bus;
import com.bookmyroute.entity.Schedule;
import com.bookmyroute.entity.Seat;
import com.bookmyroute.enums.SeatType;
import com.bookmyroute.exception.ResourceNotFoundException;
import com.bookmyroute.repository.ScheduleRepository;
import com.bookmyroute.repository.RouteReviewRepository;
import com.bookmyroute.repository.SeatRepository;
import com.bookmyroute.service.ScheduleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
                               SeatRepository seatRepository,
                               RouteReviewRepository routeReviewRepository) {
        this.scheduleRepository = scheduleRepository;
        this.seatRepository = seatRepository;
        this.routeReviewRepository = routeReviewRepository;
    }


    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final RouteReviewRepository routeReviewRepository;

    @Override
    @Transactional
    public Schedule createSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule getScheduleById(Long id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", id));
    }

    @Override
    @Transactional
    public Schedule updateSchedule(Long id, Schedule updated) {
        Schedule schedule = getScheduleById(id);
        schedule.setBus(updated.getBus());
        schedule.setRoute(updated.getRoute());
        schedule.setDepartureTime(updated.getDepartureTime());
        schedule.setArrivalTime(updated.getArrivalTime());
        schedule.setBaseFare(updated.getBaseFare());
        schedule.setAvailableSeats(updated.getAvailableSeats());
        schedule.setIsActive(updated.getIsActive());
        return scheduleRepository.save(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse.Search> searchSchedules(ScheduleSearchRequest req) {
        LocalDateTime from = req.getTravelDate().atStartOfDay();
        LocalDateTime to   = req.getTravelDate().atTime(LocalTime.MAX);

        return scheduleRepository.searchSchedules(
                req.getOrigin(), req.getDestination(), from, to, req.getSeats()
        ).stream().map(this::toSearchDto).toList();
    }

    @Override
    @Transactional
    public List<ScheduleResponse.SeatInfo> getAvailableSeats(Long scheduleId) {
        Schedule schedule = getScheduleById(scheduleId);
        ensureSeatsExist(schedule.getBus());
        Long busId = schedule.getBus().getId();
        return seatRepository.findAvailableSeatsBySchedule(busId, scheduleId)
                .stream().map(this::toSeatInfo).toList();
    }

    @Override
    @Transactional
    public void deactivateSchedule(Long id) {
        Schedule s = getScheduleById(id);
        s.setIsActive(false);
        scheduleRepository.save(s);
    }

    private ScheduleResponse.Search toSearchDto(Schedule s) {
        Long routeId = s.getRoute().getId();
        long reviewCount = routeReviewRepository.countByRouteId(routeId);
        double averageRating = reviewCount == 0
                ? 0.0
                : Math.round(routeReviewRepository.getAverageRatingByRouteId(routeId) * 10.0) / 10.0;

        return ScheduleResponse.Search.builder()
                .scheduleId(s.getId())
                .routeId(routeId)
                .origin(s.getRoute().getOrigin())
                .destination(s.getRoute().getDestination())
                .departureTime(s.getDepartureTime())
                .arrivalTime(s.getArrivalTime())
                .baseFare(s.getBaseFare())
                .availableSeats(s.getAvailableSeats())
                .busName(s.getBus().getBusName())
                .busType(s.getBus().getBusType())
                .amenities(s.getBus().getAmenities())
                .durationMins(s.getRoute().getDurationMins())
                .routeAverageRating(averageRating)
                .routeReviewCount(reviewCount)
                .build();
    }

    private ScheduleResponse.SeatInfo toSeatInfo(Seat seat) {
        return ScheduleResponse.SeatInfo.builder()
                .seatId(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .seatType(seat.getSeatType())
                .build();
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
