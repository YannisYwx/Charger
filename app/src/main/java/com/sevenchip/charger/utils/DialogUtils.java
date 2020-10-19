package com.sevenchip.charger.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

import com.sevenchip.charger.R;

/**
 * Author : Alvin
 * CreateTime : 2020/8/15 16:37
 * Description :
 */
public class DialogUtils {

    public static void showMessageDialog(Context context, String title, String message, OnDialogClickListener listener) {
        final Dialog dialog = new Dialog(context, R.style.aDialog);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_message);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
        }
        dialog.getWindow().setWindowAnimations(R.style.dialogAnim);
        dialog.getWindow().getAttributes().width = (int) (((Activity) context).getWindowManager().getDefaultDisplay().getWidth() * 0.8f);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_msg);
        tvTitle.setText(title);
        tvMessage.setText(message);
        dialog.findViewById(R.id.tv_cancel).setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancel();
            }
            dialog.dismiss();
        });
        dialog.findViewById(R.id.tv_sure).setOnClickListener(v -> {
            if (listener != null) {
                listener.onSure();
            }
            dialog.dismiss();
        });
        dialog.show();
    }


    public interface OnDialogClickListener {
        void onCancel();

        void onSure();
    }
}
