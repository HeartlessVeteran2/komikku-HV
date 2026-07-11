package eu.kanade.tachiyomi.data.coil

import coil3.key.Keyer
import coil3.request.Options
import eu.kanade.tachiyomi.ui.reader.model.ReaderPageThumbnailRequest

// KMK -->
class ReaderPageThumbnailKeyer : Keyer<ReaderPageThumbnailRequest> {
    override fun key(data: ReaderPageThumbnailRequest, options: Options): String {
        return "${data.mangaId}_${data.chapterId}_${data.page.index}"
    }
}
// KMK <--
