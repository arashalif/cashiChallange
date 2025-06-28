# CashiChallenge Server Setup and Android Integration Guide

## Overview

This guide explains how to run the CashiChallenge backend server and connect it to the Android app. The server provides payment processing and transaction history APIs.

## Server Architecture

The server is built with:
- **Ktor** - Kotlin web framework
- **Netty** - HTTP server engine
- **Kotlinx Serialization** - JSON serialization
- **In-memory storage** - For demo purposes (can be replaced with database)

## API Endpoints

### 1. Health Check
- **GET** `/health`
- Returns server status and timestamp
- Response: `{"status":"healthy","timestamp":"2024-01-01T12:00:00Z"}`

### 2. Process Payment
- **POST** `/payments`
- Processes a new payment
- Request Body:
```json
{
  "recipientEmail": "user@example.com",
  "amount": 100.50,
  "currency": "USD"
}
```
- Response:
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

### 3. Get Transactions
- **GET** `/transactions`
- Returns all transaction history
- Response:
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

## Step 1: Running the Server

### Prerequisites
- Java 11 or higher
- Gradle 7.0 or higher

### Method 1: Using Gradle (Recommended)

1. **Navigate to the server directory:**
```bash
cd server
```

2. **Run the server:**
```bash
./gradlew run
```

The server will start on `http://localhost:8080`

### Method 2: Build and Run JAR

1. **Build the server:**
```bash
cd server
./gradlew build
```

2. **Run the JAR file:**
```bash
java -jar build/libs/server-1.0.0.jar
```

### Method 3: Development Mode

For development with auto-reload:
```bash
cd server
./gradlew run --args="--development"
```

## Step 2: Testing the Server

### Test with curl

1. **Health check:**
```bash
curl http://localhost:8080/health
```

2. **Process a payment:**
```bash
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "recipientEmail": "test@example.com",
    "amount": 50.00,
    "currency": "USD"
  }'
```

3. **Get transactions:**
```bash
curl http://localhost:8080/transactions
```

### Test with Postman

1. Import the following collection:
```json
{
  "info": {
    "name": "CashiChallenge API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/health"
      }
    },
    {
      "name": "Process Payment",
      "request": {
        "method": "POST",
        "url": "http://localhost:8080/payments",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"recipientEmail\": \"test@example.com\",\n  \"amount\": 50.00,\n  \"currency\": \"USD\"\n}"
        }
      }
    },
    {
      "name": "Get Transactions",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/transactions"
      }
    }
  ]
}
```

## Step 3: Connecting Android App to Server

### Update API Configuration

1. **Update the API base URL in the shared module:**

Edit `shared/src/commonMain/kotlin/com/arshalif/cashi/core/config/ApiConfig.kt`:

```kotlin
package com.arshalif.cashi.core.config

object ApiConfig {
    // For local development
    const val BASE_URL = "http://10.0.2.2:8080" // Android emulator
    // const val BASE_URL = "http://localhost:8080" // Physical device (same network)
    // const val BASE_URL = "http://192.168.1.100:8080" // Physical device (specific IP)
}
```

### Network Security Configuration (Android)

1. **Create network security config:**

Create `composeApp/src/androidMain/res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">192.168.1.100</domain>
    </domain-config>
</network-security-config>
```

2. **Update AndroidManifest.xml:**

Edit `composeApp/src/androidMain/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application
        android:networkSecurityConfig="@xml/network_security_config"
        android:usesCleartextTraffic="true"
        ... >
        <!-- existing content -->
    </application>
</manifest>
```

### Different Network Configurations

#### For Android Emulator
```kotlin
const val BASE_URL = "http://10.0.2.2:8080"
```
- `10.0.2.2` is the special IP that Android emulator uses to access the host machine's localhost

#### For Physical Device (Same Network)
```kotlin
const val BASE_URL = "http://192.168.1.100:8080"
```
- Replace `192.168.1.100` with your computer's actual IP address
- Both device and computer must be on the same WiFi network

#### For Physical Device (USB Debugging)
```kotlin
const val BASE_URL = "http://localhost:8080"
```
- Requires port forwarding: `adb reverse tcp:8080 tcp:8080`

## Step 4: Testing the Integration

### 1. Start the Server
```bash
cd server
./gradlew run
```

### 2. Build and Run Android App
```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

### 3. Test Payment Flow
1. Open the app
2. Navigate to Payment screen
3. Enter payment details
4. Submit payment
5. Check transaction history

## Troubleshooting

### Common Issues

#### 1. Server Won't Start
- **Port already in use:** Change port in `Constants.kt`
- **Java version:** Ensure Java 11+ is installed
- **Dependencies:** Run `./gradlew clean build`

#### 2. Android Can't Connect to Server
- **Emulator:** Use `10.0.2.2:8080`
- **Physical device:** Use computer's IP address
- **Network security:** Check `network_security_config.xml`
- **Firewall:** Allow port 8080

#### 3. CORS Issues
- Server is configured to accept all origins for development
- For production, configure proper CORS headers

#### 4. JSON Parsing Errors
- Ensure request format matches API specification
- Check Content-Type header is `application/json`

### Debug Commands

#### Check Server Status
```bash
curl http://localhost:8080/health
```

#### Check Network Connectivity
```bash
# From Android device/emulator
adb shell ping 10.0.2.2
```

#### View Server Logs
```bash
# Server logs will appear in the terminal where you ran ./gradlew run
```

## Production Considerations

### Security
- Add authentication/authorization
- Use HTTPS
- Implement rate limiting
- Add input validation

### Database
- Replace in-memory storage with database
- Add data persistence
- Implement backup strategies

### Monitoring
- Add logging framework
- Implement health checks
- Add metrics collection

### Deployment
- Containerize with Docker
- Deploy to cloud platform
- Set up CI/CD pipeline

## Next Steps

1. **Add Authentication:** Implement user login/registration
2. **Database Integration:** Replace in-memory storage with PostgreSQL/MongoDB
3. **Error Handling:** Improve error responses and logging
4. **Testing:** Add unit and integration tests
5. **Documentation:** Generate API documentation with OpenAPI/Swagger

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review server logs
3. Test API endpoints with curl/Postman
4. Verify network configuration 