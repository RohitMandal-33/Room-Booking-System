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

/**
 * Parses member lists whether the API sends IDs as numbers, strings, or embedded user objects
 * (`{ "id": 1 }`). Also supports alternate JSON keys via [SerializedName] on [member].
 */
class MemberIdsDeserializer : JsonDeserializer<List<Long>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): List<Long> {
        if (json == null || json.isJsonNull) return emptyList()
        if (!json.isJsonArray) return emptyList()
        return json.asJsonArray.mapNotNull { element ->
            when {
                element.isJsonPrimitive && element.asJsonPrimitive.isNumber ->
                    element.asLong
                element.isJsonPrimitive && element.asJsonPrimitive.isString ->
                    element.asString.toLongOrNull()
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    when {
                        obj.has("id") && obj.get("id").isJsonPrimitive && obj.get("id").asJsonPrimitive.isNumber ->
                            obj.get("id").asLong
                        obj.has("id") && obj.get("id").isJsonPrimitive && obj.get("id").asJsonPrimitive.isString ->
                            obj.get("id").asString.toLongOrNull()
                        else -> null
                    }
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
    @JsonAdapter(MemberIdsDeserializer::class)
    @SerializedName(value = "member", alternate = ["members", "memberIds", "userIds"])
    val member: List<Long>? = null,
    /** Some backends expose only a count in list responses. */
    @SerializedName(value = "memberCount", alternate = ["membersCount", "member_count"])
    val memberCount: Int? = null
)
