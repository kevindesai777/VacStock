package com.pixel.vacstock.data

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** One vaccine on the shelf and how many doses are left. */
@Serializable
data class Vaccine(
    val id: String,
    val name: String,
    val count: Int,
)

/** Highest stock one vaccine can hold; matches the five-digit count field. */
const val MAX_COUNT = 99_999

/** Longest name the add/edit dialog accepts. */
const val MAX_NAME_LENGTH = 40

/**
 * The starting list, spelled exactly as in the doctor's handwritten note (on purpose: these are
 * the names he uses, so don't "correct" them). Ids come from the names so they stay the same on
 * every read until the first change is saved.
 */
val DEFAULT_VACCINES: List<Vaccine> = listOf(
    "Hexasil", "Pneumosil", "Rotasil", "Pneubivax 14", "Prevaner", "Infanrix hexa",
    "Vaxiflu 4", "Zyvac TCV", "Tresivac", "Havshield", "JE-1", "Nexipox",
    "Menectra", "Adacel", "Boostrix", "Polio", "Hep B", "BCG",
).map { name -> Vaccine(id = "default-${name.lowercase()}", name = name, count = 0) }

fun List<Vaccine>.withCountChanged(id: String, delta: Int): List<Vaccine> =
    map { if (it.id == id) it.copy(count = (it.count + delta).coerceIn(0, MAX_COUNT)) else it }

@OptIn(ExperimentalUuidApi::class)
fun List<Vaccine>.withAdded(
    name: String,
    count: Int,
    id: String = Uuid.random().toString(),
): List<Vaccine> = this + Vaccine(id = id, name = name.trim(), count = count.coerceIn(0, MAX_COUNT))

fun List<Vaccine>.withEdited(id: String, name: String, count: Int): List<Vaccine> =
    map { if (it.id == id) it.copy(name = name.trim(), count = count.coerceIn(0, MAX_COUNT)) else it }

fun List<Vaccine>.without(id: String): List<Vaccine> = filterNot { it.id == id }

/** True when another vaccine already uses [name], ignoring case and surrounding spaces. */
fun List<Vaccine>.hasName(name: String, exceptId: String? = null): Boolean =
    any { it.id != exceptId && it.name.equals(name.trim(), ignoreCase = true) }
