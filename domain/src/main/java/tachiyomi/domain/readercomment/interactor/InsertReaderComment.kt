package tachiyomi.domain.readercomment.interactor

import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.readercomment.model.ReaderComment
import tachiyomi.domain.readercomment.repository.ReaderCommentRepository

class InsertReaderComment(
    private val repository: ReaderCommentRepository,
) {

    suspend fun await(mangaId: Long, chapterId: Long?, body: String): ReaderComment? {
        return try {
            repository.insert(mangaId, chapterId, body)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            null
        }
    }
}
