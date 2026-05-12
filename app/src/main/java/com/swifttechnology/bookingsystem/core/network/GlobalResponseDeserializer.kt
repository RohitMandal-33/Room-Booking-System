package com.swifttechnology.bookingsystem.core.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GlobalResponseDeserializer : JsonDeserializer<GlobalResponse<*>> {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): GlobalResponse<*> {
        val jsonObject = json.asJsonObject
        
        val success = jsonObject.get("success")?.asBoolean ?: false
        val message = jsonObject.get("message")?.asString ?: ""
        val errorCode = jsonObject.get("errorCode")?.takeIf { !it.isJsonNull }?.asString
        
        val dataElement = jsonObject.get("data")
        
        val type = (typeOfT as? ParameterizedType)?.actualTypeArguments?.getOrNull(0)
        
        val parsedData: Any? = if (dataElement != null && !dataElement.isJsonNull && type != null) {
            val isListType = type.typeName.startsWith("java.util.List")
            if (isListType && dataElement.isJsonObject) {
                // If the generic type is a List, but the server returned an object (e.g. paginated or generic wrapper),
                // we attempt to extract the "content" array safely.
                val contentElement = dataElement.asJsonObject.get("content")
                if (contentElement != null && contentElement.isJsonArray) {
                    context.deserialize(contentElement, type)
                } else {
                    // Fallback: try parsing the whole object as the list type, letting Gson handle it or fail gracefully.
                    context.deserialize(dataElement, type)
                }
            } else {
                context.deserialize(dataElement, type)
            }
        } else {
            null
        }
        
        return GlobalResponse(parsedData, success, message, errorCode)
    }
}
