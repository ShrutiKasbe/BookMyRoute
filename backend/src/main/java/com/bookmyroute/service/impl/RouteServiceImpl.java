package com.bookmyroute.service.impl;

import com.bookmyroute.entity.Route;
import com.bookmyroute.exception.ResourceNotFoundException;
import com.bookmyroute.repository.RouteRepository;
import com.bookmyroute.service.RouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {
    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }


    private final RouteRepository routeRepository;

    @Override
    @Transactional
    public Route createRoute(Route route) {
        return routeRepository.save(route);
    }

    @Override
    @Transactional(readOnly = true)
    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> searchRoutes(String origin, String destination) {
        return routeRepository.findByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination);
    }

    @Override
    @Transactional
    public Route updateRoute(Long id, Route updated) {
        Route route = getRouteById(id);
        route.setOrigin(updated.getOrigin());
        route.setDestination(updated.getDestination());
        route.setDistanceKm(updated.getDistanceKm());
        route.setDurationMins(updated.getDurationMins());
        return routeRepository.save(route);
    }
}
