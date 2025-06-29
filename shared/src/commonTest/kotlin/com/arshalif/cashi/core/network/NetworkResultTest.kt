package com.arshalif.cashi.core.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NetworkResultTest {
    
    @Test
    fun `NetworkResult Success should contain data`() {
        val testData = "Test Data"
        val result = NetworkResult.Success(testData)
        
        assertTrue(result is NetworkResult.Success, "Result should be Success")
        assertEquals(testData, result.data, "Data should match")
    }
    
    @Test
    fun `NetworkResult Error should contain message`() {
        val errorMessage = "Test Error"
        val result = NetworkResult.Error(errorMessage)
        
        assertTrue(result is NetworkResult.Error, "Result should be Error")
        assertEquals(errorMessage, result.message, "Error message should match")
    }
    
    @Test
    fun `NetworkResult Loading should be loading state`() {
        val result = NetworkResult.Loading
        
        assertTrue(result is NetworkResult.Loading, "Result should be Loading")
    }
    
    @Test
    fun `NetworkResult Success should not be error or loading`() {
        val result = NetworkResult.Success("data")
        
        assertFalse(result is NetworkResult.Error, "Success should not be Error")
        assertFalse(result is NetworkResult.Loading, "Success should not be Loading")
    }
    
    @Test
    fun `NetworkResult Error should not be success or loading`() {
        val result = NetworkResult.Error("error")
        
        assertFalse(result is NetworkResult.Success<*>, "Error should not be Success")
        assertFalse(result is NetworkResult.Loading, "Error should not be Loading")
    }
    
    @Test
    fun `NetworkResult Loading should not be success or error`() {
        val result = NetworkResult.Loading
        
        assertFalse(result is NetworkResult.Success<*>, "Loading should not be Success")
        assertFalse(result is NetworkResult.Error, "Loading should not be Error")
    }
    
    @Test
    fun `NetworkResult can handle different data types`() {
        val stringResult = NetworkResult.Success("string")
        val intResult = NetworkResult.Success(42)
        val listResult = NetworkResult.Success(listOf(1, 2, 3))
        
        assertTrue(stringResult is NetworkResult.Success, "String result should be Success")
        assertTrue(intResult is NetworkResult.Success, "Int result should be Success")
        assertTrue(listResult is NetworkResult.Success, "List result should be Success")
        
        assertEquals("string", stringResult.data)
        assertEquals(42, intResult.data)
        assertEquals(listOf(1, 2, 3), listResult.data)
    }
    
    @Test
    fun `NetworkResult Error can handle different error messages`() {
        val networkError = NetworkResult.Error("Network connection failed")
        val validationError = NetworkResult.Error("Invalid input data")
        val serverError = NetworkResult.Error("Internal server error")
        
        assertTrue(networkError is NetworkResult.Error)
        assertTrue(validationError is NetworkResult.Error)
        assertTrue(serverError is NetworkResult.Error)
        
        assertEquals("Network connection failed", networkError.message)
        assertEquals("Invalid input data", validationError.message)
        assertEquals("Internal server error", serverError.message)
    }
} 