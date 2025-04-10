package com.calibrage.palmroot.utils;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import com.calibrage.palmroot.R;

import com.calibrage.palmroot.cloudhelper.ApplicationThread;
import com.calibrage.palmroot.common.CommonUtils;

import java.util.LinkedHashMap;

public class UiUtils {

    public static final String LOG_TAG = UiUtils.class.getName();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    /*
     * If backgrount type 0(Zero) = Green
     *                    1        = Red*/
    public static void showCustomToastMessage(final String message, final Context context, final int backgroundColorType) {
        showCustomToastMessageLong(message,context, backgroundColorType, Toast.LENGTH_SHORT);
    }

    public static void showCustomToastMessageLong(final String message, final Context context, final int backgroundColorType, final int length) {
        ApplicationThread.uiPost(LOG_TAG, "show custom toast", new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {

                if (null == context)
                    return;

                LayoutInflater inflater = LayoutInflater.from(context);
                View toastRoot = inflater.inflate(R.layout.custom_toast, null);
                TextView messageToDisplay = (TextView) toastRoot.findViewById(R.id.toast_message);
                messageToDisplay.setBackground(context.getDrawable(backgroundColorType == 0 ? R.drawable.toast_msg_green : R.drawable.toast_bg));
                messageToDisplay.setText(message);
                Toast toast = new Toast(context);
                // Set layout to toast
                toast.setView(toastRoot);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(length);
                toast.show();
            }
        });
    }



    public static ArrayAdapter createAdapter(Context mContext, LinkedHashMap<String, String> dataMap, String spinnerType){
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item,
                CommonUtils.fromMap(dataMap, spinnerType));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return spinnerArrayAdapter;
    }
}
