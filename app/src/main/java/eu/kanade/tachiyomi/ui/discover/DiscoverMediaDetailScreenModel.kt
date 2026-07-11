package eu.kanade.tachiyomi.ui.discover

import cafe.adriel.voyager.core.model.StateScreenModel
import eu.kanade.presentation.util.ioCoroutineScope
import eu.kanade.tachiyomi.data.track.TrackerManager
import eu.kanade.tachiyomi.data.track.anilist.dto.ALMediaDetailMedia
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

// KMK -->
class DiscoverMediaDetailScreenModel(
    private val mediaId: Long,
    private val trackerManager: TrackerManager = Injekt.get(),
) : StateScreenModel<DiscoverMediaDetailScreenModel.State>(State.Loading) {

    init {
        ioCoroutineScope.launch {
            val result = runCatching { trackerManager.aniList.api.getMediaDetail(mediaId) }
            mutableState.update {
                result.fold(
                    onSuccess = { State.Success(it) },
                    onFailure = { State.Error(it) },
                )
            }
        }
    }

    sealed interface State {
        data object Loading : State
        data class Success(val media: ALMediaDetailMedia) : State
        data class Error(val throwable: Throwable) : State
    }
}
// KMK <--
