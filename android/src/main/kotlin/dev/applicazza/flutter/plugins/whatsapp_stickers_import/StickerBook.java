package dev.applicazza.flutter.plugins.whatsapp_stickers_import;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class StickerBook {

    static Context myContext;
    public static ArrayList<StickerPack> allStickerPacks = checkIfPacksAreNull();

    public static void init(Context context){
        myContext = context;
    }

    private static ArrayList<StickerPack> checkIfPacksAreNull(){
        if(allStickerPacks==null){
            Log.w("IS PACKS NULL?", "YES");
            return new ArrayList<>();
        }
        Log.w("IS PACKS NULL?", "NO");
        return allStickerPacks;
    }

    public static boolean checkIfPackAlreadyAddedById(String id){
        for (StickerPack stickerPack : allStickerPacks){
            if(stickerPack.getIdentifier().equals(id)){
                return true;
            }
        }
        return false;
    }

    public static void addPackIfNotAlreadyAdded(StickerPack sp){
        if(!checkIfPackAlreadyAddedById(sp.getIdentifier())){
            allStickerPacks.add(sp);
        }
    }

    public static void addStickerPackExisting(StickerPack sp){
        allStickerPacks.add(sp);
    }

    public static ArrayList<StickerPack> getAllStickerPacks(){
        return allStickerPacks;
    }

    public static StickerPack getStickerPackByName(String stickerPackName){
        for (StickerPack sp : allStickerPacks){
            if(sp.getName().equals(stickerPackName)){
                return sp;
            }
        }
        return null;
    }

    public static StickerPack getStickerPackById(String stickerPackId){
        Log.w("THIS IS THE ALL STICKER", allStickerPacks.toString());
        for (StickerPack sp : allStickerPacks){
            if(sp.getIdentifier().equals(stickerPackId)){
                return sp;
            }
        }
        return null;
    }

    public static StickerPack getStickerPackByIdWithContext(String stickerPackId, Context context){
        if(allStickerPacks.isEmpty()){
            init(context);
        }
        Log.w("THIS IS THE ALL STICKER", allStickerPacks.toString());
        for (StickerPack sp : allStickerPacks){
            if(sp.getIdentifier().equals(stickerPackId)){
                return sp;
            }
        }
        return null;
    }
}
