package com.app.comwallet.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.comwallet.schnorrkel.utils.HexUtils;

import io.emeraldpay.polkaj.ss58.SS58Codec;
import io.emeraldpay.polkaj.ss58.SS58Type;

public class WalletUtils {

    private static final String WALLET_PREFS_NAME = "wallet_prefs";
    private static final String SELECTED_WALLET_ID_KEY = "selected_wallet_id";

    public static void saveSelectedWalletId(Context context, long walletId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WALLET_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(SELECTED_WALLET_ID_KEY, walletId);
        editor.apply();
    }

    public static long loadSelectedWalletId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WALLET_PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(SELECTED_WALLET_ID_KEY, 1); // 1 indicates default wallet selected
    }

    public static String formatWalletAddress(String walletAddress) {
        String first = walletAddress.substring(0, 3);
        String last = walletAddress.substring(walletAddress.length() - 5);
        return first + "..." + last;
    }

    public static String encodeAddress(SS58Codec ss58Codec, String pubkey) {
        return ss58Codec.encode(SS58Type.Network.SUBSTRATE, HexUtils.hexToBytes(pubkey));
    }


}
