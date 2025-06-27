package com.arshalif.cashi.core.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object DateTimeUtils {
    fun now(): Instant = Clock.System.now()
    
    /**
     * Formats a given Instant into a human-readable date-time string.
     * Example output: "2025-06-27 15:45"
     */
    fun formatTimestamp(timestamp: Instant): String {
        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = timestamp.toLocalDateTime(timeZone)
        val date = localDateTime.date
        val time = localDateTime.time
        return "${date.year}-${padZero(date.monthNumber)}-${padZero(date.dayOfMonth)} ${padZero(time.hour)}:${padZero(time.minute)}"
    }

    /**
     * Formats a given Instant into a date string in the format "12 Jun 2025".
     * Example output: "27 Jun 2025"
     */
    fun formatDate(timestamp: Instant): String {
        val timeZone = TimeZone.currentSystemDefault()
        val localDateTime = timestamp.toLocalDateTime(timeZone)
        val date = localDateTime.date
        val day = date.dayOfMonth
        val month = getMonthAbbreviation(date.monthNumber)
        val year = date.year
        
        return "$day $month $year"
    }

    /**
     * Pads single-digit numbers with a leading zero (e.g., 5 → 05).
     */
    private fun padZero(number: Int): String = number.toString().padStart(2, '0')
    
    /**
     * Returns the three-letter abbreviation for a given month number.
     */
    private fun getMonthAbbreviation(monthNumber: Int): String {
        return when (monthNumber) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> "Unknown"
        }
    }
} 