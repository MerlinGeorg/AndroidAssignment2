package com.centennial.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.HeartRateRecord
import java.time.Instant
import kotlinx.coroutines.launch

class SaveActivity : ComponentActivity() {
    private lateinit var healthConnectClient: HealthConnectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        healthConnectClient = HealthConnectClient.getOrCreate(this)

        setContent {
            SaveScreen(
                healthConnectClient = healthConnectClient,
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun SaveScreen(
    healthConnectClient: HealthConnectClient,
    onBackClick: () -> Unit
) {
    var heartRate by remember { mutableStateOf("") }
    var dateTime by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var showSaveSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Save Heart Rate Reading",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = heartRate,
            onValueChange = { heartRate = it },
            label = { Text("Heartrate (1-300 bpm)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = dateTime,
            onValueChange = { dateTime = it },
            label = { Text("Date/Time (yyyy-MM-ddTHH:mm:ss.SSSZ)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBackClick) {
                Text("Back")
            }

            Button(
                onClick = {
                    scope.launch {
                        saveHeartRateData(
                            healthConnectClient,
                            heartRate.toIntOrNull() ?: 0,
                            dateTime
                        )
                        showSaveSuccess = true
                    }
                }
            ) {
                Text("Save")
            }
        }

        if (showSaveSuccess) {
            AlertDialog(
                onDismissRequest = { showSaveSuccess = false },
                title = { Text("Success") },
                text = { Text("Heart rate reading saved successfully") },
                confirmButton = {
                    Button(onClick = { showSaveSuccess = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}