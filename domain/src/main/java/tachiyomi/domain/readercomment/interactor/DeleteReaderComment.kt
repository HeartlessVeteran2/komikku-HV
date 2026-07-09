package tachiyomi.domain.readercomment.interactor

import logcat.LogPriority
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.readercomment.repository.ReaderCommentRepository

class DeleteReaderComment(
    private val repository: ReaderCommentRepository,
) {

    suspend fun await(id: Long) {
        try {
            repository.delete(id)
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
        }
    }
}
