package com.zhjaid.GreedySnake;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

/**
 * from:zhjaid
 * email:zhjaid@163.com
 */
public class TouchMoveView extends View {
    public TouchMoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchMoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    int mX, mY;
    int yX, yY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        int x = (int) event.getX();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            mX = x;
            mY = y;
        }
        if (action == MotionEvent.ACTION_MOVE) {

        }
        if (action == MotionEvent.ACTION_UP) {
            yX = x;
            yY = y;
            if (mY - yY > 555) {
                Toast.makeText(getContext(), "向上", Toast.LENGTH_LONG).show();
                return true;
            }
            if (yY - mY > 555) {
                Toast.makeText(getContext(), "向下", Toast.LENGTH_LONG).show();
                return true;
            }
            if (mX - yX > 300) {
                Toast.makeText(getContext(), "向左", Toast.LENGTH_LONG).show();
                return true;
            }
            if (yX - mX > 300) {
                Toast.makeText(getContext(), "向右", Toast.LENGTH_LONG).show();
                return true;
            }
        }
        return true;
    }
}
