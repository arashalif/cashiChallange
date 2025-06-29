#!/bin/bash

echo "🚀 Starting CashiChallenge Server with Firebase Integration"
echo "=========================================================="

# Check if service account key exists
if [ ! -f "server/keys/serviceAccount.json" ]; then
    echo "❌ Firebase service account key not found!"
    echo "📋 Please download your service account key from Firebase Console:"
    echo "   1. Go to https://console.firebase.google.com/"
    echo "   2. Select your project"
    echo "   3. Go to Project Settings → Service Accounts"
    echo "   4. Click 'Generate new private key'"
    echo "   5. Save as 'server/keys/serviceAccount.json'"
    echo ""
    echo "⚠️  Server will start with in-memory storage only"
    echo ""
fi

# Check Java version
echo "🔍 Checking Java version..."
java -version

echo ""
echo "🏗️  Building server..."
./gradlew clean
./gradlew :server:build

echo ""
echo "🚀 Starting server on http://localhost:8080"

# Check Firebase integration status
if [ -f "server/keys/serviceAccount.json" ]; then
    echo "📊 Firebase Firestore integration: ✅ Enabled"
else
    echo "📊 Firebase Firestore integration: ❌ Disabled (in-memory only)"
fi

echo ""
echo "📝 Available endpoints:"
echo "   POST /payments     - Create payment"
echo "   GET  /payments     - Get all payments"
echo "   GET  /transactions - Get all transactions"
echo "   GET  /statistics   - Get statistics"
echo "   POST /clear        - Clear all data"
echo "   GET  /health       - Health check"
echo ""
echo "🛑 Press Ctrl+C to stop the server"
echo ""

# Start the server
./gradlew :server:run 