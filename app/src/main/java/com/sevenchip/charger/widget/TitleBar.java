package com.sevenchip.charger.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sevenchip.charger.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.sevenchip.charger.widget.TitleBar.Event.buttonLeft;
import static com.sevenchip.charger.widget.TitleBar.Event.buttonRight;

/**
 * @author : Yannis.Ywx
 * @createTime : 2017/9/28  16:03
 * @email : 923080261@qq.com
 * @description : 标题导航
 */
public class TitleBar extends RelativeLayout implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private ImageView ivLeft;
    private ImageView ivRight;
    private ImageView iv2Right;
    private TextView tvTitle;
    private TextView tvLeft;
    private TextView tvRight;
    private RadioGroup rgGroup;
    private RadioButton rbLeft, rbRight;
    private Drawable leftDrawable, rightDrawable, right2Drawable;
    private String title, leftBtnText, rightBtnText, leftText, rightText;
    private int centerMode, rightIcon1Visibility, rightIcon2Visibility;
    private int titleColor, leftTextColor, rightTextColor;
    private OnEventTriggerListener mListener;

    @IntDef({Event.textLeft, Event.textRight, Event.textTitle, Event.imageLeft, Event.imageRight, Event.imageRight2, buttonLeft, buttonRight})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Event {
        int textLeft = 0;
        int textRight = 1;
        int textTitle = 2;
        int imageLeft = 3;
        int imageRight = 4;
        int imageRight2 = 5;
        int buttonLeft = 6;
        int buttonRight = 7;
    }

    public interface OnEventTriggerListener {
        void onEventTrigger(@Event int type);
    }

    public void setOnEventTriggerListener(@NonNull OnEventTriggerListener listener) {
        this.mListener = listener;
    }

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this, true);
        if (attrs == null) {
            return;
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
        leftDrawable = a.getDrawable(R.styleable.TitleBar_leftDrawable);
        rightDrawable = a.getDrawable(R.styleable.TitleBar_rightDrawable);
        right2Drawable = a.getDrawable(R.styleable.TitleBar_right2Drawable);
        title = a.getString(R.styleable.TitleBar_centerTitle);
        leftBtnText = a.getString(R.styleable.TitleBar_leftBtnText);
        rightBtnText = a.getString(R.styleable.TitleBar_rightBtnText);
        leftText = a.getString(R.styleable.TitleBar_lfText);
        rightText = a.getString(R.styleable.TitleBar_rtText);
        centerMode = a.getInt(R.styleable.TitleBar_centerMode, 0);
        titleColor = a.getColor(R.styleable.TitleBar_titleColor, Color.parseColor("#333333"));
        leftTextColor = a.getColor(R.styleable.TitleBar_leftTextColor, Color.parseColor("#666666"));
        rightTextColor = a.getColor(R.styleable.TitleBar_rightTextColor, Color.parseColor("#666666"));
        rightIcon1Visibility = a.getInt(R.styleable.TitleBar_rightIconVisibility, 0);
        rightIcon2Visibility = a.getInt(R.styleable.TitleBar_rightIcon2Visibility, 0);
        a.recycle();
    }

    public void setLeftVisialbe(boolean visialbe) {
        if (!visialbe) {
            ivLeft.setVisibility(View.GONE);
        }
    }

    public void setButtonChecked(boolean isLeft) {
        rbLeft.setChecked(isLeft);
        rbRight.setChecked(!isLeft);
        invalidate();
    }

    @Override
    protected void onFinishInflate() {
        ivLeft = findViewById(R.id.ib_titleBar_left);
        ivRight = findViewById(R.id.ib_titleBar_right);
        iv2Right = findViewById(R.id.ib_titleBar_right_ot);
        tvTitle = findViewById(R.id.tv_titleBar_title);
        tvLeft = findViewById(R.id.tv_titleBar_left);
        tvRight = findViewById(R.id.tv_titleBar_Right);
        rgGroup = findViewById(R.id.rg_titleBar);
        rbLeft = findViewById(R.id.rb_titleBar_left);
        rbRight = findViewById(R.id.rb_titleBar_right);
        tvTitle.setTextColor(titleColor);
        tvLeft.setTextColor(leftTextColor);
        tvRight.setTextColor(rightTextColor);
        if (!TextUtils.isEmpty(leftText)) {
            tvLeft.setText(leftText);
        } else {
            tvLeft.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(rightText)) {
            tvRight.setText(rightText);
        } else {
            tvRight.setVisibility(GONE);
        }

        if (leftDrawable != null) {
            ivLeft.setVisibility(VISIBLE);
            ivLeft.setImageDrawable(leftDrawable);
        } else {
            ivLeft.setVisibility(GONE);
        }

        if (rightDrawable != null && rightIcon1Visibility == 0) {
            ivRight.setVisibility(VISIBLE);
            ivRight.setImageDrawable(rightDrawable);
        } else {
            ivRight.setVisibility(GONE);
        }

        if (right2Drawable != null && rightIcon2Visibility == 0) {
            iv2Right.setVisibility(VISIBLE);
            iv2Right.setImageDrawable(right2Drawable);
        } else {
            iv2Right.setVisibility(GONE);
        }

        if (centerMode == 0) {
            rgGroup.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setText(title);
            } else {
                tvTitle.setVisibility(GONE);
            }
        } else {
            rgGroup.setVisibility(View.VISIBLE);
            rbLeft.setText(TextUtils.isEmpty(leftBtnText) ? "" : leftBtnText);
            rbRight.setText(TextUtils.isEmpty(rightBtnText) ? "" : rightBtnText);
        }

        rgGroup.setOnCheckedChangeListener(this);

        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        iv2Right.setOnClickListener(this);
        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        tvTitle.setOnClickListener(this);
        super.onFinishInflate();
    }

    public TextView getRightTextView() {
        return tvRight;
    }

    public TextView getLeftTextView() {
        return tvLeft;
    }

    public ImageView getLeftImageView() {
        return ivLeft;
    }

    public ImageView getRightImageView() {
        return ivRight;
    }

    public TextView getTitleView() {
        return tvTitle;
    }

    public RadioGroup getCenterGroup() {
        return rgGroup;
    }

    public void setiv2RightVisiable(boolean visiable) {
        if (iv2Right != null) {
            if (visiable) {
                iv2Right.setVisibility(View.VISIBLE);
            } else {
                iv2Right.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void setRightImageViewRes(@DrawableRes int ivRes) {
        ivRight.setVisibility(VISIBLE);
        ivRight.setImageResource(ivRes);
        invalidate();
    }

    @Override
    public void onClick(View view) {
        if (mListener == null) {
            return;
        }
        int id = view.getId();
        if (id == R.id.tv_titleBar_left) {
            mListener.onEventTrigger(Event.textLeft);
        } else if (id == R.id.tv_titleBar_Right) {
            mListener.onEventTrigger(Event.textRight);
        } else if (id == R.id.tv_titleBar_title) {
            mListener.onEventTrigger(Event.textTitle);
        } else if (id == R.id.ib_titleBar_left) {
            mListener.onEventTrigger(Event.imageLeft);
        } else if (id == R.id.ib_titleBar_right) {
            mListener.onEventTrigger(Event.imageRight);
        } else if (id == R.id.ib_titleBar_right_ot) {
            mListener.onEventTrigger(Event.imageRight2);
        }

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        if (checkedId == R.id.rb_titleBar_left) {
            rbLeft.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_toggle_btn_fg_right));
            rbRight.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_toggle_btn_fg_normal));
            if (mListener == null) {
                return;
            }
            mListener.onEventTrigger(buttonLeft);

        } else if (checkedId == R.id.rb_titleBar_right) {
            rbRight.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_toggle_btn_fg_left));
            rbLeft.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_toggle_btn_fg_normal));
            if (mListener == null) {
                return;
            }
            mListener.onEventTrigger(buttonRight);
        }
    }


    public void setLeftText(@NonNull String text) {
        ivLeft.setVisibility(GONE);
        tvLeft.setVisibility(VISIBLE);
        tvLeft.setText(text);
        invalidate();
    }

    public void setRightText(@NonNull String text) {
        tvRight.setVisibility(VISIBLE);
        tvRight.setText(text);
        invalidate();
    }

    public void setTitle(@NonNull String title) {
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(title);
        invalidate();
    }

    public void setTitleColor(int color) {
        tvTitle.setTextColor(color);
    }

    public void setLeftTextColor(int color) {
        tvLeft.setTextColor(color);
    }

    public void setRightTextColor(int color) {
        tvRight.setTextColor(color);
    }

    public void setLeftDrawable(Drawable drawable) {
        ivLeft.setVisibility(VISIBLE);
        ivLeft.setImageDrawable(drawable);
        invalidate();
    }

    public void setRightDrawable(Drawable drawable) {
        ivRight.setVisibility(VISIBLE);
        ivRight.setImageDrawable(drawable);
        invalidate();
    }

    public void setRight2Drawable(Drawable drawable) {
        iv2Right.setVisibility(VISIBLE);
        iv2Right.setImageDrawable(drawable);
        invalidate();
    }

}
