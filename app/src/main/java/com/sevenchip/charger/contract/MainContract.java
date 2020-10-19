package com.sevenchip.charger.contract;

import com.sevenchip.charger.base.inter.IMode;
import com.sevenchip.charger.base.inter.IPresenter;
import com.sevenchip.charger.base.inter.IView;
import com.sevenchip.charger.data.bean.Charger;
import com.sevenchip.charger.data.bean.DownstreamData;

import java.util.List;

/**
 * Author : Alvin
 * CreateTime : 2020/7/19 16:20
 * Email : 923080261@qq.com
 * Description :
 */
public interface MainContract {

    interface View extends IView {

        void refresh();
    }

    interface Presenter extends IPresenter {
        void startListenerPort();

        void stopListenerPort();

        void applySettings(DownstreamData data);
    }

    interface Mode extends IMode {
        List<Charger> getChargers();
    }
}
