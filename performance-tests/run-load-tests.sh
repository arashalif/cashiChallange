#!/bin/bash

echo "🏋️  CashiChallenge Comprehensive Load Testing"
echo "=============================================="
echo ""

# Check if JMeter is installed
if ! command -v jmeter &> /dev/null; then
    echo "❌ JMeter is not installed. Please install Apache JMeter first."
    echo "   Download from: https://jmeter.apache.org/download_jmeter.cgi"
    echo "   Or install via Homebrew: brew install jmeter"
    exit 1
fi

# Check if server is running
echo "🔍 Checking if server is running..."
if ! curl -s http://localhost:8080/health > /dev/null; then
    echo "❌ Server is not running. Please start the server first:"
    echo "   ./start-server.sh"
    exit 1
fi

echo "✅ Server is running!"
echo ""

# Create results directory
mkdir -p performance-tests/results/load-test

# Function to run a test and show progress
run_test() {
    local test_name="$1"
    local test_file="$2"
    local description="$3"
    
    echo "🧪 Running $test_name..."
    echo "   $description"
    echo ""
    
    # Run the test
    jmeter -n -t "$test_file" \
           -l "performance-tests/results/load-test/${test_name,,}-results.jtl" \
           -e -o "performance-tests/results/load-test/${test_name,,}-report" \
           -Jserver.host=localhost \
           -Jserver.port=8080
    
    if [ $? -eq 0 ]; then
        echo "✅ $test_name completed successfully!"
        
        # Quick results summary
        if [ -f "performance-tests/results/load-test/${test_name,,}-results.jtl" ]; then
            total_requests=$(tail -n +2 "performance-tests/results/load-test/${test_name,,}-results.jtl" | wc -l)
            successful_requests=$(tail -n +2 "performance-tests/results/load-test/${test_name,,}-results.jtl" | awk -F',' '$8=="true"' | wc -l)
            failed_requests=$((total_requests - successful_requests))
            
            if [ $total_requests -gt 0 ]; then
                success_rate=$(echo "scale=2; $successful_requests * 100 / $total_requests" | bc 2>/dev/null || echo "100")
                avg_response_time=$(tail -n +2 "performance-tests/results/load-test/${test_name,,}-results.jtl" | awk -F',' '{sum+=$2; count++} END {if(count>0) printf "%.0f", sum/count; else print 0}')
                
                echo "   📊 Results: $total_requests requests, $success_rate% success rate, ${avg_response_time}ms avg response time"
            fi
        fi
        echo ""
    else
        echo "❌ $test_name failed!"
        echo ""
        return 1
    fi
}

# Test execution sequence
echo "🚀 Starting Load Testing Sequence..."
echo "This will run multiple test scenarios in sequence:"
echo "  1. Basic API Test (5 users, 10 iterations)"
echo "  2. Comprehensive Load Test (multiple scenarios)"
echo ""

# Run basic API test first
echo "=== Phase 1: Basic API Test ==="
run_test "BasicAPI" "performance-tests/payment-api-test.jmx" "Basic functionality test with 5 concurrent users"

# Ask user if they want to continue with load tests
echo "Would you like to continue with comprehensive load testing? (y/N)"
read -r response
if [[ "$response" =~ ^[Yy]$ ]]; then
    echo ""
    echo "=== Phase 2: Comprehensive Load Testing ==="
    run_test "LoadTest" "performance-tests/payment-api-load-test.jmx" "Multi-scenario load testing (baseline, normal load, stress test)"
else
    echo "Skipping comprehensive load tests."
fi

echo ""
echo "🎉 Load Testing Complete!"
echo ""
echo "📂 Results are available in:"
echo "   - performance-tests/results/load-test/"
echo ""
echo "📊 HTML Reports:"
find performance-tests/results/load-test -name "index.html" -type f | while read -r report; do
    echo "   - file://$(pwd)/$report"
done

echo ""
echo "💡 Tips for interpreting results:"
echo "   • Response Time: Target <200ms for good performance"
echo "   • Throughput: Higher is better (requests/sec)"
echo "   • Error Rate: Should be <1% for production readiness"
echo "   • 95th Percentile: Most users experience this response time or better"
echo ""
echo "🔍 Key metrics to watch:"
echo "   • Baseline: Single user performance (no concurrency overhead)"
echo "   • Load: Normal expected traffic patterns"
echo "   • Stress: Performance under high load"
echo "" 