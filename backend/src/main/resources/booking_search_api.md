# Booking Search API

## Passenger Booking Filters

`GET /api/bookings/my/search`

Requires a valid passenger JWT. The authenticated user is always resolved from the token; callers cannot request another user's bookings.

### Query parameters

| Name | Type | Required | Notes |
| --- | --- | --- | --- |
| `status` | `CONFIRMED`, `CANCELLED`, `COMPLETED`, `PENDING` | No | Filters by booking status. |
| `fromDate` | `yyyy-MM-dd` | No | Inclusive booking-created date start. |
| `toDate` | `yyyy-MM-dd` | No | Inclusive booking-created date end. |
| `page` | integer | No | Zero-based page number. Defaults to `0`. |
| `size` | integer | No | Defaults to `10`, capped at `50`. |
| `sortBy` | `bookedAt`, `departureTime`, `totalAmount`, `status`, `bookingStatus` | No | Defaults to `bookedAt`. |
| `sortDir` | `asc`, `desc` | No | Defaults to `desc`. |

### Response

Returns `ApiResponse<PagedResponse<BookingResponse>>`.

Validation errors return `400`, unauthenticated requests return `401`, and users only receive bookings owned by their authenticated account.
