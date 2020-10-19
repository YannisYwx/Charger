package com.sevenchip.charger.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.sevenchip.charger.R;

import java.lang.ref.WeakReference;

/**
 * File:com.ls.yannis.util.ToastUtil.java
 * @version V1.0 <描述当前版本功能>
 *          Email:923080261@qq.com
 * @Description: ${toast工具 控制弹出 可以避免重复弹出}
 * Author Yannis
 * Create on: 2017-07-27 12:49
 */
public class ToastUtil {
    private static Toast mToast;

    /**
     * 顯示一個自定義的吐司
     *
     * @param context 资源上下文
     * @param msg 显示的内容
     * @return Toast
     */
    public static final Toast show(Context context, String msg) {
        WeakReference<Context> mContext = new WeakReference<>(context);
        if (mToast == null) {
            mToast = Toast.makeText(mContext.get(), msg, Toast.LENGTH_SHORT);
            mToast.getView().setBackgroundResource(R.drawable.toast_black_background);
            TextView v = (TextView) mToast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.BLACK);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        }
        mToast.setText(msg);
        mToast.show();
        return mToast;
    }

    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
            mToast = null;//toast隐藏后，将其置为null
        }
    };

    /**
     * 顯示一個不會重複彈窗的土司
     *
     * @param context 资源上下文
     * @param message 显示的内容
     */
    public static void showShortToast(Context context, String message) {
        WeakReference<Context> mContext = new WeakReference<>(context);
        mHandler.removeCallbacks(r);
        if (mToast == null) {//只有mToast==null时才重新创建，否则只需更改提示文字
            mToast = Toast.makeText(mContext.get(), message, Toast.LENGTH_LONG);
            mToast.setGravity(Gravity.BOTTOM, 0, 150);
        }
        mToast.setText(message);
        mHandler.postDelayed(r, 2000);//延迟1秒隐藏toast
        mToast.show();
    }
}
