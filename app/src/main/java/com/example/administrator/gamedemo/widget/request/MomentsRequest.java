package com.example.administrator.gamedemo.widget.request;

import android.util.Log;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.bmob.bmob.BmobInitHelper;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import static  com.example.administrator.gamedemo.model.CommentInfo.CommentFields.AUTHOR_USER;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.MOMENT;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.REPLY_USER;

/**
 * Created by 大灯泡 on 2016/10/27.
 * <p>
 * 朋友圈时间线请求
 */

public class MomentsRequest extends BaseRequestClient<List<MomentsInfo>> {

    private int count = 10;
    private int curPage = 0;

    public MomentsRequest() {
    }

    public MomentsRequest setCount(int count) {
        this.count = (count <= 0 ? 10 : count);
        return this;
    }

    public MomentsRequest setCurPage(int page) {
        this.curPage = page;
        return this;
    }


    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        BmobQuery<MomentsInfo> bmobQuery = new BmobQuery<>();
        bmobQuery.include(MomentsInfo.MomentsFields.AUTHOR_USER + "," + MomentsInfo.MomentsFields.HOST);
        bmobQuery.setLimit(count);
        bmobQuery.setSkip(curPage * count);
        bmobQuery.order("-createdAt");
        bmobQuery.findObjects(new FindListener<MomentsInfo>() {
            @Override
            public void done(List<MomentsInfo> list, BmobException e) {
                if (!ToolUtil.isListEmpty(list)) {
                    queryCommentAndLikes(list);
                }else {
                }
            }
        });
    }

    private void queryCommentAndLikes(final List<MomentsInfo> momentsList) {
        /**
         * 因为bmob不支持在查询时把关系表也一起填充查询，因此需要手动再查一次，同时分页也要手动实现。。
         * oRz，果然没有自己写服务器来的简单，好吧，都是在下没钱的原因，我的锅
         */
        for (int i = 0; i < momentsList.size(); i++) {
            final int currentPos = i;
            final MomentsInfo momentsInfo = momentsList.get(i);
            BmobQuery<Students> likesQuery = new BmobQuery<>();
            likesQuery.order("-createdAt");
            likesQuery.addWhereRelatedTo("likes", new BmobPointer(momentsInfo));
            likesQuery.findObjects(new FindListener<Students>() {
                @Override
                public void done(List<Students> list, BmobException e) {
                    BmobQuery<CommentInfo> commentQuery = new BmobQuery<>();
                    commentQuery.include(MOMENT + "," + REPLY_USER + "," + AUTHOR_USER);
                    commentQuery.addWhereEqualTo("moment", momentsInfo);
                    commentQuery.order("-createdAt");
                    commentQuery.findObjects(new FindListener<CommentInfo>() {
                        @Override
                        public void done(List<CommentInfo> list, BmobException e) {
                            if (!ToolUtil.isListEmpty(list)) {
                                momentsInfo.setCommentList(list);
                            }

                            if (e == null) {
                                if (currentPos == momentsList.size() - 1) {
                                    onResponseSuccess(momentsList, getRequestType());
                                    curPage++;
                                }

                            } else {
                                onResponseError(e, getRequestType());
                            }
                        }
                    });
                }
            });
        }
    }
}