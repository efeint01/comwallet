package com.app.comwallet.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.comwallet.models.Wallet;

@Database(entities = {Wallet.class}, version = 1)
public abstract class WalletDatabase extends RoomDatabase {
    public abstract WalletDao walletDao();
}