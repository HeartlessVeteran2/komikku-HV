package eu.kanade.presentation.discover

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ChromeReaderMode
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import eu.kanade.presentation.browse.components.GlobalSearchErrorResultItem
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.discover.components.AniListMediaRow
import eu.kanade.presentation.discover.components.toCardItem
import eu.kanade.presentation.manga.components.TagsChip
import eu.kanade.presentation.util.formattedMessage
import eu.kanade.tachiyomi.data.track.anilist.dto.ALCharacterEdge
import eu.kanade.tachiyomi.data.track.anilist.dto.ALMediaDetailMedia
import eu.kanade.tachiyomi.ui.discover.DiscoverMediaDetailScreenModel
import eu.kanade.tachiyomi.ui.discover.DiscoverRowResult
import eu.kanade.tachiyomi.util.lang.htmlDecode
import tachiyomi.i18n.kmk.KMR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.LoadingScreen

// KMK -->
@Composable
fun DiscoverMediaDetailContent(
    state: DiscoverMediaDetailScreenModel.State,
    navigateUp: () -> Unit,
    onClickRelated: (Long) -> Unit,
    onClickRead: (String) -> Unit,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = (state as? DiscoverMediaDetailScreenModel.State.Success)?.media?.title?.userPreferred,
                navigateUp = navigateUp,
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        when (state) {
            DiscoverMediaDetailScreenModel.State.Loading -> LoadingScreen(Modifier.padding(contentPadding))
            is DiscoverMediaDetailScreenModel.State.Error -> {
                GlobalSearchErrorResultItem(
                    message = with(LocalContext.current) { state.throwable.formattedMessage },
                )
            }
            is DiscoverMediaDetailScreenModel.State.Success -> {
                MediaDetailBody(
                    media = state.media,
                    contentPadding = contentPadding,
                    onClickRelated = onClickRelated,
                    onClickRead = onClickRead,
                )
            }
        }
    }
}

@Composable
private fun MediaDetailBody(
    media: ALMediaDetailMedia,
    contentPadding: PaddingValues,
    onClickRelated: (Long) -> Unit,
    onClickRead: (String) -> Unit,
) {
    Column(modifier = Modifier.padding(contentPadding)) {
        media.bannerImage?.let { banner ->
            AsyncImage(
                model = banner,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.padding.medium),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
        ) {
            AsyncImage(
                model = media.coverImage.large,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Column {
                Text(text = media.title.userPreferred, style = MaterialTheme.typography.titleLarge)
                val meta = listOfNotNull(
                    media.format?.replace("_", " "),
                    media.status?.replace("_", " "),
                    media.averageScore?.let { "$it%" },
                    media.chapters?.let { "$it ch" },
                ).joinToString(" • ")
                if (meta.isNotEmpty()) {
                    Text(text = meta, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (media.genres.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.padding(horizontal = MaterialTheme.padding.medium),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                media.genres.forEach { genre ->
                    TagsChip(text = genre, onClick = null)
                }
            }
        }

        media.description?.htmlDecode()?.takeIf { it.isNotBlank() }?.let { synopsis ->
            Text(
                text = synopsis,
                modifier = Modifier.padding(MaterialTheme.padding.medium),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        val authors = media.staff.edges.filter { "Story" in it.role }.mapNotNull { it.node.name() }.joinToString(", ")
        val artists = media.staff.edges.filter { "Art" in it.role }.mapNotNull { it.node.name() }.joinToString(", ")
        if (authors.isNotEmpty() || artists.isNotEmpty()) {
            Text(
                text = listOfNotNull(authors.ifEmpty { null }, artists.ifEmpty { null }).joinToString(" / "),
                modifier = Modifier.padding(horizontal = MaterialTheme.padding.medium),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (media.characters.edges.isNotEmpty()) {
            Text(
                text = stringResource(KMR.strings.discover_characters),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.padding.medium,
                    vertical = MaterialTheme.padding.small,
                ),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = MaterialTheme.padding.small),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.extraSmall),
            ) {
                items(media.characters.edges) { edge ->
                    CharacterCard(edge = edge)
                }
            }
        }

        val relatedManga = media.relations.edges
            .filter { it.node.type == "MANGA" }
            .map { it.node.toCardItem() }
        AniListMediaRow(
            title = stringResource(KMR.strings.discover_related),
            result = DiscoverRowResult.Success(relatedManga),
            onClickMedia = onClickRelated,
        )

        Button(
            onClick = { onClickRead(media.title.userPreferred) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.padding.medium),
        ) {
            Icon(imageVector = Icons.AutoMirrored.Outlined.ChromeReaderMode, contentDescription = null)
            Text(
                text = stringResource(KMR.strings.discover_action_read),
                modifier = Modifier.padding(start = MaterialTheme.padding.small),
            )
        }
    }
}

@Composable
private fun CharacterCard(edge: ALCharacterEdge) {
    Column(
        modifier = Modifier.width(72.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = edge.node.image?.large,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(64.dp)
                .aspectRatio(0.7f)
                .clip(RoundedCornerShape(4.dp)),
        )
        Text(
            text = edge.node.name().orEmpty(),
            style = MaterialTheme.typography.labelSmall,
            maxLines = 2,
            textAlign = TextAlign.Center,
        )
    }
}
// KMK <--
