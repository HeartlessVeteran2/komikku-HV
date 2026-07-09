package tachiyomi.domain.readercomment.interactor

import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.readercomment.model.ReaderComment
import tachiyomi.domain.readercomment.repository.ReaderCommentRepository

class GetReaderCommentsByChapterId(
    private val repository: ReaderCommentRepository,
) {

    fun subscribe(chapterId: Long): Flow<List<ReaderComment>> {
        return repository.getByChapterId(chapterId)
    }
}
