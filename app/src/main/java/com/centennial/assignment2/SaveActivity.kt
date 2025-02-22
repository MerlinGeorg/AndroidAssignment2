package com.centennial.assignment2


import android.util.Log
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import java.time.ZoneId

class SaveActivity(private val healthConnectClient: HealthConnectClient) {
    suspend fun saveHeartRate(heartRate: Long, dateTime: String) {
        try {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val localDateTime = java.time.LocalDateTime.parse(dateTime, formatter)
            val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())

            val record = HeartRateRecord(
                startTime = zonedDateTime.toInstant(),
                startZoneOffset = zonedDateTime.offset,
                endTime = zonedDateTime.toInstant(),
                endZoneOffset = zonedDateTime.offset,
                samples = listOf(HeartRateRecord.Sample(zonedDateTime.toInstant(), heartRate))
            )

            healthConnectClient.insertRecords(listOf(record))
            Log.d("HealthConnect", "Heart rate saved successfully.")
        } catch (e: Exception) {
            Log.e("HealthConnect", "Error saving heart rate: ${e.message}")
        }
    }
}
