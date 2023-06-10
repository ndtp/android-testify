package dev.testify

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class PaparazziConfigurationRule  { //: TestRule {

    init {
        System.setProperty("paparazzi.test.resources", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
        System.setProperty("paparazzi.build.dir", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
        System.setProperty("paparazzi.artifacts.cache.dir", "/Users/danieljette/.gradle")
        System.setProperty("paparazzi.platform.data.root", "/Users/danieljette/.gradle/caches/transforms-3/05b2b8149b425c35cb6d1ea51a46e87b/transformed/layoutlib-native-macosx-2022.2.1-5128371-2")
    }

//    override fun apply(base: Statement, description: Description): Statement {
//
//        System.setProperty("paparazzi.test.resources", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build/intermediates/paparazzi/debug/resources.txt")
//        System.setProperty("paparazzi.build.dir", "/Users/danieljette/DevSource/ndtp/android-testify/Samples/Module/build")
//        System.setProperty("paparazzi.artifacts.cache.dir", "/Users/danieljette/.gradle")
//
//        return base
//    }
}
