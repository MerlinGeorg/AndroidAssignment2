package com.centennial.assignment2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.HealthConnectClient
import kotlinx.coroutines.launch

class SaveActivity : ComponentActivity() {
    private lateinit var healthConnectClient: HealthConnectClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        healthConnectClient = HealthConnectClient.getOrCreate(this)

        val heartRate = intent.getStringExtra("heartRate") ?: ""
        val dateTime = intent.getStringExtra("dateTime") ?: ""

        setContent {
            SaveScreen(
                healthConnectClient = healthConnectClient,
                initialHeartRate = heartRate,
                initialDateTime = dateTime,
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun SaveScreen(
    healthConnectClient: HealthConnectClient,
    initialHeartRate: String,
    initialDateTime: String,
    onBackClick: () -> Unit
) {
    var heartRate by remember { mutableStateOf(initialHeartRate) }
    var dateTime by remember { mutableStateOf(initialDateTime) }
    val scope = rememberCoroutineScope()
    var showSaveSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

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
                        try {
                            saveHeartRateData(
                                healthConnectClient,
                                heartRate.toIntOrNull() ?: 0,
                                dateTime
                            )
                            showSaveSuccess = true
                            errorMessage = null
                            // Clear input fields after successful save
                            heartRate = ""
                            dateTime = ""
                        } catch (e: Exception) {
                            Log.e("SaveScreen", "Error saving heart rate data", e)
                            errorMessage = "Failed to save: ${e.message}"
                        }
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
