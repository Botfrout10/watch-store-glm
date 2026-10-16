package com.horology.store.shared.motion

import android.provider.Settings
import com.horology.store.shared.data.AndroidShared

/** Reads the system animator scale; 0 (animations disabled) maps to the 0.5 halving rule. */
actual fun motionDurationScale(): Float {
    val raw = Settings.Global.getFloat(
        AndroidShared.appContext.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    )
    return when {
        raw == 0f -> 0.5f
        raw < 1f -> raw.coerceIn(0.5f, 1f)
        else -> 1f
    }
}
