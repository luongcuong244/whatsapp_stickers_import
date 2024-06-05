package dev.applicazza.flutter.plugins.whatsapp_stickers_import

import android.content.Context
import io.flutter.plugin.common.MethodCall;
import io.flutter.util.PathUtils;

object ConfigFileManager {
    fun fromMethodCall(context: Context?, call: MethodCall): StickerPack {
        if (context == null) {
            throw IllegalStateException("Context is null")
        }
        val identifier: String? = call.argument("identifier")
        val name: String? = call.argument("name")
        val publisher: String? = call.argument("publisher")
        val trayImageFileName: String? = call.argument("trayImageFileName")
        val publisherWebsite: String? = call.argument("publisherWebsite")
        val publisherEmail: String? = call.argument("publisherEmail")
        val privacyPolicyWebsite: String? = call.argument("privacyPolicyWebsite")
        val licenseAgreementWebsite: String? = call.argument("licenseAgreementWebsite")
        val stickerPaths: List<String>? = call.argument("stickerPaths")
        val uriList = stickerPaths?.map {
            FileUtils.getUriFromFilePath(context, it)
        }
        val stickerPack = StickerPack(
            identifier,
            name,
            publisher,
            trayImageFileName,
            publisherEmail,
            publisherWebsite,
            privacyPolicyWebsite,
            licenseAgreementWebsite,
            uriList,
            context
        )
        return stickerPack
    }
}