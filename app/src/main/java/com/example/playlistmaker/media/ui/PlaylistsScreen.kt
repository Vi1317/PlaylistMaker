package com.example.playlistmaker.media.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.viewmodel.PlaylistViewModel
import com.example.playlistmaker.ui.components.PlaylistGridItem

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistViewModel,
    onPlaylistClick: (Long) -> Unit,
    onNewPlaylistClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadPlaylists()
    }

    val state by viewModel.state.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onNewPlaylistClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .padding(top = 16.dp)
                .wrapContentHeight()
        ) {
            Text(
                text = stringResource(id = R.string.new_playlist),
                fontFamily = YsDisplay,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        when (state) {
            is PlaylistViewModel.PlaylistState.Empty, null -> {
                EmptyPlaylistsPlaceholder(modifier = Modifier.weight(1f))
            }

            is PlaylistViewModel.PlaylistState.Content -> {
                val playlists = (state as PlaylistViewModel.PlaylistState.Content).playlists

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(playlists) { playlist ->
                        val trackCountText = pluralStringResource(
                            id = R.plurals.track_count,
                            count = playlist.trackCount,
                            playlist.trackCount
                        )

                        PlaylistGridItem(
                            name = playlist.name,
                            trackCountText = trackCountText,
                            coverUrl = playlist.coverPath.ifEmpty { null },
                            onClick = { onPlaylistClick(playlist.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyPlaylistsPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.not_found_error),
            contentDescription = stringResource(id = R.string.not_found_playlist),
            modifier = Modifier
                .padding(top = 46.dp)
                .size(120.dp)
        )
        Text(
            text = stringResource(id = R.string.not_found_playlist),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 19.sp,
            fontFamily = YsDisplay,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )
    }
}
