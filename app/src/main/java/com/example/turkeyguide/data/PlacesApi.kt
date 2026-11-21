package com.example.turkeyguide.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

data class Place(
    val id: String,
    val name: String,
    val address: String,
    val phone: String?,
    val rating: Double?,
    val reviewCount: Int,
    val photoName: String?,
    val lat: Double?,
    val lng: Double?
)

class PlacesApi(private val apiKey: String) {

    private val client = OkHttpClient()

    suspend fun search(query: String, includedType: String? = null): List<Place> =
        withContext(Dispatchers.IO) {
            val json = JSONObject()
                .put("textQuery", query)
                .put("languageCode", "tr")
                .put("regionCode", "TR")
                .put("maxResultCount", 20)
            if (includedType != null) json.put("includedType", includedType)

            val body = json.toString().toRequestBody("application/json".toMediaType())

            val req = Request.Builder()
                .url("https://places.googleapis.com/v1/places:searchText")
                .addHeader("X-Goog-Api-Key", apiKey)
                .addHeader(
                    "X-Goog-FieldMask",
                    "places.id,places.displayName,places.formattedAddress," +
                        "places.nationalPhoneNumber,places.rating,places.userRatingCount," +
                        "places.photos,places.location"
                )
                .post(body)
                .build()

            client.newCall(req).execute().use { resp ->
                val text = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) error("API ${resp.code}: $text")

                val arr = JSONObject(text).optJSONArray("places") ?: return@use emptyList<Place>()
                List(arr.length()) { i ->
                    val o = arr.getJSONObject(i)
                    val loc = o.optJSONObject("location")
                    Place(
                        id = o.optString("id"),
                        name = o.optJSONObject("displayName")?.optString("text").orEmpty(),
                        address = o.optString("formattedAddress"),
                        phone = o.optString("nationalPhoneNumber").ifEmpty { null },
                        rating = if (o.has("rating")) o.getDouble("rating") else null,
                        reviewCount = o.optInt("userRatingCount", 0),
                        photoName = o.optJSONArray("photos")
                            ?.optJSONObject(0)?.optString("name")?.ifEmpty { null },
                        lat = loc?.optDouble("latitude"),
                        lng = loc?.optDouble("longitude")
                    )
                }
            }
        }

    fun photoUrl(photoName: String): String =
        "https://places.googleapis.com/v1/$photoName/media?maxHeightPx=400&key=$apiKey"
}
