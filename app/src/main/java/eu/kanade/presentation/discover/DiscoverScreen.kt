package eu.kanade.presentation.discover

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.discover.components.AniListMediaRow
import eu.kanade.presentation.discover.components.toCardItem
import eu.kanade.tachiyomi.ui.discover.DiscoverScreenModel
import eu.kanade.tachiyomi.ui.discover.map
import tachiyomi.i18n.kmk.KMR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource

// KMK -->
@Composable
fun DiscoverScreen(
    state: DiscoverScreenModel.State,
    onClickMedia: (Long) -> Unit,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = stringResource(KMR.strings.label_discover),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(contentPadding = contentPadding) {
            item {
                AniListMediaRow(
                    title = stringResource(KMR.strings.discover_continue_reading),
                    result = state.continueReading?.map { it.media.toCardItem() },
                    onClickMedia = onClickMedia,
                )
            }
            item {
                AniListMediaRow(
                    title = stringResource(KMR.strings.discover_trending_now),
                    result = state.trending.map { it.toCardItem() },
                    onClickMedia = onClickMedia,
                )
            }
            item {
                AniListMediaRow(
                    title = stringResource(KMR.strings.discover_popular_this_year),
                    result = state.popularThisYear.map { it.toCardItem() },
                    onClickMedia = onClickMedia,
                )
            }
            item {
                AniListMediaRow(
                    title = stringResource(KMR.strings.discover_all_time_popular),
                    result = state.allTimePopular.map { it.toCardItem() },
                    onClickMedia = onClickMedia,
                )
            }
        }
    }
}
// KMK <--
