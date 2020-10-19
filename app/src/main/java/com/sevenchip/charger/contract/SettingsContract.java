package com.sevenchip.charger.contract;

import com.sevenchip.charger.base.inter.IMode;
import com.sevenchip.charger.base.inter.IPresenter;
import com.sevenchip.charger.base.inter.IView;
import com.sevenchip.charger.data.bean.Channel;
import com.sevenchip.charger.data.bean.DownstreamData;

/**
 * Author : Alvin
 * CreateTime : 2020/7/19 16:20
 * Email : 923080261@qq.com
 * Description :
 */
public interface SettingsContract {

    interface View extends IView {

        void applySettingsResult(boolean isOk);

        void sendSuccess();
    }

    interface Presenter extends IPresenter {
        void applySettings(DownstreamData data);
    }

    interface Mode extends IMode {
        void setChannelToDevice(Channel channel);
    }
}
