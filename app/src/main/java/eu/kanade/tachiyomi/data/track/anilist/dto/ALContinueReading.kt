package eu.kanade.tachiyomi.data.track.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// KMK -->
@Serializable
data class ALContinueReadingResult(
    val data: ALContinueReadingData,
)

@Serializable
data class ALContinueReadingData(
    @SerialName("Page")
    val page: ALContinueReadingPage,
)

@Serializable
data class ALContinueReadingPage(
    val mediaList: List<ALContinueReadingEntry>,
)

@Serializable
data class ALContinueReadingEntry(
    val progress: Int,
    val media: ALSearchItem,
)
// KMK <--
