# 🏨 Connectly (airBnbApp)

A Spring Boot backend for a hotel/room booking platform — inspired by Airbnb's booking flow, built with secure JWT authentication, dynamic pricing strategies, and Stripe payment integration.

![Swagger UI](docs/swagger-ui.png)

---

## ✨ Features

- **JWT-based Authentication** — Signup, login, and token refresh flow secured with Spring Security
- **Hotel & Room Management** — Admin APIs to create, update, delete, and activate hotels
- **Inventory Management** — Track room availability per hotel
- **Dynamic Pricing Engine** — Strategy pattern with pluggable pricing rules:
  - Base Pricing
  - Surge Pricing
  - Holiday Pricing
  - Occupancy-based Pricing
  - Urgency Pricing
- **Booking Flow** — Init booking → add guests → checkout → cancel
- **Stripe Payments** — Secure checkout sessions with webhook support for payment confirmation
- **Hotel Browsing & Search** — Public APIs for guests to search and view hotel info
- **Global Exception Handling** — Centralized error responses via `GlobalExceptionHandler`
- **API Documentation** — Auto-generated Swagger / OpenAPI docs

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.6 |
| Security | Spring Security, JWT (jjwt 0.12.6) |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Payments | Stripe Java SDK |
| API Docs | springdoc-openapi (Swagger UI) |
| Object Mapping | ModelMapper |
| Build Tool | Maven |

---

## 📁 Project Structure

```
com.major.project.airBnbApp
├── advice            # Global response & exception handling
├── config            # OpenAPI, Stripe, and Mapper configuration
├── controller        # REST controllers (Auth, Hotel, Booking, Webhook...)
├── dto               # Request/response data transfer objects
├── entity            # JPA entities (Hotel, Room, Booking, Guest, User...)
│   └── enums
├── exception         # Custom exceptions
├── repository        # Spring Data JPA repositories
├── security          # JWT filter, JWT service, Auth service, Security config
├── service            # Business logic (Booking, Hotel, Room, Inventory, Checkout, User)
└── strategy          # Pricing strategy implementations
```

---

## 🔐 Authentication

All protected endpoints require a JWT in the `Authorization` header:

```
Authorization: Bearer <your_token>
```

| Endpoint | Description |
|---|---|
| `POST /auth/signup` | Register a new user |
| `POST /auth/login` | Login and receive access + refresh tokens |
| `POST /auth/refresh` | Refresh an expired access token |

---

## 📡 API Overview

### Hotel Browsing (Public)
- `GET /hotels/search` — Search hotels by filters
- `GET /hotels/{hotelId}/info` — Get hotel details

### Booking
- `POST /bookings/init` — Initialize a booking
- `POST /bookings/{bookingId}/addGuests` — Add guests to a booking
- `POST /bookings/{bookingId}/payments` — Trigger Stripe checkout
- `POST /bookings/{bookingId}/cancel` — Cancel a booking

### Admin — Hotel Management
- `POST /admin/hotels` — Create a hotel
- `GET /admin/hotels` — List all hotels
- `GET /admin/hotels/{hotelId}` — Get hotel by ID
- `PUT /admin/hotels/{hotelId}` — Update hotel
- `PATCH /admin/hotels/{hotelId}/activate` — Activate hotel
- `DELETE /admin/hotels/{hotelId}` — Delete hotel

### Webhooks
- `POST /webhook/payment` — Stripe payment event listener

> Full interactive API documentation is available via Swagger UI (see below).

---

## 📖 API Documentation (Swagger)

Once the application is running, explore and test all endpoints directly from your browser:

```
http://localhost:8080/swagger-ui/index.html
```

Raw OpenAPI spec:
```
http://localhost:8080/v3/api-docs
```

Use the **Authorize 🔒** button in Swagger UI to attach your JWT and test secured endpoints right from the docs.

---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL running locally (or accessible instance)
- A Stripe account (test mode API keys)

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/airBnbApp.git
cd airBnbApp
```

### 2. Configure `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/airbnb_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

jwt.secret=your_jwt_secret_key
jwt.expiration=your_token_expiry_ms

stripe.secret.key=your_stripe_secret_key
```

> ⚠️ Never commit real secrets. Use environment variables or a `.env` file excluded via `.gitignore` for production.

### 3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

---

## 🧩 Design Highlights

- **Strategy Pattern for Pricing** — `PricingStrategy` interface with multiple implementations (`SurgePricingStrategy`, `HolidayPricingStrategy`, `OccupancyPricingStrategy`, `UrgencyPricingStrategy`) composed via `PricingService`, allowing new pricing rules to be added without touching existing logic.
- **Stateless JWT Security** — Custom `JWTAuthFilter` validates tokens per-request; no server-side session state.
- **Centralized Error Handling** — `GlobalExceptionHandler` + `ApiError`/`ApiResponse` wrappers ensure consistent API responses across the app.
- **Stripe Webhook Integration** — Payment confirmations are handled asynchronously via Stripe webhook events rather than relying solely on client-side confirmation.

---

## 🗺️ Roadmap

- [ ] Add role-based access control (Guest / Hotel Owner / Admin)
- [ ] Add unit & integration test coverage
- [ ] Dockerize the application
- [ ] Add CI/CD pipeline (GitHub Actions)
- [ ] Rate limiting on public search endpoints

---

## 👤 Author

**Bhavnish Bhardwaj**
- LinkedIn: [linkedin.com/in/bhavnishbhardwaj](https://linkedin.com/in/bhavnishbhardwaj)
- Portfolio: [portfolio-bhavnish15.vercel.app](https://portfolio-bhavnish15.vercel.app)

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
