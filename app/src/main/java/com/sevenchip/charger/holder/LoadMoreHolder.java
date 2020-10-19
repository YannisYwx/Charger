package com.sevenchip.charger.holder;

import android.support.annotation.IntDef;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sevenchip.charger.R;
import com.sevenchip.charger.base.BaseRecycleHolder;
import com.sevenchip.charger.widget.SlackLoadingView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.sevenchip.charger.holder.LoadMoreHolder.LoadMoreState.error;
import static com.sevenchip.charger.holder.LoadMoreHolder.LoadMoreState.loading;
import static com.sevenchip.charger.holder.LoadMoreHolder.LoadMoreState.noData;
import static com.sevenchip.charger.holder.LoadMoreHolder.LoadMoreState.none;
import static com.sevenchip.charger.holder.LoadMoreHolder.LoadMoreState.notShow;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/7/21 0:48
 * Email : 923080261@qq.com
 * Description :
 */
public class LoadMoreHolder extends BaseRecycleHolder<Integer> {

    @IntDef({loading, none, error, notShow, noData})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoadMoreState {
        /**
         * 正在加载
         */
        int loading = 1;
        /**
         * 没有数据
         */
        int none = 2;
        /**
         * 加载出错
         */
        int error = 3;
        /**
         * 不显示
         */
        int notShow = 4;
        /**
         * 没有数据
         */
        int noData = 5;
    }


    public LoadMoreHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void refreshViewHolder(@LoadMoreState Integer state) {
        LinearLayout llLoading = itemView.findViewById(R.id.ll_loadMore);
        SlackLoadingView loadingImage = itemView.findViewById(R.id.slackLoadingView);
        TextView tvNoMore = itemView.findViewById(R.id.tv_noMore);
        TextView tvRetry = itemView.findViewById(R.id.tv_retry);


        llLoading.setVisibility(View.INVISIBLE);
        tvNoMore.setVisibility(View.INVISIBLE);
        tvRetry.setVisibility(View.INVISIBLE);

        switch (state) {
            case loading:
                llLoading.setVisibility(View.VISIBLE);
                loadingImage.start();
                break;
            case none:
                tvNoMore.setVisibility(View.VISIBLE);
                break;
            case error:
                tvRetry.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
