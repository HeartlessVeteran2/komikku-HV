package eu.kanade.presentation.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import eu.kanade.tachiyomi.ui.reader.viewer.ReaderThumbnailProvider
import java.io.File

private val TILE_WIDTH = 40.dp
private val STRIP_HEIGHT = 56.dp
private const val CENTERING_LOOKAHEAD = 3

/**
 * A horizontally scrolling strip of small thumbnails for every page in the current chapter,
 * used as a visual alternative to [ChapterNavigator]'s plain numeric slider. Tapping a tile
 * jumps directly to that page.
 */
// KMK -->
@Composable
fun PageThumbnailStrip(
    pages: List<ReaderPage>,
    mangaId: Long,
    chapterId: Long,
    currentPage: Int,
    onPageIndexChange: (Int) -> Unit,
    thumbnailProvider: ReaderThumbnailProvider,
    modifier: Modifier = Modifier,
) {
    if (pages.isEmpty()) return

    val currentIndex = (currentPage - 1).coerceIn(0, pages.size - 1)
    val listState = rememberLazyListState()

    LaunchedEffect(currentIndex) {
        val targetIndex = (currentIndex - CENTERING_LOOKAHEAD).coerceIn(0, pages.size - 1)
        listState.animateScrollToItem(targetIndex)
    }

    LazyRow(
        state = listState,
        modifier = modifier
            .fillMaxWidth()
            .height(STRIP_HEIGHT),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
    ) {
        itemsIndexed(pages, key = { index, _ -> index }) { index, page ->
            PageThumbnailTile(
                page = page,
                mangaId = mangaId,
                chapterId = chapterId,
                thumbnailProvider = thumbnailProvider,
                isSelected = index == currentIndex,
                onClick = { onPageIndexChange(index) },
            )
        }
    }
}

@Composable
private fun PageThumbnailTile(
    page: ReaderPage,
    mangaId: Long,
    chapterId: Long,
    thumbnailProvider: ReaderThumbnailProvider,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val status by page.statusFlow.collectAsState()
    var thumbnailFile by remember(mangaId, chapterId, page.index) { mutableStateOf<File?>(null) }
    LaunchedEffect(mangaId, chapterId, page.index, status) {
        thumbnailFile = thumbnailProvider.getThumbnailFile(mangaId, chapterId, page)
    }

    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .width(TILE_WIDTH)
            .fillMaxHeight()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape)
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onClick),
    ) {
        val file = thumbnailFile
        if (file != null) {
            AsyncImage(
                model = file,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
// KMK <--
