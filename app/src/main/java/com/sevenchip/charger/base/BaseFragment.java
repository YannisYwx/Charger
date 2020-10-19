package com.sevenchip.charger.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author : Yannis.Ywx
 * @createTime : 2017/9/16  18:51
 * @email : 923080261@qq.com
 * @description :
 */
public abstract class BaseFragment extends Fragment {
    private View mRootView;
    protected Context mContext;
    private AppCompatActivity mAppCompatActivity;
    protected boolean isVisible;
    protected boolean isPrepared = false;
    /**
     * 是否初始化过布局
     */
    protected boolean isViewInitiated;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mAppCompatActivity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = isInflaterForRes() ? inflater.inflate(setContentLayout(), null) : initContentView();
            initView(mRootView, savedInstanceState);
            isPrepared = true;
            checkIsVisibleToLoad();
        } else {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) {
                parent.removeView(mRootView);
            }
        }
        return mRootView;
    }

    public boolean isInflaterForRes() {
        return true;
    }

    public View initContentView() {
        return new View(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initEvent();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.isViewInitiated = true;
    }

    /**
     * 初始化视图
     *
     * @param rootView           view
     * @param savedInstanceState savedInstanceState
     */
    public abstract void initView(View rootView, @Nullable Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    public void initData() {

    }

    /**
     * 绑定事件
     */
    public abstract void initEvent();

    protected abstract
    @LayoutRes
    int setContentLayout();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInVisible();
        }
    }

    /**
     * 视图可见时操作
     */
    public void onVisible() {
        lazyLoad();
    }

    /**
     * 视图不可见时操作
     */
    public void onInVisible() {
    }

    @Override
    public void onPause() {
        super.onPause();
        onInVisible();
    }

    /**
     * 只有当绑定了视图和视图可见的时候才去加载数据
     */
    protected void checkIsVisibleToLoad() {
        if (isPrepared && getUserVisibleHint()) {
            lazyLoad();
        }
    }

    /**
     * 懒加载(可见时加载,不可见时隐藏等),在此时填充数据或者更新view
     */
    protected abstract void lazyLoad();

    public void showToast(@StringRes int msgId) {
//        ToastUtils.showShortToast(mContext, msgId);
    }

    public void showToast(String msg) {
//        ToastUtils.showShortToast(mContext, msg);
    }

}
