package com.bumblebeem.android.bumblebeem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class CustomEditText extends EditText
{

    public CustomEditText(Context context)
    {
        super(context);
        init(context);
    }

    public CustomEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }

    public void init(Context context)
    {
        try
        {
            Typeface myFont = Typeface.createFromAsset(context.getAssets(), "fonts/Merriweather-Bold.ttf");

            super.setTypeface(myFont);
        }
        catch (Exception e)
        {
            Log.e("CustomEditText", e.toString());
        }
    }
}