package com.example.playlistmaker.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.PlaylistMakerTheme

val YsDisplay = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Normal),
    Font(R.font.ys_display_medium, FontWeight.Medium)
)

@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAgreementClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.settings),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 22.sp,
                fontFamily = YsDisplay,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.theme),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontFamily = YsDisplay,
                    fontWeight = FontWeight.Normal
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeChanged,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(R.color.blue),
                        checkedTrackColor = colorResource(R.color.blue_light)
                    )
                )
            }

            SettingsRow(
                text = stringResource(id = R.string.share),
                iconResId = R.drawable.ic_share_24,
                onClick = onShareClick
            )

            SettingsRow(
                text = stringResource(id = R.string.support),
                iconResId = R.drawable.ic_support_24,
                onClick = onSupportClick
            )

            SettingsRow(
                text = stringResource(id = R.string.agreement),
                iconResId = R.drawable.ic_forward_24,
                onClick = onAgreementClick
            )
        }
    }
}

@Composable
private fun SettingsRow(
    text: String,
    iconResId: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontFamily = YsDisplay,
            fontWeight = FontWeight.Normal
        )
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
private fun SettingsScreenLightPreview() {
    PlaylistMakerTheme(darkTheme = false) {
        SettingsScreen(
            isDarkTheme = false,
            {},
            {},
            {},
            {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
private fun SettingsScreenDarkPreview() {
    PlaylistMakerTheme(darkTheme = true) {
        SettingsScreen(
            isDarkTheme = true,
            {},
            {},
            {},
            {})
    }
}
