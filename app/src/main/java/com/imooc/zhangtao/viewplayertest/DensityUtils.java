package com.imooc.zhangtao.viewplayertest;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by zhangtao on 17/2/6.
 */

public class DensityUtils
{
    public static int dp2px(Context context , float dp)
    {
        //获取设备屏幕密度
        float density = context.getResources().getDisplayMetrics().density;
        //加0.5是为了四舍五入
        int px = (int) (dp * density + 0.5f);
        return px;
    }

    public static float px2dp(Context context , float px)
    {
        float density = context.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }

    public static int sp2px(Context context , int sp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP , sp , context.getResources().getDisplayMetrics());
    }

    public static int dp2Px(Context context , float dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , dp , context.getResources().getDisplayMetrics());
    }
}
