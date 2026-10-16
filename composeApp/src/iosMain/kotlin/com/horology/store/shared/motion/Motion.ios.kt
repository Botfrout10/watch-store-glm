package com.horology.store.shared.motion

import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

actual fun motionDurationScale(): Float =
    if (UIAccessibilityIsReduceMotionEnabled()) 0.5f else 1f
