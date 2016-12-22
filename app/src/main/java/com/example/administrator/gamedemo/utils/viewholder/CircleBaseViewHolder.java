package com.example.administrator.gamedemo.utils.viewholder;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.SimpleObjectPool;
import com.example.administrator.gamedemo.utils.TimeUtil;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.UIHelper;
import com.example.administrator.gamedemo.utils.base.BaseRecyclerViewHolder;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenter;
import com.example.administrator.gamedemo.widget.ClickShowMoreLayout;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.popup.CommentPopup;
import com.example.administrator.gamedemo.widget.popup.DeleteCommentPopup;
import com.example.administrator.gamedemo.widget.praisewidget.PraiseWidget;
import com.orhanobut.logger.Logger;

import java.util.List;


/**
 * Created by 大灯泡 on 2016/11/1.
 * <p>
 * 朋友圈基本item
 */
public abstract class CircleBaseViewHolder extends BaseRecyclerViewHolder<Share> implements BaseMomentVH<Share>, ViewGroup.OnHierarchyChangeListener {


    //头部
    protected ImageView avatar;
    protected TextView nick;
    protected ClickShowMoreLayout userText;

    //底部
    protected TextView createTime;
    protected ImageView commentImage;
    protected FrameLayout menuButton;
    protected LinearLayout commentAndPraiseLayout;
    protected PraiseWidget praiseWidget;
    protected View line;
    protected LinearLayout commentLayout;

    //内容区
    protected RelativeLayout contentLayout;

    //评论区的view对象池
    private static final SimpleObjectPool<CommentWidget> COMMENT_TEXT_POOL = new SimpleObjectPool<CommentWidget>(35);

    private CommentPopup commentPopup;
    private DeleteCommentPopup deleteCommentPopup;

    private MomentPresenter momentPresenter;
    private int itemPosition;
    private Share momentsInfo;


    public CircleBaseViewHolder(Context context, ViewGroup viewGroup, int layoutResId) {
        super(context, viewGroup, layoutResId);
        onFindView(itemView);

        //header
        avatar = (ImageView) findView(avatar, R.id.avatar);
        nick = (TextView) findView(nick, R.id.nick);
        userText = (ClickShowMoreLayout) findView(userText, R.id.item_text_field);

        //bottom
        createTime = (TextView) findView(createTime, R.id.create_time);
        commentImage = (ImageView) findView(commentImage, R.id.menu_img);
        menuButton = (FrameLayout) findView(menuButton, R.id.menu_button);
        commentAndPraiseLayout = (LinearLayout) findView(commentAndPraiseLayout, R.id.comment_praise_layout);
        praiseWidget = (PraiseWidget) findView(praiseWidget, R.id.praise);
        line = findView(line, R.id.divider);
        commentLayout = (LinearLayout) findView(commentLayout, R.id.comment_layout);
        //content
        contentLayout = (RelativeLayout) findView(contentLayout, R.id.content);

        if (commentPopup == null) {
            commentPopup = new CommentPopup((Activity) getContext());
            commentPopup.setOnCommentPopupClickListener(onCommentPopupClickListener);
        }

        if (deleteCommentPopup == null) {
            deleteCommentPopup = new DeleteCommentPopup((Activity) getContext());
            deleteCommentPopup.setOnDeleteCommentClickListener(onDeleteCommentClickListener);
        }
    }

    public void setPresenter(MomentPresenter momentPresenter) {
        this.momentPresenter = momentPresenter;
    }

    public MomentPresenter getPresenter() {
        return momentPresenter;
    }

    @Override
    public void onBindData(Share data, int position) {
        if (data == null) {
            Logger.e("数据是空的！！！！");
            findView(userText, R.id.item_text_field);
            userText.setText("");
            return;
        }
        this.momentsInfo=data;
        this.itemPosition = position;
        //通用数据绑定
        onBindMutualDataToViews(data);
        //点击事件
        menuButton.setOnClickListener(onMenuButtonClickListener);
        menuButton.setTag(R.id.momentinfo_data_tag_id, data);
        //传递到子类
        onBindDataToView(data, position, getViewType());
    }

    private void onBindMutualDataToViews(Share data) {
        //header
        ImageLoadMnanger.INSTANCE.loadImage(avatar, data.getAuthor().getAvatar());
        nick.setText(data.getAuthor().getNick_name());
        userText.setText(data.getText());

        //bottom
        createTime.setText(TimeUtil.getTimeStringFromBmob(data.getCreatedAt()));
        boolean needPraiseData = addLikes(data.getLikesList());
        boolean needCommentData = addComment(data.getCommentList());
        praiseWidget.setVisibility(needPraiseData ? View.VISIBLE : View.GONE);
        commentLayout.setVisibility(needCommentData ? View.VISIBLE : View.GONE);
        line.setVisibility(needPraiseData && needCommentData ? View.VISIBLE : View.GONE);
        commentAndPraiseLayout.setVisibility(needCommentData || needPraiseData ? View.VISIBLE : View.GONE);

    }


