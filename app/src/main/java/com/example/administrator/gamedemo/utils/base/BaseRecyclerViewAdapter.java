package com.example.administrator.gamedemo.utils.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.gamedemo.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lixu on 2017/1/1.
 * <p>
 * 抽象的adapter
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder<T>> {
    private static final String TAG = "BaseRecyclerViewAdapter";

    protected Context context;
    protected List<T> datas;
    protected LayoutInflater mInflater;

    private OnRecyclerViewItemClickListener<T> onRecyclerViewItemClickListener;
    private OnRecyclerViewLongItemClickListener<T> onRecyclerViewLongItemClickListener;

    public BaseRecyclerViewAdapter(@NonNull Context context, @NonNull List<T> datas) {
        this.context = context;
        this.datas = new ArrayList<>();
        this.datas.addAll(datas);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(position, datas.get(position));
    }

    @Override
    public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BaseRecyclerViewHolder holder = null;
        if (getLayoutResId(viewType) != 0) {
            View rootView = mInflater.inflate(getLayoutResId(viewType), parent, false);
            holder = getViewHolder(parent, rootView, viewType);
        } else {
            holder = getViewHolder(parent, null, viewType);
        }
        setUpItemEvent(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        T data = datas.get(position);
        holder.itemView.setTag(R.id.recycler_view_tag, data);
        holder.onBindData(data, position);
        onBindData(holder, data, position);
    }

    private void setUpItemEvent(final BaseRecyclerViewHolder holder) {
        if (onRecyclerViewItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //这个获取位置的方法，防止添加删除导致位置不变
                    int layoutPosition = holder.getAdapterPosition();
                    onRecyclerViewItemClickListener.onItemClick(holder.itemView,
                                                                layoutPosition,
                                                                datas.get(layoutPosition));
                }
            });
        }
        if (onRecyclerViewLongItemClickListener != null) {
            //longclick
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int layoutPosition = holder.getAdapterPosition();
                    onRecyclerViewLongItemClickListener.onItemLongClick(holder.itemView,
                                                                        layoutPosition,
                                                                        datas.get(layoutPosition));
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void updateData(List<T> datas) {
        Logger.d("刷新数据="+datas.size());
        this.datas.clear();
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addMore(List<T> datas) {
        Logger.d("更多数据="+datas.size());
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    public void addData(int pos, @NonNull T data) {
        if (datas != null) {
            datas.add(pos, data);
            notifyItemInserted(pos);
        }
    }

    public void addData(@NonNull T data) {
        if (datas != null) {
            datas.add(data);
            notifyItemInserted(datas.size() - 1);
        }
    }

    public void deleteData(int pos) {
        if (datas != null && datas.size() > pos) {
            datas.remove(pos);
            notifyItemRemoved(pos);
        }
    }

    public T findData(int pos) {
        if (pos < 0 || pos > datas.size()) {
            Log.e(TAG, "我hold不住");
            return null;
        }
        return datas.get(pos);
    }

    protected abstract int getViewType(int position, @NonNull T data);

    protected abstract int getLayoutResId(int viewType);

    protected abstract BaseRecyclerViewHolder getViewHolder(ViewGroup parent, View inflatedView, int viewType);

    protected void onBindData(BaseRecyclerViewHolder<T> holder, T data, int position) {

    }

    public OnRecyclerViewItemClickListener<T> getOnRecyclerViewItemClickListener() {
        return onRecyclerViewItemClickListener;
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener<T> onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }

    public OnRecyclerViewLongItemClickListener<T> getOnRecyclerViewLongItemClickListener() {
        return onRecyclerViewLongItemClickListener;
    }

    public void setOnRecyclerViewLongItemClickListener(OnRecyclerViewLongItemClickListener<T> onRecyclerViewLongItemClickListener) {
        this.onRecyclerViewLongItemClickListener = onRecyclerViewLongItemClickListener;
    }
}
