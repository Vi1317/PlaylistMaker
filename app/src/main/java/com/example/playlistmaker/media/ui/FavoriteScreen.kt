package com.example.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.viewmodel.FavoriteViewModel
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.ui.components.TrackListItem
import java.text.SimpleDateFormat
import java.util.Locale

private val TIME_FORMAT = SimpleDateFormat("mm:ss", Locale.getDefault())

@Composable
fun FavoriteScreen (
    viewModel: FavoriteViewModel,
    onTrackClick: (Track) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    val state by viewModel.state.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        when (state) {
            is FavoriteViewModel.FavoriteState.Empty, null -> {
                EmptyFavoritesPlaceholder()
            }

            is FavoriteViewModel.FavoriteState.Content -> {
                val tracks = (state as FavoriteViewModel.FavoriteState.Content).tracks

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(tracks) { track ->
                        val formattedTime = TIME_FORMAT.format(track.trackTimeMillis)

                        TrackListItem(
                            trackName = track.trackName,
                            artistName = track.artistName,
                            trackTime = formattedTime,
                            coverUrl = track.artworkUrl100,
                            onClick = { onTrackClick(track) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.not_found_error),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 106.dp)
                .wrapContentSize()
        )
        Text(
            text = stringResource(id = R.string.not_found_media),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 19.sp,
            fontFamily = YsDisplay,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
    }
}