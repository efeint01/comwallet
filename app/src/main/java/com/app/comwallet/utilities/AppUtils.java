package com.app.comwallet.utilities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.DecimalFormat;

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    // Copy text to the clipboard
    public static void copyToClipboard(Context context, String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copied_text", textToCopy);
        clipboard.setPrimaryClip(clip);
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "getAppVersionName: " + e.getMessage());
            return "";
        }
    }

    public static String formatAmount(long amount) {
        // Define the number of decimal places for formatting
        DecimalFormat df = new DecimalFormat("#,##0.00");
        // Divide balance by 10^9 to get the amount in COM
        double comAmount = (double) amount / 1_000_000_000;
        String formattedBalance = df.format(comAmount);
        // Return the formatted balance
        return formattedBalance + " COM";
    }


}
