package tachiyomi.domain.readercomment.interactor

import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tachiyomi.domain.readercomment.model.ReaderComment
import tachiyomi.domain.readercomment.repository.ReaderCommentRepository

class InsertReaderCommentTest {

    private lateinit var insertReaderComment: InsertReaderComment
    private lateinit var repository: ReaderCommentRepository

    @BeforeEach
    fun beforeEach() {
        repository = mockk()
        insertReaderComment = InsertReaderComment(repository)
    }

    @Test
    fun `await returns the inserted comment on success`() = runTest {
        val comment = ReaderComment(
            id = 1L,
            mangaId = 2L,
            chapterId = 3L,
            body = "note",
            createdAt = 100L,
            updatedAt = 100L,
        )
        coEvery { repository.insert(2L, 3L, "note") } returns comment

        val result = insertReaderComment.await(mangaId = 2L, chapterId = 3L, body = "note")

        result shouldBe comment
    }

    @Test
    fun `await returns null when the repository throws`() = runTest {
        coEvery { repository.insert(any(), any(), any()) } throws RuntimeException("boom")

        val result = insertReaderComment.await(mangaId = 2L, chapterId = 3L, body = "note")

        result shouldBe null
    }
}
