#!/bin/bash

echo "🚀 CashiChallenge Performance Testing"
echo "====================================="
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
mkdir -p performance-tests/results

# Run performance tests
echo "🧪 Running Performance Tests..."
echo "Configuration:"
echo "  - Concurrent Users: 5"
echo "  - Ramp-up Time: 5 seconds"
echo "  - Iterations per User: 10"
echo "  - Total Requests: ~150 (5 users × 10 iterations × 3 endpoints)"
echo ""

# Create a simple JMeter test plan via command line
jmeter -n -t performance-tests/payment-api-test.jmx \
       -l performance-tests/results/results.jtl \
       -e -o performance-tests/results/html-report \
       -Jserver.host=localhost \
       -Jserver.port=8080 \
       -Jusers=5 \
       -Jrampup=5 \
       -Jiterations=10

# Check if test completed successfully
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Performance tests completed successfully!"
    echo ""
    echo "📊 Results:"
    echo "  - Raw results: performance-tests/results/results.jtl"
    echo "  - HTML report: performance-tests/results/html-report/index.html"
    echo ""
    echo "📈 Quick Summary:"
    
    # Parse results for quick summary
    if [ -f "performance-tests/results/results.jtl" ]; then
        total_requests=$(tail -n +2 performance-tests/results/results.jtl | wc -l)
        successful_requests=$(tail -n +2 performance-tests/results/results.jtl | awk -F',' '$8=="true"' | wc -l)
        failed_requests=$((total_requests - successful_requests))
        
        echo "  - Total Requests: $total_requests"
        echo "  - Successful: $successful_requests"
        echo "  - Failed: $failed_requests"
        
        if [ $failed_requests -eq 0 ]; then
            echo "  - Success Rate: 100% ✅"
        else
            success_rate=$(echo "scale=2; $successful_requests * 100 / $total_requests" | bc)
            echo "  - Success Rate: $success_rate%"
        fi
        
        # Calculate average response time
        avg_response_time=$(tail -n +2 performance-tests/results/results.jtl | awk -F',' '{sum+=$2; count++} END {if(count>0) print sum/count; else print 0}')
        echo "  - Average Response Time: ${avg_response_time}ms"
    fi
    
    echo ""
    echo "🌐 Open HTML report in browser:"
    echo "  file://$(pwd)/performance-tests/results/html-report/index.html"
    
else
    echo ""
    echo "❌ Performance tests failed!"
    echo "Check the JMeter output above for error details."
    exit 1
fi 