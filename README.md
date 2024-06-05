# whatsapp_stickers_import

A Flutter plugin for adding stickers to WhatsApp.

## Notes

* This plugin is only working fine on Android. I have not tested it on iOS.
* Before using this plugin, read
  the [WhatsApp Stickers Policy](https://github.com/WhatsApp/stickers/blob/main/Android/README.md#sticker-art-and-app-requirements)
  and make sure your stickers meet the requirements.

## Usage

To use this plugin, add `whatsapp_stickers_import` as
a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).

```yaml
dependencies:
  whatsapp_stickers_import:
    git:
      url: https://github.com/luongcuong244/whatsapp_stickers_import
```

### Android

Add the following option to your `app\build.gradle` file. This will prevent all WebP files from
being compressed.

```gradle
android {
    aaptOptions {
        noCompress "webp"
    }
}
```

### iOS

Do not forget to add following entry to ```Info.plist``` with ```Runner``` target.

```xml

<key>LSApplicationQueriesSchemes</key><array>
<string>whatsapp</string>
</array>
```

## Examples

### Network URLs

```dart

final stickerUrls = [
  'https://www.gstatic.com/android/keyboard/emojikitchen/20210521/u1fa84/u1fa84_u1fa84.png',
  'https://www.gstatic.com/android/keyboard/emojikitchen/20211115/u1f979/u1f979_u1fa84.png',
  'https://www.gstatic.com/android/keyboard/emojikitchen/20230418/u1f37d-ufe0f/u1f37d-ufe0f_u1f33a.png',
  'https://www.gstatic.com/android/keyboard/emojikitchen/20230126/u1f3c0/u1f3c0_u1f4af.png',
  'https://www.gstatic.com/android/keyboard/emojikitchen/20231113/u1f3c0/u1f3c0_u1f331.png',
];

Future addToWhatsapp() async {
  if (stickerUrls.length < 3) {
    throw Exception('You need at least 3 stickers to create a sticker pack.');
  }

  final randomId = DateTime
      .now()
      .millisecondsSinceEpoch;
  var stickerPack = WhatsappStickers.fromNetworkUrl(
    identifier: randomId.toString(),
    name: "test add to whatsapp",
    publisher: 'me',
    stickerUrls: stickerUrls,
  );

  try {
    await stickerPack.sendToWhatsApp();
  } on WhatsappStickersException catch (e) {
    print(e.cause);
  }
}

```

### Local Files

The following example use the following 'flutter_cache_manager' package to download the stickers
from the network and save them to the cache directory then add them to WhatsApp.

```yaml
dependencies:
  flutter_cache_manager:
    git:
      url: https://github.com/luongcuong244/flutter_cache_manager
      path: flutter_cache_manager
      ref: develop
```

```dart

final stickerUrls = [
  'https://www.gstatic.com/android/keyboard/emojikitchen/20210521/u1fa84/u1fa84_u1fa84.png',
  'https://www.gstatic.com/android/keyboard/emojikitchen/20211115/u1f979/u1f979_u1fa84.png',
  'https://www.gstatic.com/android/keyboard/emojikitchen/20230418/u1f37d-ufe0f/u1f37d-ufe0f_u1f33a.png',
  'https://www.gstatic.com/android/keyboard/emojikitchen/20230126/u1f3c0/u1f3c0_u1f4af.png',
  'https://www.gstatic.com/android/keyboard/emojikitchen/20231113/u1f3c0/u1f3c0_u1f331.png',
];

Future addToWhatsapp() async {
  final List<Future<String>> futures = [];
  for (final url in stickerUrls) {
    futures.add(
      DefaultCacheManager()
          .getSingleFile(url, shouldResizeTo512: true)
          .then(
            (value) async {
          return value.path;
        },
      ),
    );
  }
  final stickerPaths = await Future.wait(futures);

  if (stickerPaths.length < 3) {
    throw Exception('You need at least 3 stickers to create a sticker pack.');
  }

  final randomId = DateTime
      .now()
      .millisecondsSinceEpoch;
  var stickerPack = WhatsappStickers.fromCacheFile(
    identifier: randomId.toString(),
    name: "test add to whatsapp",
    publisher: 'me',
    stickerPaths: stickerPaths,
  );

  try {
    await stickerPack.sendToWhatsApp();
  } on WhatsappStickersException catch (e) {
    print(e.cause);
  }
}

```
