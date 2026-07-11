package eu.kanade.tachiyomi.ui.reader.model

/**
 * Coil3 request model for [eu.kanade.tachiyomi.data.coil.ReaderPageThumbnailFetcher].
 *
 * Must stay a `data class`: Compose's `AsyncImage` only skips re-running the underlying image
 * request across recompositions when the `model` passed in is `.equals()` to the previous one.
 * [page] is a plain, reference-equality class, so this only works because [ReaderPage] instances
 * are assigned once per chapter load (see `ReaderChapter.pages`) and are not rebuilt per
 * recomposition for the lifetime of that chapter being current. Constructing [page] via a fresh
 * `.map { }` inside a composable body would defeat this and force a re-fetch every recomposition.
 */
// KMK -->
data class ReaderPageThumbnailRequest(
    val mangaId: Long,
    val chapterId: Long,
    val page: ReaderPage,
)
// KMK <--
