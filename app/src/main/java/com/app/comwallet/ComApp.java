package com.app.comwallet;

import android.app.Application;

import androidx.room.Room;

import com.app.comwallet.database.WalletDatabase;

public class ComApp extends Application {
    public static WalletDatabase database;
    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(),
                        WalletDatabase.class, "wallet_database")
                .build();
    }
}