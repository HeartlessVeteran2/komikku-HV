package eu.kanade.tachiyomi.data.cache

import android.content.Context
import android.graphics.Bitmap
import com.jakewharton.disklrucache.DiskLruCache
import eu.kanade.tachiyomi.util.storage.DiskUtil
import java.io.File
import java.io.IOException

/**
 * Disk cache for small downsampled reader-page thumbnails, used by the page thumbnail strip
 * and the full-page gallery view so a page's image is only decoded once per cache lifetime.
 *
 * Modeled on [PagePreviewCache], but keyed by manga/chapter/page instead of a page-preview URL.
 */
// KMK -->
class ReaderPageThumbnailCache(private val context: Context) {

    companion object {
        const val PARAMETER_CACHE_DIRECTORY = "reader_page_thumbnail_cache"
        const val PARAMETER_APP_VERSION = 1
        const val PARAMETER_VALUE_COUNT = 1
        private const val THUMBNAIL_JPEG_QUALITY = 80
    }

    private var diskCache = DiskLruCache.open(
        File(context.cacheDir, PARAMETER_CACHE_DIRECTORY),
        PARAMETER_APP_VERSION,
        PARAMETER_VALUE_COUNT,
        50L * 1024 * 1024,
    )

    private fun keyFor(mangaId: Long, chapterId: Long, pageIndex: Int): String {
        return DiskUtil.hashKeyForDisk("${mangaId}_${chapterId}_$pageIndex")
    }

    /**
     * Returns true if a thumbnail for this page is already cached.
     */
    fun isThumbnailCached(mangaId: Long, chapterId: Long, pageIndex: Int): Boolean {
        return try {
            diskCache.get(keyFor(mangaId, chapterId, pageIndex)) != null
        } catch (e: IOException) {
            false
        }
    }

    /**
     * Returns the cache file for this page's thumbnail. The file is only guaranteed to exist
     * after [isThumbnailCached] returns true or [putThumbnail] has succeeded.
     */
    fun getThumbnailFile(mangaId: Long, chapterId: Long, pageIndex: Int): File {
        val key = keyFor(mangaId, chapterId, pageIndex)
        return File(diskCache.directory, "$key.0")
    }

    /**
     * Writes [bitmap] to the cache as a compressed JPEG. Returns the resulting cache file, or
     * null if the write failed.
     */
    fun putThumbnail(mangaId: Long, chapterId: Long, pageIndex: Int, bitmap: Bitmap): File? {
        val key = keyFor(mangaId, chapterId, pageIndex)
        var editor: DiskLruCache.Editor? = null
        return try {
            editor = diskCache.edit(key) ?: return null
            editor.newOutputStream(0).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, THUMBNAIL_JPEG_QUALITY, out)
            }
            diskCache.flush()
            editor.commit()
            File(diskCache.directory, "$key.0")
        } catch (e: Exception) {
            editor?.abortUnlessCommitted()
            null
        }
    }
}
// KMK <--
