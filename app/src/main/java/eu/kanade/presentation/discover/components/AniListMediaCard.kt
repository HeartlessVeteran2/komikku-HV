package eu.kanade.presentation.discover.components

import androidx.compose.runtime.Composable
import eu.kanade.presentation.browse.components.MangaItem
import eu.kanade.tachiyomi.data.track.anilist.dto.ALRelationNode
import eu.kanade.tachiyomi.data.track.anilist.dto.ALSearchItem
import tachiyomi.domain.manga.model.MangaCover

/**
 * A source-agnostic placeholder id used for cards built directly from AniList metadata that
 * aren't backed by any local library entry or extension source. Negative and distinct from
 * [exh.recs.sources.RECOMMENDS_SOURCE] (-1L) to avoid confusing the two discovery features.
 */
// KMK -->
const val DISCOVER_SOURCE = -2L

/** Lightweight, source-agnostic card payload shared by every row on the Discover tab. */
data class DiscoverCardItem(
    val id: Long,
    val title: String,
    val coverUrl: String,
)

fun ALSearchItem.toCardItem() = DiscoverCardItem(id = id, title = title.userPreferred, coverUrl = coverImage.large)

fun ALRelationNode.toCardItem() = DiscoverCardItem(id = id, title = title.userPreferred, coverUrl = coverImage.large)

@Composable
fun AniListMediaCard(item: DiscoverCardItem, onClick: () -> Unit) {
    MangaItem(
        title = item.title,
        cover = MangaCover(
            mangaId = -item.id,
            sourceId = DISCOVER_SOURCE,
            isMangaFavorite = false,
            ogUrl = item.coverUrl,
            lastModified = 0L,
        ),
        isFavorite = false,
        onClick = onClick,
        onLongClick = onClick,
    )
}
// KMK <--
