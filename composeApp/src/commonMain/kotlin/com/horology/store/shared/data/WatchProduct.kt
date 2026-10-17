package com.horology.store.shared.data

import kotlinx.serialization.Serializable

/** Filter vocabulary (design: DESIGN.md chips). */
object Categories {
    const val All = "All"
    const val Chronograph = "Chronograph"
    const val Automatic = "Automatic"
    const val Diver = "Diver"
    const val Dress = "Dress"
    const val Field = "Field"
    val filterable = listOf(Chronograph, Automatic, Diver, Dress, Field)
}

/** A catalog reference. Mirrors `composeResources/files/catalog.json` (camelCase keys). */
@Serializable
data class WatchProduct(
    val id: String,
    val house: String,
    val name: String,
    val reference: String,
    val category: String,
    val collection: String,
    val movement: String,
    val caseSpecs: String,
    val waterResistance: String,
    val dialColor: String,
    val strapMaterial: String,
    val priceUsd: Long,
    val year: Int,
    val description: String,
    val imageUrl: String,
) {
    val priceLabel: String get() = formatUsd(priceUsd)
}

/** Home hero selection (three well-lit references). */
val FeaturedProducts = listOf("car-4910", "mer-2480", "car-4010")

/** Onboarding decorative pocket-watch (visually verified 2026-10-16). */
const val OnboardingImage: String =
    "https://images.unsplash.com/photo-1509048191080-d2984bad6ae5?auto=format&fit=crop&w=1200&q=80"

/** $21,450 — manual grouping; deterministic across platforms. */
internal fun formatUsd(value: Long): String {
    val sign = if (value < 0) "-$" else "$"
    var rest = if (value < 0) -value else value
    val groups = ArrayList<String>(5)
    do {
        groups.add(rest.mod(1000L).toString())
        rest /= 1000L
    } while (rest > 0L)
    val out = StringBuilder(sign).append(groups.removeAt(groups.size - 1))
    for (i in groups.indices.reversed()) {
        out.append(',').append(groups[i].padStart(3, '0'))
    }
    return out.toString()
}
