package com.imooc.zhangtao.viewplayertest;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by zhangtao on 17/2/6.
 */

public class FullVideoView extends VideoView
{
    int defaultWidth = 1920;
    int defaultHeight = 1080;

    public FullVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public FullVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public FullVideoView(Context context) {
        super(context);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(defaultWidth, widthMeasureSpec);
        int height = getDefaultSize(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
