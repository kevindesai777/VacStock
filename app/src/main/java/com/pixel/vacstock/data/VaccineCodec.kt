package com.pixel.vacstock.data

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/** Turns the vaccine list into the JSON string DataStore keeps, and back. */
internal object VaccineCodec {
    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = ListSerializer(Vaccine.serializer())

    fun encode(vaccines: List<Vaccine>): String = json.encodeToString(serializer, vaccines)

    /** Returns null for text that isn't a saved list, so callers can fall back to the defaults. */
    fun decode(text: String): List<Vaccine>? = try {
        json.decodeFromString(serializer, text)
    } catch (_: IllegalArgumentException) {
        null
    }
}
