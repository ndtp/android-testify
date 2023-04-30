package dev.testify

import androidx.test.core.app.ActivityScenario
import dev.testify.internal.processor.hardware.ArchitectureType
import dev.testify.internal.processor.hardware.HardwareFingerprint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class HardwareFingerprintTest {

    @Test
    fun default() {
        lateinit var activity: TestActivity
        ActivityScenario.launch(TestActivity::class.java).onActivity {
            activity = it
        }

        val fingerprint = HardwareFingerprint()

        assertEquals("3.0", fingerprint.glEsVersion(activity))
        assertEquals(ArchitectureType.X86, fingerprint.architecture)
        assertFalse(fingerprint.is64Bit)
        assertEquals(
            "Google (ATI Technologies Inc.) - OpenGL ES-CM 1.1 (4.1 ATI-4.6.20) - Android Emulator OpenGL ES Translator (AMD Radeon RX 580 OpenGL Engine)",
            fingerprint.gpuFingerprint(
                viewGroup = activity.findViewById(R.id.test_root_view)
            )
        )
        assertEquals(
            "Google Android SDK built for x86 - goldfish_x86 - abfarm-00927 - 10 - QSR1.210802.001 - google/sdk_gphone_x86/generic_x86:10/QSR1.210802.001/7603624:userdebug/dev-keys - sdk_gphone_x86-userdebug 10 QSR1.210802.001 7603624 dev-keys",
            fingerprint.fingerprint()
        )
    }
}
