package eu.kanade.tachiyomi.data.track.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// KMK -->
@Serializable
data class ALHomeMediaPageResult(
    val data: ALHomeMediaPageData,
)

@Serializable
data class ALHomeMediaPageData(
    @SerialName("Page")
    val page: ALHomeMediaPageList,
)

@Serializable
data class ALHomeMediaPageList(
    val media: List<ALSearchItem>,
)
// KMK <--
