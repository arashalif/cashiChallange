#!/bin/bash

echo "🚀 Starting CashiChallenge Server..."
echo "=================================="

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 11 or higher."
    exit 1
fi

# Check Java version
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -lt 11 ]; then
    echo "❌ Java version $java_version is too old. Please install Java 11 or higher."
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"

# Check if Gradle wrapper exists in root directory
if [ ! -f "./gradlew" ]; then
    echo "❌ Gradle wrapper not found. Please run from the project root directory."
    exit 1
fi

# Make gradlew executable
chmod +x ./gradlew

echo "🔧 Building and starting server..."
echo "📡 Server will be available at: http://localhost:8080"
echo "📱 For Android emulator, use: http://10.0.2.2:8080"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

# Start the server using the root gradlew
./gradlew :server:run 