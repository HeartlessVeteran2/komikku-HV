package tachiyomi.domain.readercomment.interactor

import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.readercomment.model.ReaderComment
import tachiyomi.domain.readercomment.repository.ReaderCommentRepository

class GetReaderCommentsByMangaId(
    private val repository: ReaderCommentRepository,
) {

    fun subscribe(mangaId: Long): Flow<List<ReaderComment>> {
        return repository.getByMangaId(mangaId)
    }
}
