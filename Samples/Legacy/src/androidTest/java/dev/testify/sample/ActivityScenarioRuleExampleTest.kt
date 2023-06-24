package dev.testify.sample

import androidx.lifecycle.Lifecycle
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import dev.testify.sample.clients.details.ClientDetailsActivity
import org.junit.Rule
import org.junit.Test


class ActivityScenarioRuleExampleTest {

    @get:Rule
    var rule = ActivityScenarioRule(ClientDetailsActivity::class.java)

    @Test
    fun useActivityScenarioRule() {
        val scenario = rule.scenario

        Intents.init()

        scenario.onActivity {   

        }

        scenario.moveToState(Lifecycle.State.STARTED)

        Intents.release()

        scenario.close()
    }
}
