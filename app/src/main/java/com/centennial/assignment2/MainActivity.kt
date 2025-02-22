package com.centennial.assignment2


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var healthConnectClient: HealthConnectClient
    private lateinit var permissionManager: PermissionManager
    private lateinit var saveActivity: SaveActivity
    private lateinit var loadActivity: LoadActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        healthConnectClient = HealthConnectClient.getOrCreate(this)
        permissionManager = PermissionManager()
        saveActivity = SaveActivity(healthConnectClient)
        loadActivity = LoadActivity(healthConnectClient)

        val permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissionManager.handlePermissionResult(permissions)
        }

        setContent {
            MaterialTheme {
                var heartRateHistory by remember { mutableStateOf(listOf<Pair<String, String>>()) }

                LaunchedEffect(Unit) {
                    permissionManager.requestPermissions(permissionsLauncher)
                }

                HealthScreen(
                    onSaveClick = { heartRate, dateTime ->
                        lifecycleScope.launch {
                            saveActivity.saveHeartRate(heartRate.toLong(), dateTime)
                        }
                    },
                    onLoadClick = {
                        lifecycleScope.launch {
                            heartRateHistory = loadActivity.loadHeartRates()
                        }
                    },
                    heartRateHistory = heartRateHistory
                )
            }
        }
    }
}
