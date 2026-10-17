package com.horology.store.shared.data

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Regression suite for the USD formatter: the M6 bug rendered "$$021,450"
 * (double literal `$` + zero-padded leading group). These cases pin the fix.
 */
class FormatUsdTest {

    @Test
    fun plainAmount() {
        assertEquals("$21,450", formatUsd(21_450L))
    }

    @Test
    fun noLeadingZeroOnMostSignificantGroup() {
        // 21_450 -> groups [450, 21]; the "21" must NOT pad to "021".
        assertEquals("$1,234,567", formatUsd(1_234_567L))
    }

    @Test
    fun singleGroup() {
        assertEquals("$980", formatUsd(980L))
    }

    @Test
    fun zero() {
        assertEquals("$0", formatUsd(0L))
    }

    @Test
    fun negative() {
        assertEquals("-$21,450", formatUsd(-21_450L))
    }

    @Test
    fun priceLabelUsesFormatter() {
        val product = catalogStub(priceUsd = 3_980L)
        assertEquals("$3,980", product.priceLabel)
    }

    private fun catalogStub(priceUsd: Long) = WatchProduct(
        id = "test-0000",
        house = "Maison",
        name = "Test",
        reference = "REF. T/0000",
        category = Categories.Dress,
        collection = "Tests",
        movement = "Manual",
        caseSpecs = "36mm steel",
        waterResistance = "5 ATM",
        dialColor = "Silver",
        strapMaterial = "Leather",
        priceUsd = priceUsd,
        year = 2024,
        description = "Stub.",
        imageUrl = "https://example.com/test.jpg",
    )
}
