package com.example.administrator.gamedemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.answer.AnswerHistoryActivity;
import com.example.administrator.gamedemo.activity.answer.AnswerListActivity;
import com.example.administrator.gamedemo.activity.answer.HelpActivity;
import com.example.administrator.gamedemo.activity.answer.SelectTypeActivity;
import com.example.administrator.gamedemo.activity.mine.togther.TogetherActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.bean.Scriptures;
import com.example.administrator.gamedemo.utils.UIHelper;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.imageview.MyImageView;
import com.example.administrator.gamedemo.widget.request.CoverRequest;
import com.example.administrator.gamedemo.widget.request.OnResponseListener;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class AnswerFragment extends BaseFragment {

    @BindView(R.id.banner_imageView)
    ImageView bannerImageView;
    @BindView(R.id.tv_scripture)
    TextView tvScripture;

    @BindView(R.id.iv_start)
    MyImageView ivStart;
    @BindView(R.id.iv_togther)
    MyImageView ivTogther;
    @BindView(R.id.iv_history)
    MyImageView ivHistory;
    @BindView(R.id.iv_help)
    MyImageView ivHelp;



    @BindView(R.id.tv_time)
    TextView tv_time;
    public AnswerFragment() {
    }

    public static AnswerFragment getInstance() {
        return answerFragmentHolder.instance;
    }

    public static class answerFragmentHolder {
        public static final AnswerFragment instance = new AnswerFragment();
    }

    @Override
    public void initTheme() {
        getActivity().setTheme(R.style.AppTheme_NoActionBar_Immerse);
    }

    @Override
    public int initCreatView() {
        return R.layout.fragment_answer;
    }

    @Override
    public void initViews() {
//        tv_time.setText(Constants.StringData());

        UIHelper.getScreenHeightPix(mContext);

        ivStart.setOnClickIntent(new MyImageView.OnViewClickListener() {
            @Override
            public void onViewClick(MyImageView view) {
                Intent sIntent = new Intent(getActivity(), SelectTypeActivity.class);
                sIntent.putExtra("yinliang", true);
                startActivity(sIntent);
            }
        });

        ivTogther.setOnClickIntent(new MyImageView.OnViewClickListener() {
            @Override
            public void onViewClick(MyImageView view) {
                Intent pIntent = new Intent(mContext, AnswerListActivity.class);
                startActivityForResult(pIntent, 1);
            }
        });

        ivHistory.setOnClickIntent(new MyImageView.OnViewClickListener() {
            @Override
            public void onViewClick(MyImageView view) {
                Intent pIntent = new Intent(mContext, AnswerHistoryActivity.class);
                startActivityForResult(pIntent, 1);
            }
        });

        ivHelp.setOnClickIntent(new MyImageView.OnViewClickListener() {
            @Override
            public void onViewClick(MyImageView view) {
                Intent pIntent = new Intent(mContext, HelpActivity.class);
                startActivityForResult(pIntent, 1);
            }
        });
        initData();
    }

    @Override
    public void initData() {

        Logger.d("开始开始1111");
        CoverRequest coverRequest = new CoverRequest();
        Logger.d("开始开始查询");
        coverRequest.setOnResponseListener(new OnResponseListener<List<Scriptures>>() {
            @Override
            public void onStart(int requestType) {

            }

            @Override
            public void onSuccess(List<Scriptures> response, int requestType) {
                Logger.d("查询失败"+response.size());
                if(response != null && response.size() > 0) {
                    Scriptures scripturesTemp = response.get(0);
                    tvScripture.setText(scripturesTemp.getsContent());
                    ImageLoadMnanger.INSTANCE.loadImageToCover(bannerImageView, scripturesTemp.getsImage().getFileUrl());
                }
                }

            @Override
            public void onError(BmobException e, int requestType) {
                Logger.d("查询失败"+e.toString());
            }

            @Override
            public void onProgress(int pro) {

            }
        });
        coverRequest.execute();

        tv_time.setText(Constants.StringData());
    }

}
