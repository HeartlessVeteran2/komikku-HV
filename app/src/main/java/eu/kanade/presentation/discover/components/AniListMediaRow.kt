package eu.kanade.presentation.discover.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import eu.kanade.presentation.browse.components.EmptyResultItem
import eu.kanade.presentation.browse.components.GlobalSearchErrorResultItem
import eu.kanade.presentation.browse.components.GlobalSearchLoadingResultItem
import eu.kanade.presentation.util.formattedMessage
import eu.kanade.tachiyomi.ui.discover.DiscoverRowResult
import tachiyomi.presentation.core.components.material.padding

/**
 * A titled horizontal carousel of [DiscoverCardItem]s, mirroring
 * [eu.kanade.presentation.manga.components.RelatedMangasRow]'s loading/empty/error shape. [result]
 * is nullable so a row can be hidden entirely (e.g. Continue Reading while logged out) rather than
 * rendered empty.
 */
// KMK -->
@Composable
fun AniListMediaRow(
    title: String,
    result: DiscoverRowResult<DiscoverCardItem>?,
    onClickMedia: (Long) -> Unit,
) {
    if (result == null) return

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.padding.medium,
                vertical = MaterialTheme.padding.small,
            ),
        )
        when (result) {
            DiscoverRowResult.Loading -> GlobalSearchLoadingResultItem()
            is DiscoverRowResult.Success -> {
                if (result.data.isEmpty()) {
                    EmptyResultItem()
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = MaterialTheme.padding.small),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
                    ) {
                        items(result.data, key = { "discover-$title-${it.id}" }) { item ->
                            AniListMediaCard(item = item, onClick = { onClickMedia(item.id) })
                        }
                    }
                }
            }
            is DiscoverRowResult.Error -> {
                GlobalSearchErrorResultItem(
                    message = with(LocalContext.current) { result.throwable.formattedMessage },
                )
            }
        }
    }
}
// KMK <--
