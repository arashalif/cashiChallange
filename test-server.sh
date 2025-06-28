#!/bin/bash

echo "🧪 Testing CashiChallenge Server..."
echo "=================================="

SERVER_URL="http://localhost:8080"

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo "Testing: $description"
    echo "Endpoint: $method $SERVER_URL$endpoint"
    
    if [ "$method" = "POST" ]; then
        response=$(curl -s -w "\n%{http_code}" -X POST "$SERVER_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" -X GET "$SERVER_URL$endpoint")
    fi
    
    # Split response and status code
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo "✅ Success ($http_code)"
        if command -v jq &> /dev/null; then
            echo "Response:"
            echo "$body" | jq .
        else
            echo "Response: $body"
        fi
    else
        echo "❌ Failed ($http_code)"
        echo "Response: $body"
    fi
    echo ""
}

# Check if server is running
echo "🔍 Checking if server is running..."
if curl -s "$SERVER_URL/health" > /dev/null; then
    echo "✅ Server is running!"
    echo ""
else
    echo "❌ Server is not running. Please start the server first:"
    echo "   ./start-server.sh"
    exit 1
fi

# Test health endpoint
test_endpoint "GET" "/health" "" "Health Check"

# Test payment endpoint
payment_data='{
  "recipientEmail": "test@example.com",
  "amount": 50.00,
  "currency": "USD"
}'
test_endpoint "POST" "/payments" "$payment_data" "Process Payment"

# Test transactions endpoint
test_endpoint "GET" "/transactions" "" "Get Transactions"

echo "🎉 Server testing completed!"
echo ""
echo "📱 To test with Android app:"
echo "   1. Make sure server is running"
echo "   2. Build and install Android app"
echo "   3. Open app and try payment flow" 