package com.sevenchip.charger.base.inter;

/**
 * Author : Alvin
 * CreateTime : 2020/7/18 22:17
 * Email : 923080261@qq.com
 * Description :
 */
public interface IPresenter {


    /**
     * 绑定view
     */
    void onAttach();

    /**
     * 解绑view
     */
    void onDetach();
}
