package com.app.comwallet.schnorrkel.sign;


import com.app.comwallet.schnorrkel.merlin.Transcript;

/**
 * @Author:yong.huang
 * @Date:2020-08-02 16:48
 */
public class SigningContext {

    private Transcript transcript;

    public SigningContext(Transcript transcript) {
        this.transcript = transcript;
    }

    public static SigningContext createSigningContext(byte[] context) throws Exception {
        Transcript t = Transcript.createTranscript("SigningContext".getBytes());
        t.append_message("".getBytes(), context);
        return new SigningContext(t);
    }

    public SigningTranscript bytes(byte[] bytes) throws Exception {
        SigningTranscript t = new SigningTranscript(this.transcript.getStrobe());
        t.append_message("sign-bytes".getBytes(), bytes);
        return t;
    }

}
