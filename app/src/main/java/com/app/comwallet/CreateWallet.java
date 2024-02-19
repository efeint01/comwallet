package com.app.comwallet;

import com.app.comwallet.schnorrkel.sign.ExpansionMode;
import com.app.comwallet.schnorrkel.sign.KeyPair;
import com.app.comwallet.schnorrkel.sign.PrivateKey;
import com.app.comwallet.schnorrkel.sign.PublicKey;
import com.app.comwallet.schnorrkel.utils.HexUtils;
import com.app.comwallet.utilities.MnemonicUtils;

import io.emeraldpay.polkaj.ss58.SS58Type;
import io.emeraldpay.polkaj.types.Address;
import io.github.novacrypto.bip39.SeedCalculator;

public class CreateWallet {
    public static void main(String[] args) {
        // CREATE WALLET
        String mnemonic_phrase = MnemonicUtils.generateMnemonic();
        byte[] seed = new SeedCalculator().calculateSeed(mnemonic_phrase, "");
        KeyPair keyPair = KeyPair.fromSecretSeed(seed, ExpansionMode.Ed25519);
        PrivateKey privateKey = keyPair.getPrivateKey();
        PublicKey publicKey = keyPair.getPublicKey();
        Address address = new Address(SS58Type.Network.SUBSTRATE, publicKey.toPublicKey());


        System.out.println("*** ---- Wallet Created ---- ***\n");

        System.out.println("mnemonic phrase: " + mnemonic_phrase); //encrypt that
        System.out.println("seed: " + HexUtils.bytesToHex(seed));
        System.out.println("public key: " + HexUtils.bytesToHex(publicKey.toPublicKey()));
        System.out.println("private key: " + HexUtils.bytesToHex(privateKey.getKey()));
        System.out.println("ss58 address: " + address + "\n");
    }
}
