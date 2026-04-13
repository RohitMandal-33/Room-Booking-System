package com.swifttechnology.bookingsystem.features.participants.data.dtos

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type

data class CustomGroupRequestDTO(
    val groupName: String,
    val description: String?,
    val member: List<Long>
)

data class GroupMemberDetailDTO(
    val id: Long,
    val name: String? = null
)

class MembersDeserializer : JsonDeserializer<List<GroupMemberDetailDTO>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<GroupMemberDetailDTO> {
        if (json == null || json.isJsonNull) return emptyList()
        if (!json.isJsonArray) return emptyList()
        return json.asJsonArray.mapNotNull { element ->
            when {
                element.isJsonPrimitive && element.asJsonPrimitive.isNumber ->
                    GroupMemberDetailDTO(id = element.asLong)
                element.isJsonPrimitive && element.asJsonPrimitive.isString ->
                    element.asString.toLongOrNull()?.let { GroupMemberDetailDTO(id = it) }
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    val id = when {
                        obj.has("id") && obj.get("id").isJsonPrimitive && obj.get("id").asJsonPrimitive.isNumber -> obj.get("id").asLong
                        obj.has("id") && obj.get("id").isJsonPrimitive && obj.get("id").asJsonPrimitive.isString -> obj.get("id").asString.toLongOrNull()
                        obj.has("memberId") && obj.get("memberId").isJsonPrimitive && obj.get("memberId").asJsonPrimitive.isNumber -> obj.get("memberId").asLong
                        obj.has("memberId") && obj.get("memberId").isJsonPrimitive && obj.get("memberId").asJsonPrimitive.isString -> obj.get("memberId").asString.toLongOrNull()
                        else -> null
                    }
                    val name = when {
                        obj.has("memberName") && obj.get("memberName").isJsonPrimitive && obj.get("memberName").asJsonPrimitive.isString -> obj.get("memberName").asString
                        obj.has("name") && obj.get("name").isJsonPrimitive && obj.get("name").asJsonPrimitive.isString -> obj.get("name").asString
                        else -> null
                    }
                    if (id != null) GroupMemberDetailDTO(id = id, name = name) else null
                }
                else -> null
            }
        }
    }
}

data class CustomGroupResponseDTO(
    val id: Long? = null,
    @SerializedName(value = "groupName", alternate = ["name"])
    val groupName: String? = null,
    val description: String? = null,
    @JsonAdapter(MembersDeserializer::class)
    @SerializedName(value = "member", alternate = ["members", "memberIds", "userIds"])
    val membersInfo: List<GroupMemberDetailDTO>? = null,
    /** Some backends expose only a count in list responses. */
    @SerializedName(value = "memberCount", alternate = ["membersCount", "member_count"])
    val memberCount: Int? = null
)
