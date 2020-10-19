package com.sevenchip.charger.base;

import android.support.annotation.NonNull;

import com.sevenchip.charger.base.inter.IFragmentView;
import com.sevenchip.charger.base.inter.IPresenter;

/**
 * @author : Yannis.Ywx
 * @createTime : 2017/9/23  15:41
 * @email : 923080261@qq.com
 * @description :
 */
public abstract class BaseMVPFragment<P extends IPresenter> extends BaseFragment implements IFragmentView<P> {

    protected P presenter;

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.onDetach();
        }
    }


    @Override
    public void setPresenter(@NonNull P presenter) {
        this.presenter = presenter;
        presenter.onAttach();

    }

}
