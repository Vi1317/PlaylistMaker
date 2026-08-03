package com.example.playlistmaker.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.ui.YsDisplay
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.viewmodel.SearchState
import com.example.playlistmaker.search.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.components.TrackListItem
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.state.observeAsState(initial = SearchState())
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchText by rememberSaveable { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = dimensionResource(R.dimen.padding_16))
    ) {
        Text(
            text = stringResource(id = R.string.search),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = dimensionResource(R.dimen.main_text).value.sp,
            fontFamily = YsDisplay,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp, bottom = 16.dp)
        )

        BasicTextField(
            value = searchText,
            onValueChange = { text ->
                searchText = text
                viewModel.searchDebounce(text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp),
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 16.sp,
                fontFamily = YsDisplay,
                color = MaterialTheme.colorScheme.onSurface
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),

            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search_16),
                        contentDescription = stringResource(R.string.search),
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primaryContainer
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (searchText.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.search),
                                fontSize = 16.sp,
                                fontFamily = YsDisplay,
                                color = MaterialTheme.colorScheme.primaryContainer
                            )
                        }
                        innerTextField()
                    }

                    if (searchText.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_clear_16),
                            contentDescription = stringResource(R.string.clear),
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    searchText = ""
                                    viewModel.searchDebounce("")
                                    keyboardController?.hide()
                                },
                            tint = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        state.let { searchState ->
            when {
                searchState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(44.dp),
                            color = colorResource(R.color.blue)
                        )
                    }
                }

                searchState.isError -> {
                    ConnectionErrorView(onRetryClick = {
                        if (searchText.isNotEmpty()) viewModel.searchDebounce(searchText)
                    })
                }

                searchState.isEmpty -> {
                    NotFoundErrorView()
                }

                searchState.showHistory && !searchState.historyEmpty -> {
                    HistoryView(
                        tracks = searchState.historyTracks,
                        onTrackClick = onTrackClick,
                        onClearHistoryClick = { viewModel.clearHistory() }
                    )
                }

                searchState.tracks.isNotEmpty() && searchText.isNotEmpty() -> {
                    TrackList(tracks = searchState.tracks, onTrackClick = onTrackClick)
                }
            }
        }
    }
}

private val TIME_FORMAT = SimpleDateFormat("mm:ss", Locale.getDefault())

@Composable
private fun TrackList(tracks: List<Track>, onTrackClick: (Track) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(tracks, key = { it.trackId }) { track ->
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

@Composable
private fun HistoryView(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    if (tracks.isEmpty()) return

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.history),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 19.sp,
            fontFamily = YsDisplay,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp, bottom = 12.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false),
            contentPadding = PaddingValues(bottom = 0.dp)
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
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onClearHistoryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.clear), fontFamily = YsDisplay)
        }
    }
}

@Composable
private fun NotFoundErrorView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.not_found_error),
            contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.not_found_error_title),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 19.sp,
            fontFamily = YsDisplay,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun ConnectionErrorView(onRetryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.connection_error), contentDescription = null
        )
        Text(
            text = stringResource(id = R.string.connection_error_title),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 19.sp,
            fontFamily = YsDisplay,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(
            onClick = onRetryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.retry),
                fontFamily = YsDisplay
            )
        }
    }
}