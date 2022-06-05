package dev.testify

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import java.io.Closeable

class ScreenshotScenario<A : Activity> private constructor() : AutoCloseable, Closeable {
    private lateinit var activityScenario: ActivityScenario<A>

    companion object {
        fun <A : Activity> launch(activityClass: Class<A>, activityOptions: Bundle? = null): ScreenshotScenario<A> {
            return ScreenshotScenario<A>().apply {
                this.activityScenario = ActivityScenario.launch(activityClass, activityOptions)
            }
        }

        fun <A : Activity> launch(startActivityIntent: Intent, activityOptions: Bundle? = null): ScreenshotScenario<A> {
            return ScreenshotScenario<A>().apply {
                this.activityScenario = ActivityScenario.launch(startActivityIntent, activityOptions)
            }
        }

        /**
         * Launches an activity of a given class and constructs ScreenshotScenario with the activity. Waits
         * for the lifecycle state transitions to be complete.
         *
         * Normally this would be [State.RESUMED], but may be another state.
         *
         * This method cannot be called from the main thread except in Robolectric tests.
         *
         * @param intent - an intent to start activity or null to use the default one
         * @param activityOptions - an activity options bundle to be passed along with the intent to start
         *        activity
         * @throws AssertionError - if the lifecycle state transition never completes within the timeout
         * @return ScreenshotScenario which you can use to make further state transitions
         */
        inline fun <reified A : Activity> launchActivity(
            intent: Intent? = null,
            activityOptions: Bundle? = null
        ): ScreenshotScenario<A> =
            when (intent) {
                null -> launch(A::class.java, activityOptions)
                else -> launch(intent, activityOptions)
            }
    }

    /**
     * Finishes the managed activity and cleans up device's state. This method blocks execution until
     * the activity becomes [State.DESTROYED].
     *
     * It is highly recommended to call this method after you test is done to keep the device state
     * clean although this is optional.
     *
     * You may call this method more than once. If the activity has been finished already, this
     * method does nothing.
     *
     * Avoid calling this method directly. Consider one of the following options instead:
     *
     * <pre>{@code
     *  Option 1, use try-with-resources:
     *
     *  try (ActivityScenario<MyActivity> scenario = ActivityScenario.launch(MyActivity.class)) {
     *    // Your test code goes here.
     *  }
     *
     *  Option 2, use ActivityScenarioRule:
     *
     * }{@literal @Rule} {@code
     *  public ActivityScenarioRule<MyActivity> rule = new ActivityScenarioRule<>(MyActivity.class);
     *
     * }{@literal @Test}{@code
     *  public void myTest() {
     *    ActivityScenario<MyActivity> scenario = rule.getScenario();
     *    // Your test code goes here.
     *  }
     * }</pre>
     */
    override fun close() {
        activityScenario.close()
    }
}
