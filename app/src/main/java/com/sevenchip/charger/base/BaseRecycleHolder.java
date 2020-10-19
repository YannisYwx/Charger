package com.sevenchip.charger.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Author : Alvin
 * CreateTime : 2020/7/19 23:12
 * Description :
 */
public abstract class BaseRecycleHolder<T> extends RecyclerView.ViewHolder implements BaseRecycleHolderImpl<T>{
    private T data;
    protected Context mContext;
    String EXTRA = "_extra";

    public BaseRecycleHolder(@NonNull View itemView) {
        super(itemView);
        mContext = itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    /**
     * 设置数据
     *
     * @param data 绑定的数据
     */
    @Override
    public void setDataAndRefreshHolderView(T data) {
        this.data = data;
        refreshViewHolder(data);
    }

    /**
     * 刷新控件
     *
     * @param data 绑定的数据
     */
    protected abstract void refreshViewHolder(T data);

    /**
     * 根据需求刷新控件
     *
     * @param key key
     */
    protected void refreshViewHolderByKey(Object key) {
    }

    /**
     * 获得数据
     *
     * @return 当前绑定的数据
     */
    public T getData() {
        return data;
    }

    protected void startActivity(Class<? extends AppCompatActivity> cls, @Nullable Bundle bundle) {
        if (cls == null) {
            throw new NullPointerException("The Class can not be null");
        }
        Intent intent = new Intent(itemView.getContext(), cls);
        if (bundle != null) {
            intent.putExtra(EXTRA, bundle);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        itemView.getContext().startActivity(intent);
    }

    public int getColor(@ColorRes int colorRes) {
        return itemView.getContext().getResources().getColor(colorRes);
    }

    public String getString(@StringRes int stringRes) {
        return itemView.getContext().getResources().getString(stringRes);
    }

    protected void startActivity(Class<? extends AppCompatActivity> clazz) {
        startActivity(clazz, null);
    }
}

