package com.example.administrator.gamedemo.activity.answer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.mine.FunctionInfoActivity;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

import butterknife.BindView;

/**
 * @auther lixu
 * Created by lixu on 2017/2/4 0004.
 */
public class HelpActivity extends BaseActivity{



    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_answer_help);
    }

    @Override
    public void initViews() {

        mToolbar.setTitle("答题帮助");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);

    }

    @Override
    public void initData() {
    }


}
