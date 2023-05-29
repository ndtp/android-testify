package dev.testify.internal

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TestOptionsBuilderTest {

    private val subject: TestOptionsBuilder = TestOptionsBuilder()

    @Test
    fun `WHEN instantiated THEN is empty`() {
        assertThat(subject.resolved).isEmpty()
    }

    @Test
    fun `WHEN add once using strings THEN has one element`() {
        subject.add(key = "key", value = "value")
        assertThat(subject.resolved).hasSize(1)
        assertThat(subject.resolved.first()).isEqualTo("key value")
    }

    @Test
    fun `WHEN add once using AdbParams THEN has one element`() {
        subject.add(AdbParam("key", "value"))
        assertThat(subject.resolved).hasSize(1)
        assertThat(subject.resolved.first()).isEqualTo("key value")
    }

    @Test
    fun `WHEN add null THEN is empty`() {
        subject.add(null)
        assertThat(subject.resolved).isEmpty()
    }

    @Test
    fun `WHEN add all THEN has three elements`() {
        val many = setOf("key" to "value", "a" to "b", "1" to "2")
        subject.addAll(many)
        assertThat(subject.resolved).hasSize(3)
        assertThat(subject.resolved).contains("a b")
        assertThat(subject.resolved).contains("1 2")
        assertThat(subject.resolved).contains("key value")
    }

    @Test
    fun `WHEN add many times THEN has 4 elements`() {
        subject.add(key = "key", value = "value")
        subject.add(AdbParam("adb", "param"))
        subject.addAll(setOf("1" to "2", "3" to "4"))
        assertThat(subject.resolved).hasSize(4)
        assertThat(subject.resolved).contains("key value")
        assertThat(subject.resolved).contains("adb param")
        assertThat(subject.resolved).contains("1 2")
        assertThat(subject.resolved).contains("3 4")
    }
}
