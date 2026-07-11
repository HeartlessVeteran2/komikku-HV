package eu.kanade.tachiyomi.ui.discover

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import eu.kanade.presentation.util.ioCoroutineScope
import eu.kanade.tachiyomi.data.track.TrackerManager
import eu.kanade.tachiyomi.data.track.anilist.dto.ALContinueReadingEntry
import eu.kanade.tachiyomi.data.track.anilist.dto.ALSearchItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Backs the Discover home tab: three public AniList rows (Trending, All-Time Popular, Popular
 * This Year) each fetched in their own coroutine on init so one slow row doesn't delay the
 * others, plus a personalized Continue Reading row that's only populated while the AniList
 * tracker is logged in and updates reactively as login state changes.
 */
// KMK -->
class DiscoverScreenModel(
    private val trackerManager: TrackerManager = Injekt.get(),
) : StateScreenModel<DiscoverScreenModel.State>(State()) {

    init {
        // Each row is fetched in its own coroutine so a slow row never delays rendering the
        // others once they're ready.
        ioCoroutineScope.launch {
            val result = runCatching { trackerManager.aniList.api.getTrending() }
            mutableState.update { it.copy(trending = result.toRowResult()) }
        }
        ioCoroutineScope.launch {
            val result = runCatching { trackerManager.aniList.api.getAllTimePopular() }
            mutableState.update { it.copy(allTimePopular = result.toRowResult()) }
        }
        ioCoroutineScope.launch {
            val result = runCatching { trackerManager.aniList.api.getPopularThisYear() }
            mutableState.update { it.copy(popularThisYear = result.toRowResult()) }
        }

        screenModelScope.launch {
            trackerManager.aniList.isLoggedInFlow.collectLatest { loggedIn ->
                if (!loggedIn) {
                    mutableState.update { it.copy(continueReading = null) }
                    return@collectLatest
                }

                mutableState.update { it.copy(continueReading = DiscoverRowResult.Loading) }
                val userId = trackerManager.aniList.getUsername().toIntOrNull()
                val result: Result<List<ALContinueReadingEntry>> = if (userId != null) {
                    runCatching { trackerManager.aniList.api.getContinueReading(userId) }
                } else {
                    Result.failure(IllegalStateException("Missing AniList user id"))
                }
                mutableState.update { it.copy(continueReading = result.toRowResult()) }
            }
        }
    }

    private fun <T> Result<List<T>>.toRowResult(): DiscoverRowResult<T> {
        return fold(
            onSuccess = { DiscoverRowResult.Success(it) },
            onFailure = { DiscoverRowResult.Error(it) },
        )
    }

    @Immutable
    data class State(
        val trending: DiscoverRowResult<ALSearchItem> = DiscoverRowResult.Loading,
        val allTimePopular: DiscoverRowResult<ALSearchItem> = DiscoverRowResult.Loading,
        val popularThisYear: DiscoverRowResult<ALSearchItem> = DiscoverRowResult.Loading,
        // null while logged out: the Continue Reading row is hidden entirely, not just empty.
        val continueReading: DiscoverRowResult<ALContinueReadingEntry>? = null,
    )
}

sealed interface DiscoverRowResult<out T> {
    data object Loading : DiscoverRowResult<Nothing>
    data class Success<T>(val data: List<T>) : DiscoverRowResult<T>
    data class Error(val throwable: Throwable) : DiscoverRowResult<Nothing>
}

fun <T, R> DiscoverRowResult<T>.map(transform: (T) -> R): DiscoverRowResult<R> {
    return when (this) {
        DiscoverRowResult.Loading -> DiscoverRowResult.Loading
        is DiscoverRowResult.Success -> DiscoverRowResult.Success(data.map(transform))
        is DiscoverRowResult.Error -> this
    }
}
// KMK <--
