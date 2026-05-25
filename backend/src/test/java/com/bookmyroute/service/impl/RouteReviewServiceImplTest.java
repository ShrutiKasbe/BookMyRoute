package com.bookmyroute.service.impl;

import com.bookmyroute.dto.request.RouteReviewRequest;
import com.bookmyroute.entity.Booking;
import com.bookmyroute.entity.Route;
import com.bookmyroute.entity.Schedule;
import com.bookmyroute.entity.User;
import com.bookmyroute.enums.BookingStatus;
import com.bookmyroute.exception.BusinessException;
import com.bookmyroute.repository.BookingRepository;
import com.bookmyroute.repository.RouteRepository;
import com.bookmyroute.repository.RouteReviewRepository;
import com.bookmyroute.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouteReviewServiceImplTest {

    @Mock
    private RouteReviewRepository routeReviewRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RouteReviewServiceImpl routeReviewService;

    @Test
    void submitReviewCreatesReviewForCompletedOwnedBooking() {
        User user = user(1L, "passenger@example.com");
        Route route = route(10L);
        Booking booking = booking(20L, user, route, BookingStatus.COMPLETED);
        RouteReviewRequest request = request(20L, 5, " Smooth ride ");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(20L)).thenReturn(Optional.of(booking));
        when(routeReviewRepository.existsByBookingId(20L)).thenReturn(false);
        when(routeReviewRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        routeReviewService.submitReview(request, user.getEmail());

        ArgumentCaptor<com.bookmyroute.entity.RouteReview> captor =
                ArgumentCaptor.forClass(com.bookmyroute.entity.RouteReview.class);
        verify(routeReviewRepository).save(captor.capture());
        assertThat(captor.getValue().getBooking()).isEqualTo(booking);
        assertThat(captor.getValue().getRoute()).isEqualTo(route);
        assertThat(captor.getValue().getUser()).isEqualTo(user);
        assertThat(captor.getValue().getRating()).isEqualTo(5);
        assertThat(captor.getValue().getComment()).isEqualTo("Smooth ride");
    }

    @Test
    void submitReviewRejectsNonCompletedBooking() {
        User user = user(1L, "passenger@example.com");
        Booking booking = booking(20L, user, route(10L), BookingStatus.CONFIRMED);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(20L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> routeReviewService.submitReview(request(20L, 4, null), user.getEmail()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Only completed journeys can be reviewed");
    }

    @Test
    void submitReviewRejectsDuplicateBookingReview() {
        User user = user(1L, "passenger@example.com");
        Booking booking = booking(20L, user, route(10L), BookingStatus.COMPLETED);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(bookingRepository.findById(20L)).thenReturn(Optional.of(booking));
        when(routeReviewRepository.existsByBookingId(20L)).thenReturn(true);

        assertThatThrownBy(() -> routeReviewService.submitReview(request(20L, 4, null), user.getEmail()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("A review has already been submitted for this booking");
    }

    private RouteReviewRequest request(Long bookingId, Integer rating, String comment) {
        RouteReviewRequest request = new RouteReviewRequest();
        request.setBookingId(bookingId);
        request.setRating(rating);
        request.setComment(comment);
        return request;
    }

    private User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setName("Passenger One");
        user.setEmail(email);
        return user;
    }

    private Route route(Long id) {
        Route route = new Route();
        route.setId(id);
        route.setOrigin("Pune");
        route.setDestination("Mumbai");
        return route;
    }

    private Booking booking(Long id, User user, Route route, BookingStatus status) {
        Schedule schedule = new Schedule();
        schedule.setRoute(route);

        Booking booking = new Booking();
        booking.setId(id);
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setStatus(status);
        return booking;
    }
}
