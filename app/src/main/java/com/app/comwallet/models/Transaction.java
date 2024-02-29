package com.app.comwallet.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Transaction implements Parcelable {
    private int id;
    private String timestamp;
    private String method;
    private String sender;
    private String receiver;
    private String amount;
    private int blockNumber;
    private boolean isSigned;
    private String hash;
    private int nonce;
    private String fee;

    public Transaction(int id, String timestamp, String method, String sender, String receiver, String amount, int blockNumber, boolean isSigned, String hash, int nonce, String fee) {
        this.id = id;
        this.timestamp = timestamp;
        this.method = method;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.blockNumber = blockNumber;
        this.isSigned = isSigned;
        this.hash = hash;
        this.nonce = nonce;
        this.fee = fee;
    }

    protected Transaction(Parcel in) {
        id = in.readInt();
        timestamp = in.readString();
        method = in.readString();
        sender = in.readString();
        receiver = in.readString();
        amount = in.readString();
        blockNumber = in.readInt();
        isSigned = in.readByte() != 0;
        hash = in.readString();
        nonce = in.readInt();
        fee = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public void setSigned(boolean signed) {
        isSigned = signed;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public static String getFormattedTimestamp(String inputTimestamp) {
        String formattedTimestamp = null;

        // Create a SimpleDateFormat object with the desired output format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());

        // Set the timezone to UTC
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            // Parse the timestamp string to Date object
            Date date = inputFormat.parse(inputTimestamp);

            // Format the Date object to the desired output format
            formattedTimestamp = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedTimestamp;
    }


    public static String getFormattedTimestampDate(String inputTimestamp) {
        String formattedTimestamp = null;

        // Create a SimpleDateFormat object with the desired output format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

        // Set the timezone to UTC
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            // Parse the timestamp string to Date object
            Date date = inputFormat.parse(inputTimestamp);

            // Format the Date object to the desired output format
            formattedTimestamp = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedTimestamp;
    }

    public static String getFormattedTimestampTime(String inputTimestamp) {
        String formattedTimestamp = null;

        // Create a SimpleDateFormat object with the desired output format
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        // Set the timezone to UTC
        inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            // Parse the timestamp string to Date object
            Date date = inputFormat.parse(inputTimestamp);

            // Format the Date object to the desired output format
            formattedTimestamp = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedTimestamp;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(timestamp);
        dest.writeString(method);
        dest.writeString(sender);
        dest.writeString(receiver);
        dest.writeString(amount);
        dest.writeInt(blockNumber);
        dest.writeByte((byte) (isSigned ? 1 : 0));
        dest.writeString(hash);
        dest.writeInt(nonce);
        dest.writeString(fee);
    }
}