# Route Rating & Reviews API

All responses use the existing `ApiResponse<T>` envelope.

## Authenticated Passenger APIs

### Submit Review
`POST /api/bookings/reviews`

Backward-compatible alias: `POST /api/reviews`

Body:
```json
{
  "bookingId": 101,
  "rating": 5,
  "comment": "Clean bus and punctual arrival."
}
```

Rules:
- JWT required.
- Booking must belong to the current user.
- Booking status must be `COMPLETED`.
- Only one review is allowed per booking.
- Rating must be between 1 and 5.
- Comment is optional and limited to 1000 characters.

### Update Review
`PUT /api/bookings/reviews/{reviewId}`

Backward-compatible alias: `PUT /api/reviews/{reviewId}`

Body:
```json
{
  "rating": 4,
  "comment": "Good trip overall."
}
```

Only the review owner can update it.

### Delete Review
`DELETE /api/bookings/reviews/{reviewId}`

Backward-compatible alias: `DELETE /api/reviews/{reviewId}`

Only the review owner can delete it.

### Get My Booking Review
`GET /api/bookings/reviews/booking/{bookingId}`

Backward-compatible alias: `GET /api/reviews/booking/{bookingId}`

Returns the authenticated user's review for a booking.

## Public Route Review APIs

### Route Reviews
`GET /api/reviews/routes/{routeId}?page=0&size=5`

Returns paginated reviews, latest first.

### Route Rating Summary
`GET /api/reviews/routes/{routeId}/summary`

Returns:
```json
{
  "routeId": 1,
  "averageRating": 4.7,
  "reviewCount": 156
}
```

## Extended Existing Responses

`GET /api/schedules/search` now includes:
- `routeId`
- `routeAverageRating`
- `routeReviewCount`

`GET /api/bookings/my` now includes:
- `routeId`
- `canReview`
- `reviewed`
- `reviewId`
