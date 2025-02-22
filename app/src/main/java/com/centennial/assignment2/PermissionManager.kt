package com.centennial.assignment2


import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord

class PermissionManager {
    fun requestPermissions(permissionsLauncher: ActivityResultLauncher<Array<String>>) {
        val permissions = arrayOf(
            HealthPermission.getReadPermission(HeartRateRecord::class).toString(),
            HealthPermission.getWritePermission(HeartRateRecord::class).toString()
        )
        permissionsLauncher.launch(permissions)
    }

    fun handlePermissionResult(permissions: Map<String, Boolean>) {
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Log.d("HealthConnect", "All required permissions granted.")
        } else {
            Log.e("HealthConnect", "Some permissions were not granted.")
        }
    }
}
