/*
 * The MIT License (MIT)
 *
 * Modified work copyright (c) 2022 ndtp
 * Original work copyright (c) 2019 Shopify Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.testify

import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.testify.annotation.ScreenshotInstrumentation
import dev.testify.internal.exception.AssertSameMustBeLastException
import dev.testify.internal.exception.NoScreenshotsOnUiThreadException
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert.assertThrows
import org.junit.Assert.fail
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import java.util.Stack

@RunWith(AndroidJUnit4::class)
class RuleLifecycleTest {

    @get:Rule
    var rule: ScreenshotRule<TestActivity> = ScreenshotRule(TestActivity::class.java)

    @Before
    fun beforeMethod() {
        assertExpectedOrder(1, "beforeMethod")
    }

    @ScreenshotInstrumentation
    @Test
    fun testMethod1() {
        assertExpectedOrder(2, "testMethod1")
        rule.setViewModifications {
            assertExpectedOrder(3, "testMethod1_setViewModifications")
        }
        rule.assertSame()
    }

    /**
     * @ScreenshotInstrumentation is not required unless invoked from the gradle plugin.
     */
    @Test
    fun testMethod2() {
        assertExpectedOrder(2, "testMethod2")
        assertExpectedOrder(3, "testMethod2")
        rule.assertSame()
    }

    @Suppress("DEPRECATION")
    @get:Rule val thrown: ExpectedException = ExpectedException.none()

    @ScreenshotInstrumentation
    @Test
    fun testMethod3() {
        assertExpectedOrder(2, "testMethod3")
        assertExpectedOrder(3, "testMethod3")
        thrown.expect(RuntimeException::class.java)
        thrown.expectMessage("* You must call assertSame on the ScreenshotRule *")
    }

    @UiThreadTest
    @ScreenshotInstrumentation
    @Test
    fun testMethod4() {
        assertExpectedOrder(2, "testMethod4")
        assertExpectedOrder(3, "testMethod4")

        assertThrows(NoScreenshotsOnUiThreadException::class.java, rule::assertSame)
    }

    @ScreenshotInstrumentation
    @Test
    fun testMethod5() {
        assertExpectedOrder(2, "testMethod5")
        assertExpectedOrder(3, "testMethod5")

        rule.setViewModifications { }
        rule.assertSame()
        assertThrows(AssertSameMustBeLastException::class.java) {
            rule.setEspressoActions {}
        }
    }

    @After
    fun afterMethod() {
        assertExpectedOrder(4, "afterMethod")

        lifecycleVisits.pop() // pop 'afterMethod'

        lifecycleVisits.pop() // pop method
        lifecycleVisits.pop()

        lifecycleVisits.pop() // pop 'beforeMethod'
    }

    @Suppress("unused")
    companion object {

        private val lifecycleVisits = Stack<String>()

        private fun assertExpectedOrder(order: Int, tag: String) {
            if (lifecycleVisits.size != order) {
                fail(
                    String.format(
                        "In method [%s], expected %d but was %d\n%s",
                        tag,
                        order,
                        lifecycleVisits.size,
                        lifecycleVisits.toString()
                    )
                )
            }
            lifecycleVisits.push(tag)
        }

        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            assertExpectedOrder(0, "beforeClass")
        }

        @AfterClass
        @JvmStatic
        fun afterClass() {
            assertExpectedOrder(1, "afterClass")
        }
    }
}
