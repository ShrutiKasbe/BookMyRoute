package com.bookmyroute.controller;

import com.bookmyroute.dto.response.ApiResponse;
import com.bookmyroute.entity.Bus;
import com.bookmyroute.entity.Seat;
import com.bookmyroute.service.BusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Bus>> createBus(@RequestBody Bus bus) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(busService.createBus(bus), "Bus created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Bus>>> getAllBuses() {
        return ResponseEntity.ok(ApiResponse.success(busService.getAllActiveBuses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Bus>> getBus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(busService.getBusById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Bus>> updateBus(@PathVariable Long id,
                                                      @RequestBody Bus bus) {
        return ResponseEntity.ok(ApiResponse.success(busService.updateBus(id, bus), "Bus updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deactivateBus(@PathVariable Long id) {
        busService.deactivateBus(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Bus deactivated"));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse<List<Seat>>> getBusSeats(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(busService.getSeatsByBus(id)));
    }
}
