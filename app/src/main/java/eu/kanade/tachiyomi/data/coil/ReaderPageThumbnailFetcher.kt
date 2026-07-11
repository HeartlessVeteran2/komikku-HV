package eu.kanade.tachiyomi.data.coil

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import eu.kanade.tachiyomi.data.cache.ReaderPageThumbnailCache
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.ui.reader.model.ReaderPageThumbnailRequest
import kotlinx.coroutines.flow.first
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import uy.kohesive.injekt.injectLazy
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * A [Fetcher] that produces small downsampled thumbnails for reader pages, used by the page
 * thumbnail strip and the full-page gallery view.
 *
 * Thumbnails are generated from [ReaderPageThumbnailRequest.page]'s stream, the same source the
 * pager/webtoon viewers use to render the full-resolution page: requesting a thumbnail for a page
 * that hasn't finished loading yet suspends on [Page.statusFlow] until it has.
 *
 * Disk caching is handled by [ReaderPageThumbnailCache]; Coil's own [ImageLoader.memoryCache]
 * layers an in-memory cache on top so scrolling back over an already-thumbnailed page is free.
 */
// KMK -->
class ReaderPageThumbnailFetcher(
    private val data: ReaderPageThumbnailRequest,
    private val options: Options,
    private val thumbnailCache: ReaderPageThumbnailCache,
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        val mangaId = data.mangaId
        val chapterId = data.chapterId
        val page = data.page

        if (options.diskCachePolicy.readEnabled && thumbnailCache.isThumbnailCached(mangaId, chapterId, page.index)) {
            return fileLoader(thumbnailCache.getThumbnailFile(mangaId, chapterId, page.index))
        }

        page.statusFlow.first { it is Page.State.Ready || it is Page.State.Error }
        val streamProvider = page.stream ?: throw IOException("Page has no stream")

        var bitmap: Bitmap? = null
        try {
            bitmap = decodeSampledBitmap(streamProvider) ?: throw IOException("Failed to decode page thumbnail")
            val file = thumbnailCache.putThumbnail(mangaId, chapterId, page.index, bitmap)
                ?: throw IOException("Failed to write page thumbnail to cache")
            return fileLoader(file)
        } finally {
            bitmap?.recycle()
        }
    }

    private fun fileLoader(file: File): FetchResult {
        return SourceFetchResult(
            source = ImageSource(
                file = file.toOkioPath(),
                fileSystem = FileSystem.SYSTEM,
            ),
            mimeType = "image/*",
            dataSource = DataSource.DISK,
        )
    }

    private fun decodeSampledBitmap(streamProvider: () -> InputStream): Bitmap? {
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        streamProvider().use { BitmapFactory.decodeStream(it, null, boundsOptions) }
        if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) return null

        var sampleSize = 1
        while (boundsOptions.outWidth / sampleSize > THUMBNAIL_MAX_DIMENSION ||
            boundsOptions.outHeight / sampleSize > THUMBNAIL_MAX_DIMENSION
        ) {
            sampleSize *= 2
        }

        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return streamProvider().use { BitmapFactory.decodeStream(it, null, decodeOptions) }
    }

    class Factory : Fetcher.Factory<ReaderPageThumbnailRequest> {

        private val thumbnailCache: ReaderPageThumbnailCache by injectLazy()

        override fun create(data: ReaderPageThumbnailRequest, options: Options, imageLoader: ImageLoader): Fetcher {
            return ReaderPageThumbnailFetcher(
                data = data,
                options = options,
                thumbnailCache = thumbnailCache,
            )
        }
    }

    companion object {
        private const val THUMBNAIL_MAX_DIMENSION = 200
    }
}
// KMK <--
