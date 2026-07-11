package eu.kanade.tachiyomi.ui.discover

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.discover.DiscoverMediaDetailContent
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.ui.browse.source.SourcesScreen

// KMK -->
class DiscoverMediaDetailScreen(private val mediaId: Long) : Screen() {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { DiscoverMediaDetailScreenModel(mediaId) }
        val state by screenModel.state.collectAsState()

        DiscoverMediaDetailContent(
            state = state,
            navigateUp = navigator::pop,
            onClickRelated = { relatedId -> navigator.push(DiscoverMediaDetailScreen(relatedId)) },
            onClickRead = { title -> navigator.push(SourcesScreen(SourcesScreen.SmartSearchConfig(title))) },
        )
    }
}
// KMK <--
