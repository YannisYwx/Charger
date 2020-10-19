package com.sevenchip.charger.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

/**
 * @author : Alvin
 * CreateTime : 2017/9/16  11:24
 * Description : 视图工具类：
 * 获取屏幕宽高，字体位置
 */
public class ViewUtils {

    /**
     * 获取缩放比例
     *
     * @param context 上下文
     * @return scale
     */
    public static float getScale(Context context){
        return context.getResources().getDisplayMetrics().density;
    }

    /**
     * dp转px
     * @param context 上下文
     * @param dipValue dip
     * @return px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转dp
     * @param context 上下文
     * @param pxValue px
     * @return dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * px转sp
     * @param context 上下文
     * @param pxValue px
     * @return sp
     */
    public static float px2sp(Context context, float pxValue){
        return (pxValue / context.getResources().getDisplayMetrics().scaledDensity);
    }


    /**
     * sp转px
     * @param context 上下文
     * @param spValue sp
     * @return px
     */
    public static int sp2px(Context context, int spValue){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spValue, context.getResources().getDisplayMetrics());
    }

    /**
     * @param paint 测试前，必须保证paint已经设置过textSize , Typeface;
     * @return 该paint画出文字的理应高度
     */
    public static float getTextHeight(Paint paint) {
        Paint.FontMetrics m = paint.getFontMetrics();
        return m.bottom - m.top;
    }

    /**
     * 获取文字的长度
     * @param paint 画笔
     * @param content 文字
     * @return 文字长度
     */
    public static float getTextRectWidth(Paint paint, String content) {
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        return paint.measureText(content);
    }

    /**
     * 这个返回值<={@link #getTextHeight(Paint)}
     *
     * @return 文字的高度
     */
    public static float getTextRectHeight(Paint paint, String content) {
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect.height();
    }

    /**
     * 矫正text的y轴位置
     *
     * @param centerY 想要文字的y轴位置
     * @param mPaint 画笔
     * @return y轴坐标
     */
    public static float correctTextY(float centerY, Paint mPaint) {
        return centerY - (mPaint.ascent() + mPaint.descent()) / 2.0f;
    }

    /**
     * @param b       需要模糊的bitmap
     * @param context 上下文
     * @return 模糊后的bitmap
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Bitmap b, Context context) {
        RenderScript rs = RenderScript.create(context);
        Allocation overlayAlloc = Allocation.createFromBitmap(rs, b);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, overlayAlloc.getElement());
        blur.setInput(overlayAlloc);
        blur.setRadius(25);
        blur.forEach(overlayAlloc);
        overlayAlloc.copyTo(b);
        return b;
    }


    /**
     * 获取屏幕高度
     * @param context 当前屏幕
     * @return 高度值
     */
    public static int  getScreenHeight(Context context){
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.heightPixels;
    }


    /**
     * 获取屏幕宽
     * @param context 当前屏幕
     * @return 宽值
     */
    public static int  getScreenWidth(Context context){
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.widthPixels;
    }


    public static DisplayMetrics getDisplayMetrics(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }



}
