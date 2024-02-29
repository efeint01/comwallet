package com.app.comwallet.views;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.comwallet.R;

public class CenteredToast {

    public static void show(Context context, String message) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_layout, null);

        // Set the text for the toast
        TextView text = layout.findViewById(R.id.textViewToast);
        text.setText(message);

        // Create the toast and set its view and gravity
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.setGravity(Gravity.CENTER, 0, 0); // Centered

        // Show the toast
        toast.show();
    }
}