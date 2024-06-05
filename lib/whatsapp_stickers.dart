import 'package:flutter/services.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'exceptions.dart';

class WhatsappStickers {
  static const MethodChannel _channel = MethodChannel('whatsapp_stickers_import');

  final String identifier;
  final String name;
  final String publisher;
  List<String> _stickerPaths = [];
  List<String> _stickerUrls = [];
  String trayImageFileName = "trayimage";
  String publisherWebsite = "";
  String publisherEmail = "";
  String privacyPolicyWebsite = "";
  String licenseAgreementWebsite = "";

  WhatsappStickers.fromCacheFile({
    required this.identifier,
    required this.name,
    required this.publisher,
    required List<String> stickerPaths,
    this.trayImageFileName = "trayimage",
    this.publisherWebsite = "",
    this.publisherEmail = "",
    this.privacyPolicyWebsite = "",
    this.licenseAgreementWebsite = "",
  }) : _stickerPaths = stickerPaths;

  WhatsappStickers.fromNetworkUrl({
    required this.identifier,
    required this.name,
    required this.publisher,
    required List<String> stickerUrls,
    this.trayImageFileName = "trayimage",
    this.publisherWebsite = "",
    this.publisherEmail = "",
    this.privacyPolicyWebsite = "",
    this.licenseAgreementWebsite = "",
  }) : _stickerUrls = stickerUrls;

  Future<void> sendToWhatsApp() async {
    try {
      if (_stickerPaths.length < 3 && _stickerUrls.length < 3) {
        throw Exception(
            'You need at least 3 stickers to create a sticker pack.');
      }

      if (trayImageFileName.isEmpty) {
        throw Exception('Tray image file name cannot be empty.');
      }

      if (_stickerPaths.isEmpty) {
        _stickerPaths = await getCachePathFromNetworkUrl();
      }

      final payload = <String, dynamic>{};
      payload['identifier'] = identifier;
      payload['name'] = name;
      payload['publisher'] = publisher;
      payload['trayImageFileName'] = trayImageFileName;
      payload['publisherWebsite'] = publisherWebsite;
      payload['publisherEmail'] = publisherEmail;
      payload['privacyPolicyWebsite'] = privacyPolicyWebsite;
      payload['licenseAgreementWebsite'] = licenseAgreementWebsite;
      payload['stickerPaths'] = _stickerPaths;
      await _channel.invokeMethod('sendToWhatsApp', payload);
    } on PlatformException catch (e) {
      switch (e.code.toUpperCase()) {
        case WhatsappStickersFileNotFoundException.code:
          throw WhatsappStickersFileNotFoundException(e.message);
        case WhatsappStickersNumOutsideAllowableRangeException.code:
          throw WhatsappStickersNumOutsideAllowableRangeException(e.message);
        case WhatsappStickersUnsupportedImageFormatException.code:
          throw WhatsappStickersUnsupportedImageFormatException(e.message);
        case WhatsappStickersImageTooBigException.code:
          throw WhatsappStickersImageTooBigException(e.message);
        case WhatsappStickersIncorrectImageSizeException.code:
          throw WhatsappStickersIncorrectImageSizeException(e.message);
        case WhatsappStickersAnimatedImagesNotSupportedException.code:
          throw WhatsappStickersAnimatedImagesNotSupportedException(e.message);
        case WhatsappStickersTooManyEmojisException.code:
          throw WhatsappStickersTooManyEmojisException(e.message);
        case WhatsappStickersEmptyStringException.code:
          throw WhatsappStickersEmptyStringException(e.message);
        case WhatsappStickersStringTooLongException.code:
          throw WhatsappStickersStringTooLongException(e.message);
        case WhatsappStickersAlreadyAddedException.code:
          throw WhatsappStickersAlreadyAddedException(e.message);
        case WhatsappStickersCancelledException.code:
          throw WhatsappStickersCancelledException(e.message);
        default:
          throw WhatsappStickersException(e.message);
      }
    }
  }

  Future<List<String>> getCachePathFromNetworkUrl() async {
    assert(_stickerUrls.isNotEmpty && _stickerUrls.length >= 3);
    final List<Future<String>> futures = [];
    for (final url in _stickerUrls) {
      futures.add(
        DefaultCacheManager().getSingleFile(url, shouldResizeTo512: true).then(
          (value) async {
            return value.path;
          },
        ),
      );
    }
    return await Future.wait(futures);
  }
}

class WhatsappStickerImage {
  final String path;

  WhatsappStickerImage._internal(this.path);

  factory WhatsappStickerImage.fromAsset(String asset) {
    return WhatsappStickerImage._internal('assets://$asset');
  }

  factory WhatsappStickerImage.fromFile(String file) {
    return WhatsappStickerImage._internal('file://$file');
  }
}
