package eu.kanade.tachiyomi.data.track.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// KMK -->
@Serializable
data class ALMediaDetailResult(
    val data: ALMediaDetailData,
)

@Serializable
data class ALMediaDetailData(
    @SerialName("Media")
    val media: ALMediaDetailMedia,
)

@Serializable
data class ALMediaDetailMedia(
    val id: Long,
    val title: ALItemTitle,
    val coverImage: ItemCover,
    val bannerImage: String?,
    val description: String?,
    val genres: List<String>,
    val averageScore: Int?,
    val status: String?,
    val format: String?,
    val chapters: Long?,
    val staff: ALStaff,
    val characters: ALCharacterConnection,
    val relations: ALRelationConnection,
)

@Serializable
data class ALCharacterConnection(
    val edges: List<ALCharacterEdge>,
)

@Serializable
data class ALCharacterEdge(
    val role: String,
    val node: ALCharacterNode,
)

@Serializable
data class ALCharacterNode(
    val name: ALStaffName,
    val image: ItemCover?,
)

@Serializable
data class ALRelationConnection(
    val edges: List<ALRelationEdge>,
)

@Serializable
data class ALRelationEdge(
    val relationType: String,
    val node: ALRelationNode,
)

@Serializable
data class ALRelationNode(
    val id: Long,
    val title: ALItemTitle,
    val coverImage: ItemCover,
    val type: String,
)
// KMK <--
