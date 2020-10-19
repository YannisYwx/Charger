package com.sevenchip.charger.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.sevenchip.charger.R;

/**
 * @author : Alvin
 * create at : 2020/7/28 16:04
 * description : 充电属性控件
 */
public class ChargerPropertyView extends ConstraintLayout {
    TextView tvLabel;
    TextView tvValue;
    TextView tvUnit;
    private String label;
    private String value;
    private String unit;

    public ChargerPropertyView(Context context) {
        this(context, null);
    }

    public ChargerPropertyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChargerPropertyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChargerPropertyView);
            label = ta.getString(R.styleable.ChargerPropertyView_label);
            value = ta.getString(R.styleable.ChargerPropertyView_propertyValue);
            unit = ta.getString(R.styleable.ChargerPropertyView_unit);
            ta.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.view_charger_property, this, true);
    }

    public void setChargerProperty(@NonNull String label, @NonNull String value, @NonNull String unit) {
        tvLabel.setText(label);
        tvValue.setText(value);
        tvUnit.setText(unit);
        invalidate();
    }

    public void setChargerProperty(@NonNull String value) {
        tvValue.setText(value);
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvLabel = findViewById(R.id.tv_label);
        tvValue = findViewById(R.id.tv_value);
        tvUnit = findViewById(R.id.tv_unit);
        tvLabel.setText(TextUtils.isEmpty(label) ? "-" : label);
        tvValue.setText(TextUtils.isEmpty(value) ? "-" : value);
        tvUnit.setText(TextUtils.isEmpty(unit) ? "" : unit);
    }
}
