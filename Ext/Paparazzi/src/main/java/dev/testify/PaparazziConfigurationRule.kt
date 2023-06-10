package dev.testify

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PaparazziConfigurationRule : TestRule {

    override fun apply(base: Statement, description: Description): Statement {

        System.setProperty("paparazzi.test.resources", "")

        return base
    }
}
