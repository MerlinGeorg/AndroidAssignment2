package com.centennial.assignment2


import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LoadActivity(private val healthConnectClient: HealthConnectClient) {
    suspend fun loadHeartRates(): List<Pair<String, String>> {
        return try {
            val response = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.before(Instant.now().plusSeconds(1))
                )
            )
            response.records.map { record ->
                val formattedDateTime =
                    ZonedDateTime.ofInstant(record.startTime, ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                Pair(formattedDateTime, "${record.samples.first().beatsPerMinute} bpm")
            }
        } catch (e: Exception) {
            Log.e("HealthConnect", "Error loading heart rates: ${e.message}")
            emptyList()
        }
    }
}
