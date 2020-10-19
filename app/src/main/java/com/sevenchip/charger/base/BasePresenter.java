package com.sevenchip.charger.base;

import android.support.v7.app.AppCompatActivity;

import com.sevenchip.charger.base.inter.IMode;
import com.sevenchip.charger.base.inter.IPresenter;
import com.sevenchip.charger.base.inter.IView;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/7/26 21:35
 * Email : 923080261@qq.com
 * Description :
 */
public abstract class BasePresenter<V extends IView, M extends IMode> implements IPresenter {

    protected V mView;

    protected M mMode;

    public BasePresenter(V view) {
        if (view == null) {
            throw new NullPointerException("The view must not be null");
        }
        this.mView = view;
        this.mMode = createMode();
    }

    public abstract M createMode();

    @Override
    public void onAttach() {

    }

    @Override
    public void onDetach() {
        if (mView != null) {
            mView = null;
        }
    }

    public AppCompatActivity getActivity() {
        if (mView instanceof AppCompatActivity) {
            return (AppCompatActivity) mView;
        }
        return null;
    }
}
