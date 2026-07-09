package tachiyomi.data.readercomment

import tachiyomi.domain.readercomment.model.ReaderComment

object ReaderCommentMapper {
    fun map(
        id: Long,
        mangaId: Long,
        chapterId: Long?,
        body: String,
        createdAt: Long,
        updatedAt: Long,
    ): ReaderComment = ReaderComment(
        id = id,
        mangaId = mangaId,
        chapterId = chapterId,
        body = body,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
