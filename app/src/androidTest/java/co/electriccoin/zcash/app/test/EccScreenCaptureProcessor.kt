package co.electriccoin.zcash.app.test

import android.os.Build
import android.os.Environment
import androidx.test.runner.screenshot.ScreenCapture
import androidx.test.runner.screenshot.ScreenCaptureProcessor
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class EccScreenCaptureProcessor private constructor(private val screenshotDir: File) : ScreenCaptureProcessor {

    @Throws(IOException::class)
    override fun process(capture: ScreenCapture): String {
        screenshotDir.checkDirectoryIsWriteable()

        val filename = newFilename(
            name = capture.name ?: "",
            suffix = capture.format.toString().lowercase()
        )

        BufferedOutputStream(FileOutputStream(File(screenshotDir, filename))).use {
            capture.bitmap.compress(capture.format, DEFAULT_QUALITY, it)
            it.flush()
        }

        return filename
    }

    companion object {
        const val DEFAULT_QUALITY = 100

        fun new(): EccScreenCaptureProcessor {

            // Screenshots need to be stored in a public directory so they won't get cleared by Test Orchestrator
            // This path must be coordinated with the build.gradle.kts script which copies these off the device
            @Suppress("DEPRECATION")
            val screenshotsDirectory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "zcash_screenshots").also {
                it.mkdirs()
            }

            return EccScreenCaptureProcessor(screenshotsDirectory)
        }

        private fun newFilename(name: String, suffix: String) = "screenshot-$name-${Build.VERSION.SDK_INT}-${Build.DEVICE}-${UUID.randomUUID()}.$suffix"
    }
}

private fun File.checkDirectoryIsWriteable() {
    if (!isDirectory && !canWrite()) {
        throw IOException("The directory $this does not exist or is not writable.")
    }
}
