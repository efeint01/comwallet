package com.app.comwallet.schnorrkel.sign;

import com.app.comwallet.schnorrkel.merlin.Strobe128;
import com.app.comwallet.schnorrkel.merlin.Transcript;
import com.app.comwallet.schnorrkel.merlin.TranscriptRng;
import com.app.comwallet.schnorrkel.merlin.TranscriptRngBuilder;

import cafe.cryptography.curve25519.CompressedRistretto;
import cafe.cryptography.curve25519.Scalar;

/**
 * @Author:yong.huang
 * @Date:2020-08-02 16:56
 */
public class SigningTranscript extends Transcript {

    public SigningTranscript(Strobe128 strobe128) {
        super(strobe128);
    }

    public void proto_name(byte[] label) throws Exception {
        commit_bytes("proto-name".getBytes(), label);
    }

    public void commit_point(byte[] label, CompressedRistretto compressed) throws Exception {
        this.commit_bytes(label, compressed.toByteArray());
    }

    public Scalar witness_scalar(byte[] label, byte[] nonce_seeds) throws Exception {
        byte[] scalar_bytes = new byte[64];
        this.witness_bytes(label, scalar_bytes, nonce_seeds);
        Scalar scalar = Scalar.fromBytesModOrderWide(scalar_bytes);
        return scalar;
    }

    public void witness_bytes(byte[] label, byte[] dest, byte[] nonce_seeds) throws Exception {
        this.witness_bytes_rng(label, dest, nonce_seeds);
    }

    public void witness_bytes_rng(byte[] label, byte[] dest, byte[] nonce_seeds) throws Exception {
        TranscriptRngBuilder br = build_rng();
        br = br.commit_witness_bytes(label, nonce_seeds);
        TranscriptRng r = br.tFinalize();
        r.fill_bytes(dest);
    }

    public Scalar challenge_scalar(byte[] label) throws Exception {
        byte[] buf = new byte[64];
        this.challenge_bytes(label, buf);
        Scalar scalar = Scalar.fromBytesModOrderWide(buf);
        return scalar;
    }
}
