package com.sevenchip.charger.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.sevenchip.charger.R;

/**
 * @author : Alvin
 * create at : 2020/7/28 15:09
 * description :
 */
public class VoltageView extends ConstraintLayout {
    TextView tvVoltage;
    TextView tvVoltageSecond;
    ImageView ivPower;
    private Drawable powerIcon;
    private String voltageValue;
    private String voltageS;
    public VoltageView(Context context) {
        this(context, null);
    }

    public VoltageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoltageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VoltageView);
            powerIcon = ta.getDrawable(R.styleable.VoltageView_powerIcon);
            voltageValue = ta.getString(R.styleable.VoltageView_voltage);
            voltageS = ta.getString(R.styleable.VoltageView_second);
            ta.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.view_valtage_info, this, true);
    }

    public void setVoltageInfo(@NonNull Bitmap bitmap, @NonNull String voltage, @NonNull String voltageSecond) {
        tvVoltage.setText(voltage);
        tvVoltageSecond.setText(voltageSecond);
        ivPower.setImageBitmap(bitmap);
        invalidate();
    }

    public void setVoltageInfo(@DrawableRes int res, @NonNull String voltage, @NonNull String voltageSecond) {
        tvVoltage.setText(voltage);
        tvVoltageSecond.setText(voltageSecond);
        ivPower.setImageBitmap(BitmapFactory.decodeResource(getResources(), res));
        invalidate();
    }

    public void setVoltageInfo(@DrawableRes int res, @NonNull String voltage) {
        tvVoltage.setText(voltage);
        ivPower.setImageBitmap(BitmapFactory.decodeResource(getResources(), res));
        invalidate();
    }

    public void setVoltageInfo(@NonNull String voltage) {
        tvVoltage.setText(voltage);
        invalidate();
    }

    public void setVoltageInfo(@NonNull String voltage, @NonNull String voltageSecond) {
        tvVoltage.setText(voltage);
        tvVoltageSecond.setText(voltageSecond);
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvVoltage = findViewById(R.id.tv_voltage);
        tvVoltageSecond = findViewById(R.id.tv_voltage_s);
        ivPower = findViewById(R.id.iv_voltage);
        tvVoltage.setText(TextUtils.isEmpty(voltageValue) ? "-" : voltageValue);
        tvVoltageSecond.setText(TextUtils.isEmpty(voltageS) ? "6S" : voltageS);
        ivPower.setImageDrawable(powerIcon);
    }
}
