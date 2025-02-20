package com.centennial.assignment2

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime

suspend fun saveHeartRateData(
    healthConnectClient: HealthConnectClient,
    heartRate: Int,
    dateTimeStr: String
) {
    try {
        val timestamp = Instant.parse(dateTimeStr)
        val zonedDateTime = ZonedDateTime.ofInstant(timestamp, ZoneOffset.systemDefault())

        val record = HeartRateRecord(
            startTime = timestamp,
            startZoneOffset = zonedDateTime.offset,
            endTime = timestamp,
            endZoneOffset = zonedDateTime.offset,
            samples = listOf(
                HeartRateRecord.Sample(
                    beatsPerMinute = heartRate.toLong(),
                    time = timestamp
                )
            )
        )

        healthConnectClient.insertRecords(listOf(record))
    } catch (e: Exception) {
        throw e
    }
}

suspend fun loadHeartRateData(
    healthConnectClient: HealthConnectClient
): List<HeartRateRecord> {
    return try {
        val request = ReadRecordsRequest(
            recordType = HeartRateRecord::class,
            timeRangeFilter = TimeRangeFilter.before(Instant.now())
        )

        healthConnectClient.readRecords(request).records
    } catch (e: Exception) {
        emptyList()
    }
}