# 🚀 CashiChallenge Quick Start Guide

## 📋 Prerequisites

- **Java 11+**: `java -version`
- **Android Studio** or **Android SDK**
- **Bash terminal** (macOS/Linux) or **Git Bash** (Windows)

## 🏃‍♂️ Quick Start Commands

### 1. Start the Backend Server
```bash
# Navigate to project directory
cd /path/to/CashiChallenge

# Start the server (runs on port 8080)
./start-server.sh
```

**Expected Output:**
```
✅ Server started successfully on port 8080
🌐 Server URL: http://localhost:8080
📱 API Endpoints:
   - Health: GET /health
   - Payment: POST /payments
   - Transactions: GET /transactions
```

### 2. Test Server Connection
```bash
# In a new terminal window
./test-server.sh
```

**Expected Output:**
```
✅ Server is running and responding
📊 Health check: OK
💳 Payment endpoint: Available
📋 Transaction endpoint: Available
```

### 3. Build and Run Android App
```bash
# Build the Android app
./gradlew :composeApp:assembleDebug

# Install on connected device/emulator
./gradlew :composeApp:installDebug
```

## 📱 Using the Application

### Android App Features

1. **Payment Screen**
   - Enter recipient email
   - Enter payment amount
   - Select currency (USD, EUR, GBP)
   - Tap "Send Payment"
   - View success/error messages

2. **Transaction History**
   - Tap "View Transaction History"
   - See list of all transactions
   - Pull to refresh
   - Tap back to return to payment screen

### Testing the Payment Flow

1. **Send a Test Payment:**
   - Email: `test@example.com`
   - Amount: `100.00`
   - Currency: `USD`
   - Tap "Send Payment"

2. **Check Transaction History:**
   - Navigate to transaction screen
   - Verify your payment appears in the list
   - Check timestamp and details

## 🔧 Network Configuration

### For Android Emulator
- ✅ **Already configured** for `http://10.0.2.2:8080`
- No additional setup needed

### For Physical Device
1. **Find your computer's IP:**
   ```bash
   # macOS/Linux
   ifconfig | grep "inet " | grep -v 127.0.0.1 | head -1 | awk '{print $2}'
   
   # Windows (Git Bash)
   ipconfig | grep "IPv4" | head -1 | awk '{print $NF}'
   ```

2. **Update API configuration:**
   ```bash
   # Edit the config file
   nano shared/src/commonMain/kotlin/com/arshalif/cashi/core/config/ApiConfig.kt
   ```
   
   Change to:
   ```kotlin
   const val BASE_URL = "http://YOUR_IP_ADDRESS:8080"
   ```

3. **Update network security config:**
   ```bash
   # Edit the security config
   nano composeApp/src/androidMain/res/xml/network_security_config.xml
   ```
   
   Add your IP:
   ```xml
   <domain includeSubdomains="true">YOUR_IP_ADDRESS</domain>
   ```

## 🧪 Testing Commands

### Test Server Endpoints
```bash
# Health check
curl http://localhost:8080/health

# Send a test payment
curl -X POST http://localhost:8080/payments \
  -H "Content-Type: application/json" \
  -d '{
    "recipientEmail": "test@example.com",
    "amount": 100.0,
    "currency": "USD"
  }'

# Get transaction history
curl http://localhost:8080/transactions
```

### Test Android Connection
```bash
# Check if device is connected
adb devices

# View app logs
adb logcat | grep "Cashi"

# Test network connectivity
adb shell ping 10.0.2.2  # For emulator
adb shell ping YOUR_IP   # For physical device
```

## 🔍 Troubleshooting

### Server Issues
```bash
# Check if port 8080 is in use
lsof -i :8080

# Kill process using port 8080
sudo kill -9 $(lsof -t -i:8080)

# Check Java version
java -version
```

### Android Issues
```bash
# Clean and rebuild
./gradlew clean
./gradlew build

# Clear app data
adb shell pm clear com.arshalif.cashi.composeapp

# Check network connectivity
adb shell ping 10.0.2.2
```

## 📚 Additional Resources

- **Server Setup**: `SERVER_SETUP.md`
- **API Documentation**: Check server logs for endpoint details
- **Android Debugging**: Use Android Studio's Logcat

## 🎯 Next Steps

1. **Customize the server** for your specific needs
2. **Add authentication** to secure the API
3. **Implement database** instead of in-memory storage
4. **Add more validation** rules
5. **Create iOS app** using the shared Kotlin code

---

**Happy coding! 🚀** 