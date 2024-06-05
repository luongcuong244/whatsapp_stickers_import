import 'package:flutter/material.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:whatsapp_stickers_import/exceptions.dart';
import 'package:whatsapp_stickers_import/whatsapp_stickers.dart';

void main() {
  runApp(AppRoot());
}

class AppRoot extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text('WhatsApp Stickers Flutter Demo'),
        ),
        body: Container(
          padding: EdgeInsets.all(16),
          child: Column(
            children: <Widget>[
              GridView.builder(
                gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 3,
                  crossAxisSpacing: 16,
                  mainAxisSpacing: 16,
                  childAspectRatio: 1,
                ),
                itemBuilder: (context, index) {
                  return Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(20),
                      color: Colors.grey[200],
                    ),
                    child: FractionallySizedBox(
                      widthFactor: 0.6,
                      heightFactor: 0.6,
                      child: Image.network(stickerUrls[index]),
                    ),
                  );
                },
                itemCount: stickerUrls.length,
                shrinkWrap: true,
                physics: NeverScrollableScrollPhysics(),
              ),
              Center(
                child: Padding(
                  padding: const EdgeInsets.only(top: 32.0),
                  child: ElevatedButton(
                    child: Text('Add to WhatsApp'),
                    onPressed: addToWhatsapp,
                  ),
                ),
              ),
            ],
            mainAxisSize: MainAxisSize.max,
          ),
        ),
      ),
    );
  }
}

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

  // final List<Future<String>> futures = [];
  // for (final url in stickerUrls) {
  //   futures.add(
  //     DefaultCacheManager()
  //         .getSingleFile(url, shouldResizeTo512: true)
  //         .then(
  //           (value) async {
  //         return value.path;
  //       },
  //     ),
  //   );
  // }
  // final stickerPaths = await Future.wait(futures);

  final randomId = DateTime.now().millisecondsSinceEpoch;

  var stickerPack = WhatsappStickers.fromNetworkUrl(
    identifier: randomId.toString(),
    name: "test add to whatsapp",
    publisher: 'global',
    stickerUrls: stickerUrls,
    trayImageFileName: "tray image"
  );

  try {
    await stickerPack.sendToWhatsApp();
  } on WhatsappStickersException catch (e) {
    print(e.cause);
  }
}
