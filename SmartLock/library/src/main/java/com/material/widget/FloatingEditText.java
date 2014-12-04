package com.material.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by IntelliJ IDEA.
 * User: keith.
 * Date: 14-10-30.
 * Time: 15:57.
 */
public class FloatingEditText extends RelativeLayout {

    private TextView mFloatingText;
    private EditText mEditText;
    private View mUnderLineView;

    public FloatingEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FloatingEditText(Context context) {
        super(context);
        init();
    }

    public FloatingEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mFloatingText = new TextView(getContext());
        mFloatingText.setText("Floating text");
        mFloatingText.setGravity(Gravity.CENTER_VERTICAL);
        mFloatingText.setBackgroundColor(Color.GREEN);
        addView(mFloatingText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mEditText = new EditText(getContext());
        mEditText.setBackgroundColor(Color.TRANSPARENT);
        addView(mEditText, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mUnderLineView = new View(getContext());
        mUnderLineView.setBackgroundColor(Color.RED);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                20);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(mUnderLineView, params);
    }
}
