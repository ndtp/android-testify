package dev.testify.compose.scenario

import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import dev.testify.ComposableTestActivity

fun launchComposableTestActivity(
    intent: Intent? = null,
    activityOptions: Bundle? = null
): ActivityScenario<ComposableTestActivity> =
    launchActivity(intent, activityOptions)
