package tachiyomi.domain.readercomment.interactor

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tachiyomi.domain.readercomment.repository.ReaderCommentRepository

class DeleteReaderCommentTest {

    private lateinit var deleteReaderComment: DeleteReaderComment
    private lateinit var repository: ReaderCommentRepository

    @BeforeEach
    fun beforeEach() {
        repository = mockk()
        deleteReaderComment = DeleteReaderComment(repository)
    }

    @Test
    fun `await deletes the comment by id`() = runTest {
        coEvery { repository.delete(1L) } returns Unit

        deleteReaderComment.await(1L)

        coVerify(exactly = 1) { repository.delete(1L) }
    }

    @Test
    fun `await does not throw when the repository throws`() = runTest {
        coEvery { repository.delete(any()) } throws RuntimeException("boom")

        deleteReaderComment.await(1L)
    }
}
