package com.app.comwallet.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.app.comwallet.database.WalletDao;

@Entity(tableName = "wallets")
public class Wallet {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String mnemonic;
    private String privateKey;
    private String publicKey;

    public Wallet(String name, String mnemonic, String privateKey, String publicKey) {
        this.name = name;
        this.mnemonic = mnemonic;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public static String generateWalletName(WalletDao walletDao) {
        Wallet lastInsertedWallet = walletDao.getLastInsertedWallet();
        if (lastInsertedWallet != null) {
            long lastInsertedId = lastInsertedWallet.getId();
            return "Wallet " + (lastInsertedId + 1);
        } else {
            return "Wallet 1";
        }
    }


}