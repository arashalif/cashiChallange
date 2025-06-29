# JMeter Performance Testing Suite

This directory contains Apache JMeter performance tests for the CashiChallenge Payment API.

## 🎯 Overview

The performance testing suite includes multiple test scenarios to validate the API's performance under different load conditions and ensure proper handling of validation scenarios.

## 📁 Test Files

### 1. `payment-api-test.jmx`
**Basic API Functionality Test**
- **Purpose**: Basic functionality testing with moderate load
- **Configuration**: 5 users, 10 iterations, 5-second ramp-up
- **Tests**:
  - Health check endpoint
  - Payment creation with random data
  - Transaction history retrieval

### 2. `payment-api-load-test.jmx`
**Comprehensive Load Testing Suite**
- **Purpose**: Multi-scenario load testing to identify performance bottlenecks
- **Scenarios**:
  1. **Baseline Performance (1 User)**: 20 iterations to establish baseline metrics
  2. **Normal Load (10 Users)**: 15 iterations to simulate typical usage
  3. **Stress Test (25 Users)**: 10 iterations to test system limits
- **Features**:
  - Dynamic email generation
  - Random amounts and currencies
  - Realistic user simulation

### 3. `payment-validation-test.jmx`
**Validation & Edge Cases Test**
- **Purpose**: Test payment validation logic and edge cases
- **Test Cases**:
  - ✅ Valid payment scenarios
  - ❌ Invalid email formats
  - ❌ Negative amounts
  - ❌ Zero amounts
  - ✅ Different currencies (USD, EUR)
  - ✅ Large amount payments
  - ✅ Transaction history retrieval

## 🚀 Running Tests

### Prerequisites

1. **Install Apache JMeter**:
   ```bash
   # Via Homebrew (macOS)
   brew install jmeter
   
   # Or download from: https://jmeter.apache.org/download_jmeter.cgi
   ```

2. **Start the Server**:
   ```bash
   ./start-server.sh
   ```

### Quick Start

```bash
# Basic performance test
./performance-tests/run-performance-tests.sh

# Comprehensive load testing
./performance-tests/run-load-tests.sh

# Validation testing only
jmeter -n -t performance-tests/payment-validation-test.jmx \
       -l performance-tests/results/validation-results.jtl \
       -e -o performance-tests/results/validation-report
```

### Manual Execution

```bash
# Run specific test with custom parameters
jmeter -n -t performance-tests/payment-api-test.jmx \
       -l results/test-results.jtl \
       -e -o results/html-report \
       -Jserver.host=localhost \
       -Jserver.port=8080 \
       -Jusers=10 \
       -Jrampup=5 \
       -Jiterations=20
```

## 📊 Understanding Results

### Key Metrics

| Metric | Good | Acceptable | Poor |
|--------|------|------------|------|
| **Response Time** | <200ms | <500ms | >1000ms |
| **Throughput** | >100 req/sec | >50 req/sec | <25 req/sec |
| **Error Rate** | 0% | <1% | >5% |
| **95th Percentile** | <300ms | <800ms | >1500ms |

### Response Time Guidelines
- **Baseline (1 user)**: Establishes system performance without concurrency overhead
- **Load (10 users)**: Typical production load simulation
- **Stress (25+ users)**: Peak load and system limits testing

### Common Issues & Solutions

| Issue | Symptoms | Solutions |
|-------|----------|-----------|
| **High Response Time** | >1000ms average | • Check database queries<br>• Review server resources<br>• Optimize API endpoints |
| **High Error Rate** | >5% failures | • Check validation logic<br>• Review error handling<br>• Verify input data |
| **Low Throughput** | <25 req/sec | • Database connection pooling<br>• Server scaling<br>• Code optimization |

## 🎛️ Customization

### Test Parameters

You can customize test execution with JMeter properties:

```bash
# Custom load test
jmeter -n -t payment-api-load-test.jmx \
       -Jserver.host=your-server.com \
       -Jserver.port=8080 \
       -Jusers=50 \
       -Jrampup=30 \
       -Jiterations=100
```

### Environment Variables

| Property | Default | Description |
|----------|---------|-------------|
| `server.host` | localhost | Target server hostname |
| `server.port` | 8080 | Target server port |
| `users` | varies | Number of concurrent users |
| `rampup` | varies | Ramp-up time in seconds |
| `iterations` | varies | Number of iterations per user |

## 📈 Performance Targets

### Production Readiness Criteria

- ✅ **Response Time**: 95% of requests under 200ms
- ✅ **Throughput**: Handle 100+ requests/second
- ✅ **Error Rate**: Less than 0.1% under normal load
- ✅ **Stability**: No memory leaks during extended runs
- ✅ **Scalability**: Linear performance improvement with horizontal scaling

### Load Testing Scenarios

1. **Baseline**: Single user, no concurrency
2. **Typical**: 10-20 concurrent users
3. **Peak**: 50-100 concurrent users  
4. **Stress**: 200+ concurrent users (breaking point testing)

## 📝 Reporting

### Automated Reports

HTML reports are automatically generated in:
- `performance-tests/results/html-report/index.html`
- `performance-tests/results/load-test/*/index.html`

### Key Report Sections

1. **Summary**: Overall test statistics
2. **Response Times**: Distribution and percentiles
3. **Throughput**: Requests per second over time
4. **Active Threads**: Concurrency levels
5. **Response Codes**: Success/error breakdown

## 🐛 Troubleshooting

### Common Issues

1. **Server Not Running**:
   ```
   Error: Connection refused
   Solution: Start server with ./start-server.sh
   ```

2. **JMeter Not Found**:
   ```
   Error: command not found: jmeter
   Solution: Install JMeter via brew install jmeter
   ```

3. **High Error Rate**:
   ```
   Check server logs for validation errors
   Verify test data matches API requirements
   ```

### Debug Mode

Run tests with verbose output:
```bash
jmeter -n -t test-file.jmx -l results.jtl -j jmeter.log
tail -f jmeter.log  # Monitor real-time logs
```

## 🔄 Continuous Integration

### Integration with CI/CD

```yaml
# Example GitHub Actions workflow
- name: Performance Tests
  run: |
    ./start-server.sh &
    sleep 10
    ./performance-tests/run-performance-tests.sh
    # Parse results and fail if thresholds not met
```

### Performance Thresholds

Configure automated pass/fail criteria:
- Response time 95th percentile < 500ms
- Error rate < 1%
- Throughput > 50 req/sec

## 📚 Additional Resources

- [Apache JMeter Documentation](https://jmeter.apache.org/usermanual/index.html)
- [JMeter Best Practices](https://jmeter.apache.org/usermanual/best-practices.html)
- [Performance Testing Guidelines](https://martinfowler.com/articles/practical-test-pyramid.html) 