    /**
     * 添加点赞
     *
     * @param likesList
     * @return ture=显示点赞，false=不显示点赞
     */
    private boolean addLikes(List<Students> likesList) {
        if (ToolUtil.isListEmpty(likesList)) {
            return false;
        }
        praiseWidget.setDatas(likesList);
        return true;
    }


    private int commentLeftAndPaddintRight = UIHelper.dipToPx(8f);
    private int commentTopAndPaddintBottom = UIHelper.dipToPx(3f);

    /**
     * 添加评论
     *
     * @param commentList
     * @return ture=显示评论，false=不显示评论
     */
    private boolean addComment(List<CommentInfo> commentList) {
        if (ToolUtil.isListEmpty(commentList)) {
            return false;
        }
        final int childCount = commentLayout.getChildCount();
        if (childCount < commentList.size()) {
            //当前的view少于list的长度，则补充相差的view
            int subCount = commentList.size() - childCount;
            for (int i = 0; i < subCount; i++) {
                CommentWidget commentWidget = COMMENT_TEXT_POOL.get();
                if (commentWidget == null) {
                    commentWidget = new CommentWidget(getContext());
                    commentWidget.setPadding(commentLeftAndPaddintRight, commentTopAndPaddintBottom, commentLeftAndPaddintRight, commentTopAndPaddintBottom);
                    commentWidget.setLineSpacing(4, 1);
                }
                commentWidget.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_comment_widget));
                commentWidget.setOnClickListener(onCommentWidgetClickListener);
                commentWidget.setOnLongClickListener(onCommentLongClickListener);
                commentLayout.addView(commentWidget);
            }
        } else if (childCount > commentList.size()) {
            //当前的view的数目比list的长度大，则减去对应的view
            commentLayout.removeViews(commentList.size(), childCount - commentList.size());
        }
        //绑定数据
        for (int n = 0; n < commentList.size(); n++) {
            CommentWidget commentWidget = (CommentWidget) commentLayout.getChildAt(n);
            if (commentWidget != null) commentWidget.setCommentText(commentList.get(n));
        }
        return true;
    }


    @Override
    public void onChildViewAdded(View parent, View child) {

    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        if (child instanceof CommentWidget) COMMENT_TEXT_POOL.put((CommentWidget) child);
    }

    public void clearCommentPool() {
        COMMENT_TEXT_POOL.clearPool();
    }

    /**
     * ==================  click listener block
     */

    private View.OnClickListener onCommentWidgetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!(v instanceof CommentWidget)) return;
            CommentInfo commentInfo = ((CommentWidget) v).getData();
            if (commentInfo == null) return;
            if (commentInfo.canDelete()) {
                deleteCommentPopup.showPopupWindow(commentInfo);
            } else {
                momentPresenter.showCommentBox(itemPosition, momentsInfo, (CommentWidget) v);
            }
        }
    };

    private DeleteCommentPopup.OnDeleteCommentClickListener onDeleteCommentClickListener=new DeleteCommentPopup.OnDeleteCommentClickListener() {
        @Override
        public void onDelClick(CommentInfo commentInfo) {
            momentPresenter.deleteComment(itemPosition,commentInfo.getCommentid(),momentsInfo.getCommentList());
        }
    };

    private View.OnLongClickListener onCommentLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    };

    private View.OnClickListener onMenuButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Share info = (Share) v.getTag(R.id.momentinfo_data_tag_id);
            if (info != null) {
                commentPopup.updateMomentInfo(info);
                commentPopup.showPopupWindow(commentImage);
            }
        }
    };


    private CommentPopup.OnCommentPopupClickListener onCommentPopupClickListener = new CommentPopup.OnCommentPopupClickListener() {
        @Override
        public void onLikeClick(View v, @NonNull Share info, boolean hasLiked) {
            if (hasLiked) {
                momentPresenter.unLike(itemPosition, info.getMomentid(), info.getLikesList());
            } else {
                momentPresenter.addLike(itemPosition, info.getMomentid(), info.getLikesList());
            }

        }

        @Override
        public void onCommentClick(View v, @NonNull Share info) {
            momentPresenter.showCommentBox(itemPosition, info, null);
        }
    };

    /**
     * ============  tools method block
     */


    protected final View findView(View view, int resid) {
        if (resid > 0 && itemView != null && view == null) {
            return itemView.findViewById(resid);
        }
        return view;
    }


}
