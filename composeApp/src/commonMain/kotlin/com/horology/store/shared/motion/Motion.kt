package com.horology.store.shared.motion

/**
 * Reduce-motion response per DESIGN.md: entrance/copy durations are halved.
 * 1f = standard, 0.5f = reduced (Android animation scale 0 / iOS Reduce Motion).
 */
expect fun motionDurationScale(): Float
