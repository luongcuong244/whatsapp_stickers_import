package dev.applicazza.flutter.plugins.whatsapp_stickers_import

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** WhatsappStickersPlugin */
class WhatsappStickersPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {
  private lateinit var channel: MethodChannel
  private var context: Context? = null
  private var activity: Activity? = null
  private var result: Result? = null
  private val ADD_PACK = 200

  companion object {
    private const val EXTRA_STICKER_PACK_ID = "sticker_pack_id"
    private const val EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority"
    private const val EXTRA_STICKER_PACK_NAME = "sticker_pack_name"

    @JvmStatic
    fun getContentProviderAuthorityURI(context: Context): Uri {
      return Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT).authority(
        getContentProviderAuthority(context)
      ).appendPath(StickerContentProvider.METADATA).build()
    }

    @JvmStatic
    fun getContentProviderAuthority(context: Context): String {
      return context.packageName + ".stickercontentprovider"
    }
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "whatsapp_stickers_import")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    this.result = result
    when (call.method) {
      "sendToWhatsApp" -> {
        try {
          val stickerPack: StickerPack = ConfigFileManager.fromMethodCall(context, call)
          val stickerPackIdentifier = stickerPack.identifier
          val stickerPackName = stickerPack.name
          val authority: String? = context?.let { getContentProviderAuthority(it) }

          StickerBook.addPackIfNotAlreadyAdded(stickerPack)

          val ws = WhitelistCheck.isWhatsAppConsumerAppInstalled(context?.packageManager)
          if (!(ws || WhitelistCheck.isWhatsAppSmbAppInstalled(context?.packageManager))) {
            throw InvalidPackException(
              InvalidPackException.OTHER,
              "WhatsApp is not installed on target device!"
            )
          }

          if (WhitelistCheck.isWhitelisted(context!!, stickerPackIdentifier)) {
            throw InvalidPackException(
              InvalidPackException.OTHER,
              "Sticker pack already added"
            )
          }

          val intent = createIntentToAddStickerPack(authority, stickerPackIdentifier, stickerPackName)

          try {
            this.activity?.startActivityForResult(intent, ADD_PACK)
          } catch (e: ActivityNotFoundException) {
            throw InvalidPackException(
              InvalidPackException.FAILED,
              "Sticker pack not added. If you'd like to add it, make sure you update to the latest version of WhatsApp."
            )
          }
        } catch (e: InvalidPackException) {
          result.error(e.code, e.message, null)
        }
      }
      else -> result.notImplemented()
    }
  }

  private fun createIntentToAddStickerPack(authority: String?, identifier: String?, stickerPackName: String?): Intent {
    return Intent().apply {
      action = "com.whatsapp.intent.action.ENABLE_STICKER_PACK"
      putExtra(EXTRA_STICKER_PACK_ID, identifier)
      putExtra(EXTRA_STICKER_PACK_AUTHORITY, authority)
      putExtra(EXTRA_STICKER_PACK_NAME, stickerPackName)
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    activity = null
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    this.activity = binding.activity
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }
}