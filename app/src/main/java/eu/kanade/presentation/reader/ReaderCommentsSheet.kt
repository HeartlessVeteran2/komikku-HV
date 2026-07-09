package eu.kanade.presentation.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.components.TabbedDialog
import kotlinx.collections.immutable.persistentListOf
import tachiyomi.domain.readercomment.model.ReaderComment
import tachiyomi.i18n.MR
import tachiyomi.i18n.kmk.KMR
import tachiyomi.presentation.core.i18n.stringResource

/**
 * A tabbed bottom sheet showing freeform notes for the current chapter, plus book-level notes
 * for the manga as a whole. Opened from the comment icon in the reader's bottom bar.
 */
// KMK -->
@Composable
fun ReaderCommentsSheet(
    chapterId: Long,
    onDismissRequest: () -> Unit,
    chapterComments: List<ReaderComment>,
    mangaComments: List<ReaderComment>,
    onAddComment: (chapterId: Long?, body: String) -> Unit,
    onDeleteComment: (id: Long) -> Unit,
) {
    TabbedDialog(
        onDismissRequest = onDismissRequest,
        tabTitles = persistentListOf(
            stringResource(KMR.strings.reader_comments_tab_chapter),
            stringResource(KMR.strings.reader_comments_tab_manga),
        ),
    ) { page ->
        val comments = if (page == 0) chapterComments else mangaComments
        val targetChapterId = if (page == 0) chapterId else null
        ReaderCommentsPage(
            comments = comments,
            onAddComment = { body -> onAddComment(targetChapterId, body) },
            onDeleteComment = onDeleteComment,
        )
    }
}

@Composable
private fun ReaderCommentsPage(
    comments: List<ReaderComment>,
    onAddComment: (String) -> Unit,
    onDeleteComment: (Long) -> Unit,
) {
    var newCommentText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .heightIn(min = 200.dp, max = 500.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = comments, key = { it.id }) { comment ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = comment.body, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onDeleteComment(comment.id) }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(MR.strings.action_delete),
                        )
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp),
        ) {
            OutlinedTextField(
                value = newCommentText,
                onValueChange = { newCommentText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(KMR.strings.reader_comments_hint)) },
            )
            IconButton(
                enabled = newCommentText.isNotBlank(),
                onClick = {
                    onAddComment(newCommentText)
                    newCommentText = ""
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(MR.strings.action_add),
                )
            }
        }
    }
}
// KMK <--
