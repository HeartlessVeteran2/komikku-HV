package eu.kanade.tachiyomi.ui.reader.viewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import eu.kanade.tachiyomi.data.cache.ReaderPageThumbnailCache
import eu.kanade.tachiyomi.ui.reader.model.ReaderPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import java.io.File
import java.io.InputStream

/**
 * Produces small downsampled thumbnails for reader pages, backed by [ReaderPageThumbnailCache]
 * so a given page's image is decoded at most once per cache lifetime. Used by the page
 * thumbnail strip and the full-page gallery view.
 *
 * Thumbnails are generated from [ReaderPage.stream], the same source the pager/webtoon viewers
 * use to render the full-resolution page, so requesting a thumbnail for a page that hasn't been
 * downloaded yet will fetch it from the source like any other page load.
 */
// KMK -->
class ReaderThumbnailProvider(
    private val thumbnailCache: ReaderPageThumbnailCache,
) {

    suspend fun getThumbnailFile(mangaId: Long, chapterId: Long, page: ReaderPage): File? {
        return withContext(Dispatchers.IO) {
            if (thumbnailCache.isThumbnailCached(mangaId, chapterId, page.index)) {
                return@withContext thumbnailCache.getThumbnailFile(mangaId, chapterId, page.index)
            }
            val streamProvider = page.stream ?: return@withContext null
            try {
                val bitmap = decodeSampledBitmap(streamProvider) ?: return@withContext null
                val file = thumbnailCache.putThumbnail(mangaId, chapterId, page.index, bitmap)
                bitmap.recycle()
                file
            } catch (e: Exception) {
                logcat(LogPriority.ERROR, e) { "Failed to generate reader page thumbnail" }
                null
            }
        }
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

    companion object {
        private const val THUMBNAIL_MAX_DIMENSION = 200
    }
}
// KMK <--
