/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2023 ndtp
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

package dev.testify.internal.processor.hardware

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.annotation.ChecksSdkIntAtLeast
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@Suppress("MemberVisibilityCanBePrivate", "unused")
class HardwareFingerprint {

    @ChecksSdkIntAtLeast val apiLevel = Build.VERSION.SDK_INT
    val board: String? = Build.BOARD
    val bootloader: String? = Build.BOOTLOADER
    val display: String? = Build.DISPLAY
    val fingerprint: String? = Build.FINGERPRINT
    val hardware: String? = Build.HARDWARE
    val host: String? = Build.HOST
    val id: String? = Build.ID
    val model: String = deviceModel
    val version: String? = Build.VERSION.RELEASE

    private val deviceModel: String
        get() = capitalize(
            if (Build.MODEL.lowercase().startsWith(Build.MANUFACTURER.lowercase()))
                Build.MODEL
            else
                "${Build.MANUFACTURER} ${Build.MODEL}"
        )

    private fun capitalize(str: String) = str.apply {
        if (isNotEmpty()) {
            first().run { if (isLowerCase()) uppercase() }
        }
    }

    fun fingerprint(): String {
        return "$model - $board - $host - $version - $id - $fingerprint - $display"
    }

    private class StubRenderer : GLSurfaceView.Renderer {

        private lateinit var rendererName: String
        private lateinit var vendorName: String
        private lateinit var glVersion: String
        private lateinit var glExtensions: List<String>

        private val syncLatch = CountDownLatch(1)

        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            gl?.let {
                rendererName = gl.glGetString(GL10.GL_RENDERER)
                vendorName = gl.glGetString(GL10.GL_VENDOR)
                glVersion = gl.glGetString(GL10.GL_VERSION)
                glExtensions = gl.glGetString(GL10.GL_EXTENSIONS).split(" ")
                syncLatch.countDown()
            }
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {}
        override fun onDrawFrame(gl: GL10) {}

        val fingerprint: String by lazy {
            syncLatch.await(5, TimeUnit.SECONDS)
            "$vendorName - $glVersion - $rendererName"
        }
    }

    fun glEsVersion(context: Context): String? {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
        val configurationInfo = activityManager?.deviceConfigurationInfo
        return configurationInfo?.glEsVersion
    }

    fun gpuFingerprint(viewGroup: ViewGroup): String {
        if (Looper.getMainLooper().thread == Thread.currentThread())
            throw RuntimeException("This method can not be called from the main application thread")

        val renderer = StubRenderer()
        val latch = CountDownLatch(1)

        Handler(Looper.getMainLooper()).post {
            val glSurfaceView = GLSurfaceView(viewGroup.context)
            glSurfaceView.setRenderer(renderer)
            viewGroup.addView(glSurfaceView)
            latch.countDown()
        }
        latch.await(2, TimeUnit.SECONDS)
        return renderer.fingerprint
    }

    val architecture: ArchitectureType
        get() = ArchitectureType.findArchitectureType(System.getProperty("os.arch") ?: "unknown")

    val is64Bit: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            Build.SUPPORTED_ABIS.find {
                it.equals("x86_64", ignoreCase = true) ||
                    it.equals("arm64-v8a", ignoreCase = true)
            } != null
        else
            false
}


