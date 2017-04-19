package com.example.administrator.gamedemo.widget.pullrecyclerview.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;

import java.util.ArrayList;


/**
 * Created by 大灯泡 on 2017/3/15.
 * <p>
 * recyclerview的headerviewadapter包裹
 */

public class HeaderViewWrapperAdapter extends RecyclerView.Adapter implements WrapperRecyclerAdapter {
    private final RecyclerView.Adapter mWrappedAdapter;
    private RecyclerView recyclerView;

    final ArrayList<FixedViewInfo> mHeaderViewInfos;
    final ArrayList<FixedViewInfo> mFooterViewInfos;

    static final ArrayList<FixedViewInfo> EMPTY_INFO_LIST = new ArrayList<>();


    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            notifyItemRangeChanged(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            notifyItemRangeInserted(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            notifyItemRangeRemoved(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            int headerViewsCountCount = getHeadersCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount, toPosition + headerViewsCountCount + itemCount);
        }
    };


    public HeaderViewWrapperAdapter(RecyclerView recyclerView,
                                    @NonNull RecyclerView.Adapter mWrappedAdapter,
                                    ArrayList<FixedViewInfo> mHeaderViewInfos,
                                    ArrayList<FixedViewInfo> mFooterViewInfos) {
        this.recyclerView = recyclerView;
        this.mWrappedAdapter = mWrappedAdapter;
        this.mWrappedAdapter.registerAdapterDataObserver(mDataObserver);
        if (mHeaderViewInfos == null) {
            this.mHeaderViewInfos = EMPTY_INFO_LIST;
        } else {
            this.mHeaderViewInfos = mHeaderViewInfos;
        }
        if (mFooterViewInfos == null) {
            this.mFooterViewInfos = EMPTY_INFO_LIST;
        } else {
            this.mFooterViewInfos = mFooterViewInfos;
        }
    }


    public int getHeadersCount() {
        return mHeaderViewInfos.size();
    }

    public int getFootersCount() {
        return mFooterViewInfos.size();
    }

    public boolean removeHeader(View headerView) {
        for (int i = 0; i < mHeaderViewInfos.size(); i++) {
            FixedViewInfo info = mHeaderViewInfos.get(i);
            if (info.view == headerView) {
                mHeaderViewInfos.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeFooter(View footerView) {
        for (int i = 0; i < mFooterViewInfos.size(); i++) {
            FixedViewInfo info = mFooterViewInfos.get(i);
            if (info.view == footerView) {
                mFooterViewInfos.remove(i);
                return true;
            }
        }
        return false;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //header
        if (onCreateHeaderViewHolder(viewType)) {
            final int headerPosition = getHeaderPosition(viewType);
            View headerView = mHeaderViewInfos.get(headerPosition).view;
            checkAndSetRecyclerViewLayoutParams(headerView);
            return new HeaderOrFooterViewHolder(headerView);
        } else if (onCreateFooterViewHolder(viewType)) {
            //footer
            final int footerPosition = getFooterPosition(viewType);
            View footerView = mFooterViewInfos.get(footerPosition).view;
            checkAndSetRecyclerViewLayoutParams(footerView);
            return new HeaderOrFooterViewHolder(footerView);
        }
        return mWrappedAdapter.onCreateViewHolder(parent, viewType);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int numHeaders = getHeadersCount();
        int adapterCount = mWrappedAdapter.getItemCount();
        if (position < numHeaders) {
            //header
            return;
        } else if (position > (numHeaders + adapterCount - 1)) {
            //footer
            return;
        } else {
            int adjustPosition = position - numHeaders;
            if (adjustPosition < adapterCount) {
                mWrappedAdapter.onBindViewHolder(holder, adjustPosition);
            }
        }

    }

    private void checkAndSetRecyclerViewLayoutParams(View child) {
        if (child == null) return;
        LayoutParams p = child.getLayoutParams();
        LayoutParams params = null;
        if (p == null) {
            params = new LayoutParams(new MarginLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)));
        } else {
            if (!(p instanceof RecyclerView.LayoutParams)) {
                params = recyclerView.getLayoutManager().generateLayoutParams(p);
            }
        }
        child.setLayoutParams(params);

    }

    @Override
    public int getItemCount() {
        if (mWrappedAdapter != null) {
            return getHeadersCount() + getFootersCount() + mWrappedAdapter.getItemCount();
        } else {
            return getHeadersCount() + getFootersCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int numHeaders = getHeadersCount();
        if (mWrappedAdapter == null) return -1;
        //header之后的view，返回adapter的itemType
        int adjustPos = position - numHeaders;
        int adapterItemCount = mWrappedAdapter.getItemCount();
        if (position >= numHeaders) {
            if (adjustPos < adapterItemCount) {
                //如果是adapter返回的范围内，则取adapter的itemviewtype
                return mWrappedAdapter.getItemViewType(adjustPos);
            }
        } else if (position < numHeaders) {
            return mHeaderViewInfos.get(position).itemViewType;
        }
        return mFooterViewInfos.get(position - adapterItemCount - numHeaders).itemViewType;
    }

    @Override
    public RecyclerView.Adapter getWrappedAdapter() {
        return this.mWrappedAdapter;
    }

    private boolean onCreateHeaderViewHolder(int viewType) {
        return mHeaderViewInfos.size() > 0 && viewType <= FixedViewInfo.ITEM_VIEW_TYPE_HEADER_START && viewType > FixedViewInfo.ITEM_VIEW_TYPE_FOOTER_START;
    }

    private boolean onCreateFooterViewHolder(int viewType) {
        return mFooterViewInfos.size() > 0 && viewType <= FixedViewInfo.ITEM_VIEW_TYPE_FOOTER_START;
    }

    private int getHeaderPosition(int viewType) {
        return Math.abs(viewType) - Math.abs(FixedViewInfo.ITEM_VIEW_TYPE_HEADER_START);
    }

    private int getFooterPosition(int viewType) {
        return Math.abs(viewType) - Math.abs(FixedViewInfo.ITEM_VIEW_TYPE_FOOTER_START);
    }

    public int findHeaderPosition(View headerView) {
        if (headerView == null) return -1;
        for (int i = 0; i < mHeaderViewInfos.size(); i++) {
            FixedViewInfo info = mHeaderViewInfos.get(i);
            if (info.view == headerView) return i;
        }
        return -1;
    }

    public int findFooterPosition(View footerView) {
        if (footerView == null) return -1;
        for (int i = 0; i < mHeaderViewInfos.size(); i++) {
            FixedViewInfo info = mHeaderViewInfos.get(i);
            if (info.view == footerView) {
                return getHeadersCount() + mWrappedAdapter.getItemCount() + i;
            }
        }
        return -1;
    }


    public static final class HeaderOrFooterViewHolder extends RecyclerView.ViewHolder {

        public HeaderOrFooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
