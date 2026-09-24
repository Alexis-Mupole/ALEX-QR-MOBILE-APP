package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.generator.QrCodeGenerator
import com.example.model.DotShape
import com.example.model.EyeStyle
import com.example.model.ParsedQrContent
import com.example.model.QrStyleConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `main activity launches without crash`() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AlexQr", appName)
    }

    @Test
    fun `offline zxing matrix generation`() {
        val content = "https://example.com/alexqr"
        val matrix = QrCodeGenerator.generateMatrix(content)
        assertNotNull(matrix)
        assertTrue(matrix!!.isNotEmpty())
        assertTrue(matrix.size > 20)
    }

    @Test
    fun `offline qr style json serialization`() {
        val config = QrStyleConfig(
            primaryColor = 0xFF00E5FF,
            secondaryColor = 0xFF7C3AED,
            useGradient = true,
            backgroundColor = 0xFFFFFFFF,
            dotShape = DotShape.ROUNDED,
            eyeStyle = EyeStyle.CIRCLES
        )
        val json = config.toJson()
        val deserialized = QrStyleConfig.fromJson(json)

        assertEquals(config.primaryColor, deserialized.primaryColor)
        assertEquals(config.secondaryColor, deserialized.secondaryColor)
        assertEquals(config.useGradient, deserialized.useGradient)
        assertEquals(config.dotShape, deserialized.dotShape)
        assertEquals(config.eyeStyle, deserialized.eyeStyle)
    }

    @Test
    fun `offline wifi qr parsing`() {
        val rawWifi = "WIFI:S:MyGuestNetwork;T:WPA;P:SuperSecret123;H:false;;"
        val parsed = ParsedQrContent.parse(rawWifi)

        assertEquals("WIFI", parsed.contentType)
        assertEquals("MyGuestNetwork", parsed.wifiSsid)
        assertEquals("SuperSecret123", parsed.wifiPassword)
        assertEquals("WPA", parsed.wifiAuthType)
    }
}
