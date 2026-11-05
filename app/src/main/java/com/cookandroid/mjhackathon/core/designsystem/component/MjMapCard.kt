package com.cookandroid.mjhackathon.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cookandroid.mjhackathon.R
import com.cookandroid.mjhackathon.core.designsystem.theme.MjHackathonTheme
import com.cookandroid.mjhackathon.core.util.noRippleClickable
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

/**
 * 전달받은 좌표를 중심으로 Google Maps와 장소 핀 하나를 표시한다.
 * 좌표가 변경되면 지도 중심과 핀도 갱신한다. 장소명과 주소는 호출부에서 제공한다.
 * 지도 제스처는 끄고 지도, 핀, 장소 정보 클릭을 onClick으로 전달한다.
 * Android Studio의 정적 Preview에서만 대체 배경을 사용한다.
 */
@Composable
fun MjMapCard(
    latitude: Double,
    longitude: Double,
    placeName: String,
    address: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    zoom: Float = 16f,
) {
    require(latitude.isFinite() && latitude in -90.0..90.0) {
        "latitude는 -90~90 범위의 유한한 값이어야 합니다."
    }
    require(longitude.isFinite() && longitude in -180.0..180.0) {
        "longitude는 -180~180 범위의 유한한 값이어야 합니다."
    }
    require(zoom.isFinite() && zoom in 2f..21f) {
        "zoom은 2~21 범위의 유한한 값이어야 합니다."
    }
    val colors = MjHackathonTheme.colors
    val typography = MjHackathonTheme.typography
    val shape = RoundedCornerShape(20.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = shape)
            .clip(shape)
            .background(colors.bgSurface)
            .noRippleClickable(onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(colors.bgMuted),
            contentAlignment = Alignment.Center,
        ) {
            if (LocalInspectionMode.current) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MjMapCardPin()
                    Text(
                        text = "$latitude, $longitude",
                        color = colors.textSecondary,
                        style = typography.caption,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            } else {
                MjMapCardMap(
                    targetPosition = LatLng(latitude, longitude),
                    placeName = placeName,
                    address = address,
                    zoom = zoom,
                    onClick = onClick,
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = placeName,
                color = colors.textPrimary,
                style = typography.body2Strong,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = address,
                color = colors.textSecondary,
                style = typography.caption,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MjMapCardMap(
    targetPosition: LatLng,
    placeName: String,
    address: String,
    zoom: Float,
    onClick: () -> Unit,
) {
    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(targetPosition, zoom)
    }
    val markerState = rememberUpdatedMarkerState(targetPosition)
    val colors = MjHackathonTheme.colors
    val uiSettings = remember {
        MapUiSettings(
            compassEnabled = false,
            indoorLevelPickerEnabled = false,
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false,
            rotationGesturesEnabled = false,
            scrollGesturesEnabled = false,
            scrollGesturesEnabledDuringRotateOrZoom = false,
            tiltGesturesEnabled = false,
            zoomControlsEnabled = false,
            zoomGesturesEnabled = false,
        )
    }

    LaunchedEffect(targetPosition, zoom) {
        cameraState.position = CameraPosition.fromLatLngZoom(targetPosition, zoom)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraState,
        uiSettings = uiSettings,
        contentDescription = "$placeName 위치 지도",
        onMapClick = { onClick() },
    ) {
        MarkerComposable(
            colors.bgAccent,
            colors.iconDefault,
            state = markerState,
            anchor = Offset(0.5f, 0.5f),
            title = placeName,
            snippet = address,
            contentDescription = "$placeName 위치",
            onClick = {
                onClick()
                true
            },
        ) {
            MjMapCardPin()
        }
    }
}

@Composable
private fun MjMapCardPin() {
    val colors = MjHackathonTheme.colors
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(colors.bgAccent, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_map_pin),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = colors.iconDefault,
        )
    }
}

@Preview(name = "MjMapCard - 레이아웃", widthDp = 284, showBackground = true)
@Composable
private fun MjMapCardPreview() {
    MjHackathonTheme {
        Box(Modifier.background(MjHackathonTheme.colors.bgCanvas).padding(20.dp)) {
            MjMapCard(
                latitude = 37.5665,
                longitude = 126.9780,
                placeName = "서울시청",
                address = "서울특별시 중구 세종대로 110",
                onClick = {},
            )
        }
    }
}

@Preview(name = "MjMapCard - 긴 장소 정보", widthDp = 284, showBackground = true)
@Composable
private fun MjMapCardLongTextPreview() {
    MjHackathonTheme {
        Box(Modifier.background(MjHackathonTheme.colors.bgCanvas).padding(20.dp)) {
            MjMapCard(
                latitude = 37.5665,
                longitude = 126.9780,
                placeName = "서울시청 앞 광장과 만남의 장소 안내",
                address = "서울특별시 중구 세종대로 110 서울시청 앞 서울광장",
                onClick = {},
            )
        }
    }
}
