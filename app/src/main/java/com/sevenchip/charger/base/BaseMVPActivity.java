package com.sevenchip.charger.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sevenchip.charger.base.inter.IPresenter;

/**
 * Author : Alvin
 * CreateTime : 2020/7/18 22:31
 * Description :
 */
public abstract class BaseMVPActivity<P extends IPresenter> extends BaseActivity {
    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindMVP();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    private void bindMVP() {
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        mPresenter.onAttach();
    }

    public abstract P createPresenter();
}
