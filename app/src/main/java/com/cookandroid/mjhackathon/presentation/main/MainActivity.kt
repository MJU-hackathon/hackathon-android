package com.cookandroid.mjhackathon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.presentation.main.navigation.MainNavHost
import com.cookandroid.mjhackathon.presentation.main.navigation.rememberMainAppState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
            ) { _ ->
                // 거부 시 별도 안내 UI를 표시하지 않음
            }

            LaunchedEffect(Unit) {
                val permissions = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )

                val hasMissingPermission = permissions.any { permission ->
                    ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        permission,
                    ) != PackageManager.PERMISSION_GRANTED
                }

                if (hasMissingPermission) {
                    permissionLauncher.launch(permissions)
                }
            }

            MjHackathonTheme {
                val appState = rememberMainAppState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // 화면의 시스템 여백 바깥에서도 앱바와 같은 배경을 그린다.
                    containerColor = MjHackathonTheme.colors.bgCanvas,
                ) { innerPadding ->
                    MainNavHost(
                        appState = appState,
                        paddingValues = innerPadding,
                    )
                }
            }
        }
    }
}
