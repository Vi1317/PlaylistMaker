package com.example.playlistmaker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.YsDisplay

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TrackListItem(
    trackName: String,
    artistName: String,
    trackTime: String,
    coverUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            GlideImage(
                model = coverUrl,
                contentDescription = null,
                loading = placeholder(R.drawable.placeholder),
                failure = placeholder(R.drawable.placeholder),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = trackName,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontFamily = YsDisplay,
                    fontWeight = FontWeight.Normal,
                    maxLines = 1,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = artistName,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 13.sp,
                        fontFamily = YsDisplay,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.ic_dot_13),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(13.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            MaterialTheme.colorScheme.onSecondary
                        )
                    )

                    Text(
                        text = trackTime,
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 13.sp,
                        fontFamily = YsDisplay,
                        fontWeight = FontWeight.Normal,
                        style = TextStyle(
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        maxLines = 1
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.ic_forward_24),
                contentDescription = stringResource(id = R.string.forward),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(24.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    MaterialTheme.colorScheme.onSecondary
                )
            )
        }
}
