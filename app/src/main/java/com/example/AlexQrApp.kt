package com.example

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.example.data.local.AppDatabase
import com.example.data.local.QrRepository

class AlexQrApp : Application(), CameraXConfig.Provider {

    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { QrRepository(database.qrRecordDao()) }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}
