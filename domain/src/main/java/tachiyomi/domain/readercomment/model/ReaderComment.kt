package tachiyomi.domain.readercomment.model

/**
 * A freeform note attached to a chapter, or to a manga as a whole when [chapterId] is null.
 */
data class ReaderComment(
    val id: Long,
    val mangaId: Long,
    val chapterId: Long?,
    val body: String,
    val createdAt: Long,
    val updatedAt: Long,
)
