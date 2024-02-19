package com.app.comwallet.schnorrkel.utils;

/**
 * @Author:yong.huang
 * @Date:2020-08-01 00:23
 */
public class NumberUtils {

    public static long[] xorState(byte[] buf) {
        long[] state = new long[25];
        int offset = 0;
        for (int i = 0; i < buf.length / 8; i++) {
            long a = readInt64(buf, offset);
            state[i] ^= a;
            offset += 8;
        }
        return state;
    }

    public static long readInt64(byte[] bytes, int offset) {
        return (long) bytes[offset] & 255L |
                ((long) bytes[offset + 1] & 255L) << 8 |
                ((long) bytes[offset + 2] & 255L) << 16 |
                ((long) bytes[offset + 3] & 255L) << 24 |
                ((long) bytes[offset + 4] & 255L) << 32 |
                ((long) bytes[offset + 5] & 255L) << 40 |
                ((long) bytes[offset + 6] & 255L) << 48 |
                ((long) bytes[offset + 7] & 255L) << 56;
    }


    public static void int64ToBytes(long val, byte[] result, int offset) {
        result[offset] = (byte) (255L & val);
        result[offset + 1] = (byte) (255L & val >> 8);
        result[offset + 2] = (byte) (255L & val >> 16);
        result[offset + 3] = (byte) (255L & val >> 24);
        result[offset + 4] = (byte) (255L & val >> 32);
        result[offset + 5] = (byte) (255L & val >> 40);
        result[offset + 6] = (byte) (255L & val >> 48);
        result[offset + 7] = (byte) (255L & val >> 56);
    }


    public static void uint32ToBytes(int val, byte[] out, int offset) {
        out[offset] = (byte) ((int) (255L & val));
        out[offset + 1] = (byte) ((int) (255L & val >> 8));
        out[offset + 2] = (byte) ((int) (255L & val >> 16));
        out[offset + 3] = (byte) ((int) (255L & val >> 24));
    }

    public static void uint64ToBytes(long val, byte[] out, int offset) {
        out[offset] = (byte) ((int) (255L & val));
        out[offset + 1] = (byte) ((int) (255L & val >> 8));
        out[offset + 2] = (byte) ((int) (255L & val >> 16));
        out[offset + 3] = (byte) ((int) (255L & val >> 24));
        out[offset + 4] = (byte) ((int) (255L & val >> 32));
        out[offset + 5] = (byte) ((int) (255L & val >> 40));
        out[offset + 6] = (byte) ((int) (255L & val >> 48));
        out[offset + 7] = (byte) ((int) (255L & val >> 56));
    }

}
