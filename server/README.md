# Cashi Payment API Server

This is the backend service for the Cashi Payment App, implemented using Ktor framework.

## Features

- **POST /payments** - Process payment requests
- **GET /transactions** - Retrieve transaction history
- **GET /health** - Health check endpoint
- **GET /** - Server status

## API Endpoints

### POST /payments
Process a payment request.

**Request Body:**
```json
{
  "recipientEmail": "user@example.com",
  "amount": 100.50,
  "currency": "USD"
}
```

**Response (Success - 201 Created):**
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "payment": {
    "id": "1",
    "recipientEmail": "user@example.com",
    "amount": 100.50,
    "currency": "USD",
    "timestamp": "2024-01-01T12:00:00Z"
  }
}
```

**Response (Error - 400 Bad Request):**
```json
{
  "success": false,
  "message": "Payment validation failed",
  "error": "Invalid email format"
}
```

### GET /transactions
Retrieve all transactions.

**Response (200 OK):**
```json
{
  "success": true,
  "transactions": [
    {
      "id": "1",
      "recipientEmail": "user@example.com",
      "amount": 100.50,
      "currency": "USD",
      "timestamp": "2024-01-01T12:00:00Z"
    }
  ]
}
```

### GET /health
Health check endpoint.

**Response (200 OK):**
```json
{
  "status": "healthy",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

## Running the Server

### Development Mode
```bash
./gradlew :server:run
```

### Production Mode
```bash
./gradlew :server:run --no-daemon
```

The server will start on `http://localhost:8080`

## Testing

Run the tests:
```bash
./gradlew :server:test
```

## Architecture

- **Application.kt** - Main server setup and routing
- **PaymentService.kt** - Business logic for payment processing
- **Validation** - Uses shared validation logic from the KMP module

## Data Storage

Currently uses in-memory storage for demo purposes. In a production environment, this would be replaced with:
- Database (PostgreSQL, MySQL)
- Message queue (RabbitMQ, Kafka)
- Cache (Redis)

## Validation

The server validates:
- Email format
- Amount > 0
- Supported currencies (USD, EUR, GBP)

## Error Handling

- **400 Bad Request** - Validation errors
- **500 Internal Server Error** - Server errors

## Performance

- Concurrent payment processing
- Thread-safe data structures
- Simulated processing time (100ms)

## Security Considerations

For production, consider adding:
- Authentication/Authorization
- Rate limiting
- Input sanitization
- HTTPS
- CORS configuration 