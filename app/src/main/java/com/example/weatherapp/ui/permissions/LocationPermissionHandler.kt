package com.example.weatherapp.ui.permissions

import android.Manifest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.example.weatherapp.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@Composable
fun PermissionRequest(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.error_location_permission) )},
        text = { Text(stringResource(R.string.error_location_permission)) },
        confirmButton = {
            Button(onClick = onRequestPermission) {
                Text(stringResource(R.string.permit))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.do_not_permit))
            }
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionHandler(
    isFirstLaunch: Boolean,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }
    )

    LaunchedEffect(isFirstLaunch) {
        if (isFirstLaunch && !permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    when {
        permissionState.status.isGranted -> {
            if (isFirstLaunch) {
                LaunchedEffect(Unit) {
                    onPermissionGranted()
                }
            }
        }
        permissionState.status.shouldShowRationale -> {
            PermissionRequest(
                onRequestPermission = {
                    permissionState.launchPermissionRequest()
                },
                onDismiss = onPermissionDenied
            )
        }
    }
}