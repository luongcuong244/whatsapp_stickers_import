package dev.applicazza.flutter.plugins.whatsapp_stickers_import

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File


object FileUtils {

    fun getUriFromFilePath(context: Context, filePath: String): Uri {
        val file = File(filePath)
        return FileProvider.getUriForFile(context, context.packageName + ".provider", file)
    }
}