package eu.kanade.presentation.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import eu.kanade.presentation.components.AppBar
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import eu.kanade.tachiyomi.ui.reader.viewer.ReaderThumbnailProvider
import tachiyomi.i18n.kmk.KMR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import java.io.File

/**
 * A full-screen grid of every page in the current chapter. Tapping a tile jumps to that page.
 */
// KMK -->
@Composable
fun ReaderPageGalleryDialog(
    pages: List<ReaderPage>,
    mangaId: Long,
    chapterId: Long,
    currentPageIndex: Int,
    onPageSelected: (Int) -> Unit,
    thumbnailProvider: ReaderThumbnailProvider,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    title = stringResource(KMR.strings.action_page_gallery),
                    navigateUp = onDismissRequest,
                )
            },
        ) { contentPadding ->
            val gridState = rememberLazyGridState(initialFirstVisibleItemIndex = currentPageIndex)
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Adaptive(minSize = 96.dp),
                contentPadding = contentPadding,
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(pages, key = { index, _ -> index }) { index, page ->
                    GalleryPageTile(
                        page = page,
                        mangaId = mangaId,
                        chapterId = chapterId,
                        thumbnailProvider = thumbnailProvider,
                        isSelected = index == currentPageIndex,
                        onClick = {
                            onPageSelected(index)
                            onDismissRequest()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryPageTile(
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
        contentAlignment = Alignment.BottomEnd,
        modifier = Modifier
            .padding(4.dp)
            .aspectRatio(0.7f)
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
        Text(
            text = page.number.toString(),
            modifier = Modifier
                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 4.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
// KMK <--
