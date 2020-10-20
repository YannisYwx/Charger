package com.sevenchip.charger.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.data.status.WifiStatus;
import com.sevenchip.charger.utils.ToastUtil;
import com.sevenchip.charger.utils.WifiStatusChangedReceiver;
import com.sevenchip.charger.widget.AppMsg;
import com.sevenchip.charger.widget.TitleBar;

import butterknife.ButterKnife;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import static com.sevenchip.charger.widget.AppMsg.LENGTH_LONG;
import static com.sevenchip.charger.widget.AppMsg.LENGTH_SHORT;
import static com.sevenchip.charger.widget.AppMsg.LENGTH_STICKY;

/**
 * Author : Alvin
 * CreateTime : 2020/7/18 22:20
 * Description :
 */
public abstract class BaseActivity extends SwipeBackActivity implements View.OnClickListener,
        WifiStatusChangedReceiver.OnWifStatusChangerListener, TitleBar.OnEventTriggerListener {
    public static final String EXTRA = "Extra";
    protected TitleBar mTitleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(setLayoutResId());
        steepStatusBar();
        //StatusBarUtils.statusBarLightMode(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.BLACK);
        }
        setAndroidNativeLightStatusBar(this, false);
        ButterKnife.bind(this);
        init();
        initView(savedInstanceState);
        initEvent();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        WifiStatusChangedReceiver.getInstance().addWifStatusChangerListener(this);
    }

    @Override
    public View findViewById(int id) {
        return super.findViewById(id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WifiStatusChangedReceiver.getInstance().removeWifStatusChangerListener(this);
    }

    private static void setAndroidNativeLightStatusBar(AppCompatActivity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 沉浸状态栏
     */
    private void steepStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }


    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 初始化数据
     */
    public void initData() {
        if (isShowToolbar()) {
            mTitleBar = (TitleBar) findViewById(R.id.title_bar);
            /*mTitleBar.setTitle(initTitle());*/
            /*mTitleBar.setVisibility(isShowToolbar() ? View.VISIBLE : View.GONE);*/
            mTitleBar.setOnEventTriggerListener(this);
            initTitleBar();
        }
    }

    public boolean isShowToolbar() {
        return true;
    }

    /**
     * 初始化titleBar
     */
    public void initTitleBar() {

    }

    /**
     * 绑定事件
     */
    protected abstract void initEvent();

    /**
     * 初始化视图
     *
     * @param savedInstanceState 保存数据
     */
    protected void initView(@Nullable Bundle savedInstanceState) {

    }

    /**
     * 设置布局文件
     *
     * @return 布局文件id
     */
    protected abstract int setLayoutResId();

    /**
     * 注册点击事件
     *
     * @param args 需要绑定的控件
     */
    protected void registerClickEvent(View... args) {
        for (View view : args) {
            if (view != null) {
                view.setOnClickListener(this);
            }
        }
    }

    /**
     * 弹出一个土司
     *
     * @param msg 显示内容
     */
    public void showToast(final String msg) {
        runOnUiThread(() -> ToastUtil.showShortToast(BaseActivity.this, msg));
    }

    /**
     * 带参数切换activity
     *
     * @param bundle
     * @param cls
     */
    protected void switchTo(Bundle bundle, Class<? extends AppCompatActivity> cls) {
        final Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null) {
            intent.putExtra(EXTRA, bundle);
        }
        intent.setClass(this, cls);
        startActivity(intent);
    }

    /**
     * 不带参数切换activity
     *
     * @param cls
     */
    protected void switchTo(Class<? extends AppCompatActivity> cls) {
        switchTo(null, cls);
    }

    /**
     * 不带参数切换activity，并且销毁当前Activity
     *
     * @param cls
     */
    public void switchToAndFinish(Class<? extends AppCompatActivity> cls) {
        switchTo(cls);
        finish();
    }

    @Override
    public void onWifiStatusChanger(int status, String SSID) {
        if (isShowAppMsg()) {
            showAppMsg(status, SSID);
        }
    }

    AppMsg provided;

    private int appMsgParentId = -1;

    public void setAppMsgParentId(int id) {
        this.appMsgParentId = id;
    }

    public void dismissAllMsg() {
        if (provided != null) {
            provided.cancel();
            provided = null;
        }
    }

    public void showWornAppMsg() {
        if (provided != null) {
            provided.cancel();
            provided = null;
        }
        AppMsg.Style style = new AppMsg.Style(LENGTH_STICKY, R.color.alert);
        int priority = AppMsg.PRIORITY_HIGH;
        String msg = "Please stop charger device!";
        int imgRes = R.mipmap.alarm;
        provided = AppMsg.makeText(this, msg, style, R.layout.sticky);
        provided.setPriority(priority);
        View view = provided.getView();
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imgRes);
        view.findViewById(R.id.remove_btn).setOnClickListener(v -> {
            if (provided != null) {
                provided.cancel();
                provided = null;
            }
        });
        if (isShowToolbar()) {
            provided.setParent(R.id.title_bar);
        }
        provided.setAnimation(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        provided.show();
    }

    private void showAppMsg(@WifiStatus int status, String ssid) {
        if (provided != null) {
            provided.cancel();
            provided = null;
        }
        AppMsg.Style style = new AppMsg.Style(LENGTH_STICKY, R.color.alert);
        int priority = AppMsg.PRIORITY_NORMAL;
        String msg = "App msg";
        int imgRes = R.mipmap.ic_wifi_on;
        switch (status) {
            case WifiStatus
                    .Close:
                priority = AppMsg.PRIORITY_HIGH;
                msg = getString(R.string.wifi_status_close);
                imgRes = R.mipmap.ic_wifi_off;
                style = new AppMsg.Style(LENGTH_STICKY, R.color.alert);
                break;
            case WifiStatus
                    .Open:
                priority = AppMsg.PRIORITY_LOW;
                msg = getString(R.string.wifi_status_open);
                imgRes = R.mipmap.ic_wifi_on;
                style = new AppMsg.Style(LENGTH_SHORT, R.color.info);
                break;
            case WifiStatus
                    .Disconnect:
                priority = AppMsg.PRIORITY_HIGH;
                msg = getString(R.string.wifi_status_disconnect);
                imgRes = R.mipmap.ic_wifi_disconnect;
                style = new AppMsg.Style(LENGTH_STICKY, R.color.alert);
                break;
            case WifiStatus
                    .Connect:
                priority = AppMsg.PRIORITY_NORMAL;
                msg = getString(R.string.wifi_status_connect, ssid);
                imgRes = R.mipmap.ic_wifi_connect;
                style = new AppMsg.Style(LENGTH_LONG, R.color.mediumseagreen);
                break;
        }
        provided = AppMsg.makeText(this, msg, style, R.layout.sticky);
        provided.setPriority(priority);
        View view = provided.getView();
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(imgRes);
        view.findViewById(R.id.remove_btn).setOnClickListener(v -> {
            if (provided != null) {
                provided.cancel();
                provided = null;
            }
        });
        if (isShowToolbar()) {
            provided.setParent(R.id.title_bar);
        }
        provided.setAnimation(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        provided.show();
    }

    private boolean isShowAppMsg() {
        return true;
    }

    @Override
    public void onEventTrigger(@TitleBar.Event int type) {
        switch (type) {
            case TitleBar.Event.buttonLeft:

                break;
            case TitleBar.Event.buttonRight:

                break;
            case TitleBar.Event.imageLeft:
                onLeftTitleBarClick();
                break;
            case TitleBar.Event.imageRight:
                onRightTitleBarClick(mTitleBar.getRightImageView());
                break;
            case TitleBar.Event.imageRight2:

                break;
            case TitleBar.Event.textLeft:
                onLeftTitleBarClick();
                break;
            case TitleBar.Event.textRight:
                onRightTitleBarClick(mTitleBar.getRightTextView());
                break;
            case TitleBar.Event.textTitle:

                break;
            default:
                break;
        }
    }


    /**
     * 返回按钮
     */
    public void onLeftTitleBarClick() {
        finish();
    }

    /**
     * titleBar右侧点击
     */
    public void onRightTitleBarClick(@NonNull View view) {

    }

//    /**
//     * 初始化标题
//     *
//     * @return title
//     */
//    protected abstract String initTitle();

    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void onClick(View v) {

    }
}
