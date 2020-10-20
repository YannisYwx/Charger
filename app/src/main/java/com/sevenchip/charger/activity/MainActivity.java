package com.sevenchip.charger.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.sevenchip.charger.R;
import com.sevenchip.charger.adapter.ChargerDeviceAdapter;
import com.sevenchip.charger.base.BaseMVPActivity;
import com.sevenchip.charger.contract.MainContract;
import com.sevenchip.charger.data.DataManager;
import com.sevenchip.charger.data.TestDataMode;
import com.sevenchip.charger.data.bean.DownstreamData;
import com.sevenchip.charger.data.bean.UpstreamData;
import com.sevenchip.charger.holder.ChargerDeviceHolder;
import com.sevenchip.charger.presenter.MainPresenter;
import com.sevenchip.charger.utils.RecyclerViewSpacesItemDecoration;
import com.sevenchip.charger.widget.TitleBar;

import java.util.HashMap;

import butterknife.BindView;

public class MainActivity extends BaseMVPActivity<MainContract.Presenter> implements MainContract.View, ChargerDeviceHolder.OnBatteryStatusChangeListener {

    @BindView(R.id.rv_device)
    RecyclerView mRecyclerView;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private ChargerDeviceAdapter mAdapter;
    protected LayoutAnimationController controller;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    //然后通过一个函数来申请
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void init() {
        verifyStoragePermissions(this);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 10);//右间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 10);//右间距

        mAdapter = new ChargerDeviceAdapter(DataManager.getInstance().getCurrentBatteryCharger(), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);
        mRecyclerView.setLayoutAnimation(controller);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mTitleBar = (TitleBar) findViewById(R.id.title_bar);
        setSwipeBackEnable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.startListenerPort();
    }

    @Override
    public void initData() {
        super.initData();
    }

    private int status = -1;

    @Override
    protected void initEvent() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_add_device:
                    status = 1;
                    break;
                case R.id.nav_device_list:
                    status = 2;
                    break;
                case R.id.nav_settings_wifi:
                    status = 3;
                    break;
                case R.id.nav_test:
                    status = 4;
                    break;
                default:
                    break;
            }
            drawerLayout.closeDrawer(Gravity.START);
            return false;
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                new Handler().postDelayed(() -> {
                    switch (status) {
                        case 1:
                            switchTo(AddDeviceActivity.class);
                            break;
                        case 2:
                            switchTo(DeviceIdListActivity.class);
                            break;
                        case 3:
                            switchTo(WifiSettingActivity.class);
                            break;
                        case 4:
                            switchTo(TestActivity.class);
                            break;
                        default:
                            break;
                    }
                    status = -1;
                }, 200);
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public MainContract.Presenter createPresenter() {
        return new MainPresenter(this);
    }

    @Override
    public void refresh() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLeftTitleBarClick() {
        drawerLayout.openDrawer(Gravity.START);
    }

    @Override
    protected void onDestroy() {
        mPresenter.stopListenerPort();
        super.onDestroy();
    }

    @Override
    public void showMsg(String msg) {
        showToast(msg);
    }

    @Override
    public void OnBatteryStatusChanged(boolean status, UpstreamData upstreamData) {
        DownstreamData downstreamData = upstreamData.getDownstreamData();
        downstreamData.setStatus(status ? 0 : 1);
        downstreamData.setCurrent(0.0f);
        mPresenter.applySettings(downstreamData);
    }

    @Override
    public void scrollToFinishActivity() {
    }
}
