package com.shopify.testify

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.shopify.testify.TestifyFeatures.ExampleDisabledFeature
import com.shopify.testify.TestifyFeatures.ExampleFeature
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeaturesTest {

    @Test
    fun overrideDefault() {
        assertTrue(ExampleFeature.isEnabled())
        assertFalse(ExampleDisabledFeature.isEnabled())

        ExampleFeature.setEnabled(false)
        ExampleDisabledFeature.setEnabled(true)

        assertFalse(ExampleFeature.isEnabled())
        assertTrue(ExampleDisabledFeature.isEnabled())
    }

    @Test
    fun manifestCanOverrideDefault() {
        val mockPackageManager: PackageManager = mock()
        val mockContext: Context = mock {
            on { packageManager } doReturn mockPackageManager
            on { packageName } doReturn "com.testify.sample"
        }
        doReturn(mock<ApplicationInfo>().apply {
            metaData = mock {
                on { getBoolean("testify-disabled") } doReturn true
            }
        }).whenever(mockPackageManager).getApplicationInfo("com.testify.sample", GET_META_DATA)

        assertTrue(ExampleDisabledFeature.isEnabled(mockContext))
    }

    @Test
    fun canReset() {
        ExampleFeature.setEnabled(false)
        ExampleDisabledFeature.setEnabled(true)

        TestifyFeatures.reset()

        assertTrue(ExampleFeature.isEnabled())
        assertFalse(ExampleDisabledFeature.isEnabled())
    }
}
