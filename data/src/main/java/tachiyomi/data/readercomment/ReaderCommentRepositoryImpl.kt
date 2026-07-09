package tachiyomi.data.readercomment

import kotlinx.coroutines.flow.Flow
import tachiyomi.data.DatabaseHandler
import tachiyomi.domain.readercomment.model.ReaderComment
import tachiyomi.domain.readercomment.repository.ReaderCommentRepository

class ReaderCommentRepositoryImpl(
    private val handler: DatabaseHandler,
) : ReaderCommentRepository {

    override fun getByChapterId(chapterId: Long): Flow<List<ReaderComment>> {
        return handler.subscribeToList {
            reader_commentsQueries.getByChapterId(chapterId, ReaderCommentMapper::map)
        }
    }

    override fun getByMangaId(mangaId: Long): Flow<List<ReaderComment>> {
        return handler.subscribeToList {
            reader_commentsQueries.getByMangaId(mangaId, ReaderCommentMapper::map)
        }
    }

    override suspend fun insert(mangaId: Long, chapterId: Long?, body: String): ReaderComment? {
        return handler.await(inTransaction = true) {
            val now = System.currentTimeMillis()
            reader_commentsQueries.insert(mangaId, chapterId, body, now, now)
            val id = reader_commentsQueries.selectLastInsertedRowId().executeAsOne()
            ReaderComment(
                id = id,
                mangaId = mangaId,
                chapterId = chapterId,
                body = body,
                createdAt = now,
                updatedAt = now,
            )
        }
    }

    override suspend fun delete(id: Long) {
        handler.await { reader_commentsQueries.deleteById(id) }
    }
}
