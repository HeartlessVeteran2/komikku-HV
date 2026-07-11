package eu.kanade.tachiyomi.ui.discover

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import eu.kanade.presentation.discover.DiscoverScreen
import eu.kanade.presentation.util.Tab
import tachiyomi.i18n.kmk.KMR
import tachiyomi.presentation.core.i18n.stringResource

// KMK -->
data object DiscoverTab : Tab {
    private fun readResolve(): Any = DiscoverTab

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 4u,
            title = stringResource(KMR.strings.label_discover),
            icon = rememberVectorPainter(Icons.Outlined.Explore),
        )

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { DiscoverScreenModel() }
        val state by screenModel.state.collectAsState()

        DiscoverScreen(
            state = state,
            onClickMedia = { mediaId -> navigator.push(DiscoverMediaDetailScreen(mediaId)) },
        )
    }
}
// KMK <--
