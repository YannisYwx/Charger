package com.sevenchip.charger.base;

/**
 * Author : Alvin
 * CreateTime : 2020/7/19 23:17
 * Description :
 */
public interface BaseRecycleHolderImpl<T> {

    /**
     * 刷新数据接口
     * @param data 数据
     */
    void setDataAndRefreshHolderView(T data);
}
