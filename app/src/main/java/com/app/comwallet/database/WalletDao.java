package com.app.comwallet.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.comwallet.models.Wallet;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WalletDao {
    @Insert
    void insertWallet(Wallet wallet);

    @Query("SELECT * FROM wallets")
    List<Wallet> getAllWallets();

    @Query("SELECT * FROM wallets ORDER BY id DESC LIMIT 1")
    Wallet getLastInsertedWallet();

    @Query("SELECT * FROM wallets WHERE id = :selectedWalletId")
    Wallet getSelectedWallet(long selectedWalletId);

    @Update
    void updateWallet(Wallet wallet);

    @Query("UPDATE wallets SET name = :newName WHERE id = :walletId")
    void renameWallet(long walletId, String newName);

    @Delete
    void deleteWallet(Wallet wallet);
}
