package com.example.playlistmaker.media.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.media.viewmodel.FavoriteViewModel
import com.example.playlistmaker.media.viewmodel.PlaylistViewModel
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.launch

val YsDisplay = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Normal),
    Font(R.font.ys_display_medium, FontWeight.Medium)
)

private const val TAB_FAVOURITE = 0
private const val TAB_PLAYLISTS = 1
private const val TABS_COUNT = 2

@Composable
fun MediaScreen(
    favoriteViewModel: FavoriteViewModel,
    playlistViewModel: PlaylistViewModel,
    onTrackClick: (Track) -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onNewPlaylistClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary
    ) {
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(pageCount = { TABS_COUNT })
        val selectedTabIndex = pagerState.currentPage

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.media),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 22.sp,
                fontFamily = YsDisplay,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp, bottom = 16.dp)
            )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                },
                divider = {}
            ) {
                Tab(
                    selected = selectedTabIndex == TAB_FAVOURITE,
                    selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(TAB_FAVOURITE)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.favorite),
                            fontFamily = YsDisplay,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    },
                )

                Tab(
                    selected = selectedTabIndex == TAB_PLAYLISTS,
                    selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(TAB_PLAYLISTS)
                        }
                    },
                    text = {
                        Text(
                            text = stringResource(R.string.playlist),
                            fontFamily = YsDisplay,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    },
                )
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    TAB_FAVOURITE -> FavoriteScreen(
                        viewModel = favoriteViewModel,
                        onTrackClick = onTrackClick
                    )
                    TAB_PLAYLISTS -> PlaylistsScreen(
                        viewModel = playlistViewModel,
                        onPlaylistClick = onPlaylistClick,
                        onNewPlaylistClick = onNewPlaylistClick
                    )
                }
            }
        }
    }
}
