# BookMyRoute

BookMyRoute is a full-stack bus ticket booking application with a React frontend and a Spring Boot REST API. Passengers can search routes, select seats, create bookings, view their trips, and cancel bookings. Admin users can manage routes, schedules, users, buses, and booking records.

## Tech Stack

- Frontend: React 18, Vite, Tailwind CSS, React Router, Axios
- Backend: Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
- Database: MySQL
- Authentication: JWT-based login for users and admins
- Optional AI: OpenAI-powered chatbot endpoint

## Project Structure

```text
BookMyRoute/
|-- backend/    # Spring Boot API
|-- frontend/   # React + Vite client
|-- README.md
`-- .gitignore
```

## Features

- User registration and login
- JWT-protected passenger routes
- Bus schedule search by origin, destination, date, and passengers
- Seat availability lookup
- Booking creation with passenger details and payment method
- Booking history and booking cancellation
- Admin dashboard and management views
- Route, schedule, bus, user, and booking APIs
- Chatbot widget backed by `/api/chatbot/message`

## Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+
- MySQL 8+

## Backend Setup

1. Create a MySQL database:

```sql
CREATE DATABASE bookmyroute;
```

2. Configure environment variables if you do not want to use the defaults:

```bash
DB_USERNAME=root
DB_PASSWORD=root
ADMIN_EMAIL=admin@bookmyroute.com
ADMIN_PASSWORD=Admin@12345
ADMIN_NAME=BookMyRoute Admin
OPENAI_API_KEY=
OPENAI_MODEL=gpt-5.4-mini
```

3. Start the backend:

```bash
cd backend
mvn spring-boot:run
```

The API runs on `http://localhost:8080`. Controllers are prefixed with `/api`.

## Frontend Setup

1. Install dependencies:

```bash
cd frontend
npm install
```

2. Start the Vite dev server:

```bash
npm run dev
```

The frontend runs on `http://localhost:3000` and proxies API calls through `/api`.

## Useful Commands

```bash
# Backend
cd backend
mvn spring-boot:run
mvn test

# Frontend
cd frontend
npm run dev
npm run build
npm run preview
```

## Default Admin Login

The backend bootstraps an admin account from `application.properties` when the app starts:

- Email: `admin@bookmyroute.com`
- Password: `Admin@12345`

Override these values with `ADMIN_EMAIL`, `ADMIN_PASSWORD`, and `ADMIN_NAME` in your environment before running the backend.

## Main API Routes

- `POST /api/auth/register` - register a passenger
- `POST /api/auth/login` - passenger login
- `POST /api/auth/admin/login` - admin login
- `GET /api/auth/me` - current authenticated user
- `GET /api/routes` - list routes
- `GET /api/routes/cities` - list available cities
- `GET /api/schedules/search` - search schedules
- `GET /api/schedules/{id}/seats` - list available seats
- `POST /api/bookings` - create booking
- `GET /api/bookings/my` - current user's bookings
- `PATCH /api/bookings/{bookingRef}/cancel` - cancel booking
- `GET /api/admin/dashboard` - admin dashboard data
- `GET /api/admin/bookings` - admin booking list
- `POST /api/chatbot/message` - chatbot response

## Configuration Notes

- Database settings live in `backend/src/main/resources/application.properties`.
- Hibernate is set to `spring.jpa.hibernate.ddl-auto=update`, so tables are created or updated automatically during development.
- CORS allows `http://localhost:3000` and `http://localhost:5173` by default.
- JWT settings are configured with `app.jwt.secret`, `app.jwt.expiration-ms`, and `app.jwt.refresh-expiration-ms`.
- The chatbot works only when `OPENAI_API_KEY` is provided.
