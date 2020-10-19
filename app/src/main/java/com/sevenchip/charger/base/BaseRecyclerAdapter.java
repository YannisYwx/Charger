package com.sevenchip.charger.base;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevenchip.charger.R;
import com.sevenchip.charger.holder.LoadMoreHolder;
import com.sevenchip.charger.utils.ThreadPoolManager;
import com.sevenchip.charger.utils.UIUtils;

import java.util.List;

/**
 * Author : Yannis.Ywx
 * CreateTime : 2020/7/21 0:29
 * Email : 923080261@qq.com
 * Description :
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter {

    private int PAGE_SIZE = 10;

    private static final int VIEW_LOAD_MORE = 1_001;
    private static final int VIEW_COMMON = 1_002;
    protected List<T> mDataList;
    private LoadMoreTask mLoadMoreTask;
    private int mCurLoadMoreState;
    private LoadMoreHolder mLoadMoreHolder;


    /**
     * 暴露一个方法,设置数据集合
     *
     * @param dataList 数据集合
     */
    public void setData(List<T> dataList) {
        mDataList = dataList;
    }

    public List<T> getDataList() {
        return mDataList;
    }

    public BaseRecyclerAdapter(List<T> dataList) {
        mDataList = dataList;
    }

    //获取条目类型
    @Override
    public int getItemViewType(int position) {
        if (isLoadMoreHolderPosition(position)) {
            //公共条目
            return VIEW_LOAD_MORE;
        } else if (isCommonHolderPosition(position)) {
            //加载更多
            return VIEW_COMMON;
        } else {
            //其他视图
            return getOtherItemType(position);
        }
    }

    /**
     * 默认的获取CommonHolder（通用的item）位置判断 复写该方法可以根据具体数据和需求返回所需
     *
     * @param position pos
     * @return 默认位置为除其他item以外的其他位置  （load more排除）
     */
    protected boolean isCommonHolderPosition(int position) {
        return position > initOtherItemCount() - 1 && position < mDataList.size() + initOtherItemCount();
    }

    /**
     * 默认加载很多Holder位置判断
     * 位于最后一个位置
     * @param position pos
     * @return
     */
    protected boolean isLoadMoreHolderPosition(int position) {
        return position == mDataList.size() + initOtherItemCount();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_LOAD_MORE) {
            View view = loadItemLayout(R.layout.item_loadmore, parent);
            //给加载更多的holder设置点击事件 统一处理
            viewHolder = new LoadMoreHolder(view);
            viewHolder.itemView.setVisibility(isShowLoadMoreView() ? View.VISIBLE : View.GONE);
        } else if (viewType == VIEW_COMMON) {
            //普通视图的holder 由子类决定 并且由子类设置点击事件
            viewHolder = getCommonHolder(parent, viewType);
        } else {
            viewHolder = getSpecialHolder(parent, viewType);
        }
        return viewHolder;
    }

    private void onItemClick(int position) {
        mOnItemClickListener.onItemClick(position);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isLoadMoreHolderPosition(position)) {
            //加载更多
            mLoadMoreHolder = (LoadMoreHolder) holder;
            if (hasLoadMore()) {

                //判断是否有加载更多 并且状态为正在加载
                mLoadMoreHolder.setDataAndRefreshHolderView(LoadMoreHolder.LoadMoreState.loading);
                if (isLoadFromSelf() && position != 0) {
                    //触发加载更多
                    triggerLoadMoreData();
                } else if (position != 0) {
                    //通知外面去加载数据
                    notifyToLoadMoreData();
                }
            } else {
                //没有加载更多的条目
                mLoadMoreHolder.setDataAndRefreshHolderView(LoadMoreHolder.LoadMoreState.notShow);
            }
        } else if (isCommonHolderPosition(position)) {
            //普通视图
            BaseRecycleHolderImpl<T> baseRecycleHolder = (BaseRecycleHolderImpl<T>) holder;
            baseRecycleHolder.setDataAndRefreshHolderView(mDataList.get(position - initOtherItemCount()));
        } else {
            //其他视图
            BaseRecycleHolderImpl otherHolder = (BaseRecycleHolderImpl) holder;
            //其他holder 目前不知道所要的数据类型 默认为空
            otherHolder.setDataAndRefreshHolderView(getOtherData(position));
        }
        if (mOnItemClickListener != null) {
            int pos = holder.getLayoutPosition();
            holder.itemView.setOnClickListener(v -> onItemClick(pos));
        }
    }

    /**
     * 复写该方法 注入其他控件的数据
     *
     * @param position 位置
     * @return 默认为空
     */
    protected Object getOtherData(int position) {
        return null;
    }


    /**
     * 是否用自身方法加载
     *
     * @return 默认通过自身的线程池任务执行加载任务
     */
    public boolean isLoadFromSelf() {
        return true;
    }

    /**
     * 是否显示加载更多的view
     *
     * @return 默认显示加载更多的view
     */
    public boolean isShowLoadMoreView() {
        return true;
    }


    /**
     * 通知外部加载数据
     */
    private void notifyToLoadMoreData() {
        if (mOnLoadMoreDataListener != null) {
            mOnLoadMoreDataListener.onLoadMoreData();
        }
    }

    /**
     * 触发加载更多
     */
    private void triggerLoadMoreData() {
        if (mLoadMoreTask == null) {
            //重置状态,修改UI
            mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.loading);
            mLoadMoreHolder.setDataAndRefreshHolderView(LoadMoreHolder.LoadMoreState.loading);

            mLoadMoreTask = new LoadMoreTask();
            ThreadPoolManager.getInstance().execute(mLoadMoreTask);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 1 + initOtherItemCount() : 1 + initOtherItemCount() + mDataList.size();
    }

    /**
     * 由子类实现,确定其他条目的类型
     *
     * @param position
     * @return
     */
    protected abstract int getOtherItemType(int position);

    /**
     * 有子类实现 获得通用类型
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract RecyclerView.ViewHolder getCommonHolder(ViewGroup parent, int viewType);

    /**
     * 子类 确定视图
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract RecyclerView.ViewHolder getSpecialHolder(ViewGroup parent, int viewType);

    /**
     * 由子类选择实现
     *
     * @return
     * @des 初始化其他条目的个数
     */
    protected abstract int initOtherItemCount();

    /**
     * 是否有加载更多,默认没有,子类可以选择性实现
     *
     * @return
     */
    protected abstract boolean hasLoadMore();


    /**
     * 加载更多的方法,子类可以选择实现
     *
     * @return
     * @throws Exception exception
     */
    protected abstract List<T> onLoadMoreData() throws Exception;

    /**
     * 只刷新List数据 防止刷新了加载更多 整个item
     */
    protected void refreshCommonData() {
        notifyItemRangeChanged(initOtherItemCount(), initOtherItemCount() + mDataList.size());
    }

    /**
     * 只刷新数据 防止刷新了加载更多 制定控件
     */
    protected void refreshCommonData(Object refreshKey) {
        notifyItemRangeChanged(initOtherItemCount(), initOtherItemCount() + mDataList.size(), refreshKey);
    }

    /**
     * 处理数据 排序 设置一些自己需要的参数
     *
     * @param dataList
     */
    protected void disposeData(List<T> dataList) {
    }

    public void noMoreData() {
        mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.none);
        if (mLoadMoreHolder != null) {
            mLoadMoreHolder.setDataAndRefreshHolderView(mCurLoadMoreState);
        }
    }

    /**
     * 加载数据
     */
    public void loadMoreData(List<T> loadMoreList) {
        if (loadMoreList != null) {
            int size = loadMoreList.size();
            if (size < PAGE_SIZE) {
                //没有加载更多
                mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.none);
            } else {
                //有加载更多
                mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.loading);
            }
        } else {
            //没有加载更多
            mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.none);
        }

        //刷新UI
        final List<T> finalLoadMoreList = loadMoreList;
        UIUtils.getMainHandler().post(() -> {
            if (finalLoadMoreList != null && finalLoadMoreList.size() > 0) {
                //更新数据集
                mDataList.addAll(finalLoadMoreList);
                //处理数据 比如item需要分组显示等 有这类需求可以实现该方法
                disposeData(mDataList);
                //RecyclerView局部刷新UI
                notifyItemRangeChanged(initOtherItemCount() + mDataList.size() - finalLoadMoreList.size(), initOtherItemCount() + mDataList.size());
            }
            //更新加载更多的UI
            if (mLoadMoreHolder != null) {
                mLoadMoreHolder.setDataAndRefreshHolderView(mCurLoadMoreState);
            }
        });
    }


    /**
     * 加载更多的任务
     */
    private class LoadMoreTask implements Runnable {

        @Override
        public void run() {
            List<T> loadMoreList = null;
            try {
                loadMoreList = onLoadMoreData();

                if (loadMoreList != null) {
                    int size = loadMoreList.size();
                    if (size < PAGE_SIZE) {
                        //没有加载更多
                        mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.none);
                    } else {
                        //有加载更多
                        mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.loading);
                    }
                } else {
                    //没有加载更多
                    mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.none);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //网络异常,显示加载失败的视图
                mCurLoadMoreState = (LoadMoreHolder.LoadMoreState.error);
            }

            //刷新UI
            final List<T> finalLoadMoreList = loadMoreList;
            UIUtils.getMainHandler().post(() -> {
                if (finalLoadMoreList != null && finalLoadMoreList.size() > 0) {
                    //更新数据集
                    mDataList.addAll(finalLoadMoreList);
                    //处理数据 比如item需要分组显示等 有这类需求可以实现该方法
                    disposeData(mDataList);
                    //利用RecyclerViwe 局部刷新UI
                    notifyItemRangeChanged(initOtherItemCount() + mDataList.size() - finalLoadMoreList.size(), initOtherItemCount() + mDataList.size());
                }
                //更新加载更多的UI
                mLoadMoreHolder.setDataAndRefreshHolderView(mCurLoadMoreState);
            });
            //线程结束 置空任务
            mLoadMoreTask = null;
        }
    }

    /**
     * 根据视图资源文件加载视图
     *
     * @param layoutRes
     * @param parent
     * @return
     */
    protected View loadItemLayout(@LayoutRes int layoutRes, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    public interface OnItemClickListener {
        /**
         * 点击item
         *
         * @param position 当前的位置
         */
        void onItemClick(int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnLoadMoreDataListener {
        /**
         * 加载更多
         */
        void onLoadMoreData();
    }

    private OnLoadMoreDataListener mOnLoadMoreDataListener;

    public void setOnLoadMoreDataListener(OnLoadMoreDataListener listener) {
        this.mOnLoadMoreDataListener = listener;
    }
}
