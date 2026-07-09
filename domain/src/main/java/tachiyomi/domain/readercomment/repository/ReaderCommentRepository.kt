package tachiyomi.domain.readercomment.repository

import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.readercomment.model.ReaderComment

interface ReaderCommentRepository {

    fun getByChapterId(chapterId: Long): Flow<List<ReaderComment>>

    fun getByMangaId(mangaId: Long): Flow<List<ReaderComment>>

    suspend fun insert(mangaId: Long, chapterId: Long?, body: String): ReaderComment?

    suspend fun delete(id: Long)
}
