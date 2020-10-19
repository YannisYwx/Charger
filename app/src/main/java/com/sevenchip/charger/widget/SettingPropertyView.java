package com.sevenchip.charger.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.utils.TextSpanUtils;

/**
 * @author : Alvin
 * create at : 2020/7/28 16:04
 * description : 设置属性的控件
 */
public class SettingPropertyView extends ConstraintLayout {
    private static final String TAG = SettingPropertyView.class.getSimpleName();

    ImageView ivAdd;
    ImageView ivSub;
    TextView tvLabel;
    TextView tvPropertyValue;
    TextView tvPropertyHint;
    AppCompatSeekBar mSeekBar;
    private String mLabel;
    private String mValue;
    private String mHint;
    private String mSymbol;
    private String mHintHighlight;
    private int mProgress = 35;
    private float mMinValue;
    private float mMaxValue;
    private boolean isDecimal = false;

    public SettingPropertyView(Context context) {
        this(context, null);
    }

    public SettingPropertyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingPropertyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SettingPropertyView);
            mLabel = ta.getString(R.styleable.SettingPropertyView_label);
            mSymbol = ta.getString(R.styleable.SettingPropertyView_symbol);
            mValue = ta.getString(R.styleable.SettingPropertyView_propertyValue);
            mHint = ta.getString(R.styleable.SettingPropertyView_hint);
            mHintHighlight = ta.getString(R.styleable.SettingPropertyView_hintHighlight);
            mMinValue = ta.getFloat(R.styleable.SettingPropertyView_min, 0f);
            mMaxValue = ta.getFloat(R.styleable.SettingPropertyView_max, 100f);
            mProgress = ta.getInteger(R.styleable.SettingPropertyView_progress, 35);
            isDecimal = ta.getBoolean(R.styleable.SettingPropertyView_isDecimal, false);
            ta.recycle();
        }
        LayoutInflater.from(context).inflate(R.layout.view_set_channel_property, this, true);
    }

    public void setChargerProperty(@NonNull String label,
                                   @NonNull String value,
                                   @NonNull String hint,
                                   @NonNull String hintHighLight, float min, float max, int progress) {
        this.mMinValue = min;
        this.mMaxValue = max;
        tvLabel.setText(label);
        tvPropertyValue.setText(value);
        TextSpanUtils.getInstant().setHighLight(tvPropertyHint, hint, hintHighLight, Color.WHITE);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(progress);
        invalidate();
    }

    public void setChargerProperty(@NonNull String value, float min, float max, int progress) {
        this.mMinValue = min;
        this.mMaxValue = max;
        tvPropertyValue.setText(value);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(progress);
        invalidate();
    }

    private int currentValue;

    public void setChargerProperty(@NonNull String value,
                                   @NonNull String hint,
                                   @NonNull String hintHighLight, float min, float max) {
        this.mMinValue = min;
        this.mMaxValue = max;
        this.mHint = hint;
        this.mHintHighlight = hintHighLight;
        if (!TextUtils.isEmpty(mHint) && !TextUtils.isEmpty(mHintHighlight) && mHint.contains(mHintHighlight)) {
            TextSpanUtils.getInstant().setHighLight(tvPropertyHint, mHint, mHintHighlight, Color.WHITE);
        }
        tvPropertyValue.setText(String.format("%s%s", value, mSymbol));
        if (isDecimal) {
            currentValue = (int) (Float.parseFloat(value) * 10);
        } else {
            currentValue = Integer.parseInt(value);
        }
        initProgressbar();
        invalidate();
    }

    public void setChargerProperty(float min, float max, String value) {
        this.mMinValue = min;
        this.mMaxValue = max;
        tvPropertyValue.setText(String.format("%s%s", value, mSymbol));
        if (isDecimal) {
            currentValue = (int) (Float.parseFloat(value) * 10);
        } else {
            currentValue = (int) Float.parseFloat(value);
        }
        initProgressbar();
        invalidate();
    }

    private void initProgressbar() {
        if (isDecimal) {
            mMinValue = mMinValue * 10;
            mMaxValue = mMaxValue * 10;
        }
        min = (int) mMinValue;
        max = (int) mMaxValue;
        mSeekBar.setMax(max - min);
        currentValue = Math.max(min, currentValue);
        currentValue = Math.min(max, currentValue);
        currentValue -= min;
        mSeekBar.setProgress(currentValue);
    }

    private boolean isDecimal() {
        return isDecimal;
    }

    private int min;
    private int max;

    public int getValue() {
        return mSeekBar.getProgress() + min;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvLabel = findViewById(R.id.tv_label_property);
        tvPropertyValue = findViewById(R.id.tv_current_value);
        tvPropertyHint = findViewById(R.id.tv_hint_property);
        ivAdd = findViewById(R.id.iv_add);
        ivSub = findViewById(R.id.iv_sub);
        mSeekBar = findViewById(R.id.sb_property);
        if (!TextUtils.isEmpty(mHint) && !TextUtils.isEmpty(mHintHighlight) && mHint.contains(mHintHighlight)) {
            TextSpanUtils.getInstant().setHighLight(tvPropertyHint, mHint, mHintHighlight, Color.WHITE);
        }
        tvLabel.setText(TextUtils.isEmpty(mLabel) ? "-" : mLabel);
        tvPropertyValue.setText(TextUtils.isEmpty(mValue) ? "-" : mValue);
        mSeekBar.setMax(100);
        mSeekBar.setProgress(mProgress);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String v;
                int pV = progress + min;
                if (isDecimal) {
                    float fp = pV / 10.0f;
                    v = fp + mSymbol;
                } else {
                    v = pV + mSymbol;
                }
                tvPropertyValue.setText(v);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ivAdd.setOnClickListener(view -> {
            int stepSize = getStepSize();
            int tempP = mSeekBar.getProgress();
            tempP = tempP + stepSize;
            tempP = Math.min(tempP, max);
            mSeekBar.setProgress(tempP);
        });

        ivSub.setOnClickListener(view -> {
            int stepSize = getStepSize();
            int tempP = mSeekBar.getProgress();
            tempP = tempP - stepSize;
            tempP = Math.max(tempP, 0);
            mSeekBar.setProgress(tempP);
        });
    }

    public int getStepSize() {
//        return max > 300 ? max / 100 : 1;
        return 1;
    }
}
