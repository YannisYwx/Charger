package com.sevenchip.charger.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.sevenchip.charger.R;
import com.sevenchip.charger.widget.AppMsg;

import static android.view.Gravity.BOTTOM;
import static android.view.Gravity.CENTER;

/**
 * @author : Yannis.Ywx
 * CreateTime : 2017/9/19  11:54
 * Description :
 */
public class UIUtils {
    static Context sContext;
    private static Handler sHandler = new Handler(Looper.getMainLooper());
    private static Toast sToast;

    private static Runnable r = new Runnable() {
        public void run() {
            sToast.cancel();
            //toast隐藏后，将其置为null
            sToast = null;
        }
    };

    public static void init(Context context) {
        if (context == null) {
            throw new NullPointerException("Context is null");
        }
        sContext = context.getApplicationContext();
    }

    public static Handler getMainHandler() {
        return sHandler;
    }

    public static void post(Runnable run) {
        postDelayed(run, 0L);
    }

    public static void postDelayed(Runnable run, long delayMillis) {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }
        sHandler.postDelayed(run, delayMillis);
    }

    public static Context getContext() {
        return sContext;
    }

    public static String[] getStringArray(@ArrayRes int res) {
        return sContext.getResources().getStringArray(res);
    }

    /**
     * 展示一个不会重复弹窗的toast
     *
     * @param context    上下文
     * @param message    消息
     * @param showLength 展示时长
     */
    public static void showToast(Context context, String message, int showLength) {
        sHandler.removeCallbacks(r);
        if (sToast == null) {
            //只有sToast==null时才重新创建，否则只需更改提示文字
            sToast = Toast.makeText(context, message, showLength);
            sToast.setGravity(BOTTOM, 0, 150);
        }
        sToast.setText(message);
        //延迟1秒隐藏toast
        sHandler.postDelayed(r, 1000);
        sToast.show();
    }

    public static void showShortToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showToast(String msg) {
        if (sContext == null) {
            throw new NullPointerException("UIUtils must call init Method first!");
        }
        showShortToast(sContext, msg);
    }

    public static void showToast(@StringRes int msgRes) {
        if (sContext == null) {
            throw new NullPointerException("UIUtils must call init Method first!");
        }
        showShortToast(sContext, sContext.getResources().getString(msgRes));
    }

    private static long sLastClickedTime;

    private static final long INTERVAL_TIME = 8_00;

    /**
     * 判断控件是或否被过高频率点击
     *
     * @return true
     */
    public static boolean isDoubleClick() {
        long currentClickTime = System.currentTimeMillis();
        long interval = currentClickTime - sLastClickedTime;
        if (interval > 0 && interval > INTERVAL_TIME) {
            return false;
        }
        sLastClickedTime = currentClickTime;
        return true;
    }

    public static void showInfo(AppCompatActivity activity, String msg) {
        AppMsg.cancelAll();
        AppMsg.Style style = new AppMsg.Style(1500, R.color.themeBluePre);
        AppMsg.makeText(activity, msg, style)
                .setLayoutGravity(CENTER)
                .setAnimation(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
//                .setParent(layoutRes)
                .show();
    }
}
