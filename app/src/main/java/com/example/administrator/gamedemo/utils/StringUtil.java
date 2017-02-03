package com.example.administrator.gamedemo.utils;

import android.text.TextUtils;

/**
 * Created by 大灯泡 on 2016/10/28.
 * <p>
 * 字符串工具类
 */

public class StringUtil {

    public static boolean noEmpty(String originStr) {
        return !TextUtils.isEmpty(originStr);
    }


    public static boolean noEmpty(String... originStr) {
        boolean noEmpty = true;
        for (String s : originStr) {
            if (TextUtils.isEmpty(s)) {
                noEmpty = false;
                break;
            }
        }
        return noEmpty;
    }

    /**
     * 检验邮箱格式是否正确
     * @param target
     * @return
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }
}
