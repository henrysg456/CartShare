package com.example.david_000.cartshare;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by david_000 on 10/27/2015.
 */
public class FlyOutContainer extends LinearLayout {

    // References to groups contained in this view.
    private View menu;
    private View content;

    // Constants
    protected static final int menuMargin = 150;

    public enum MenuState {
        CLOSED, OPEN
    };

    // Position information attributes
    protected int currentContentOffset = 0;
    protected MenuState menuCurrentState = MenuState.CLOSED;

    public FlyOutContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FlyOutContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlyOutContainer(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.menu = this.getChildAt(0);
        this.content = this.getChildAt(1);

        this.menu.setVisibility(View.GONE);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed)
            this.calculateChildDimensions();

        this.menu.layout(left, top, right - menuMargin, bottom);

        this.content.layout(left + this.currentContentOffset, top, right + this.currentContentOffset, bottom);
    }  //end onLayout


    public void toggleMenu() {
        switch (this.menuCurrentState) {
            case CLOSED:
                this.menu.setVisibility(View.VISIBLE);
                this.currentContentOffset = this.getMenuWidth();
                this.content.offsetLeftAndRight(currentContentOffset);
                this.menuCurrentState = MenuState.OPEN;
                break;
            case OPEN:
                this.content.offsetLeftAndRight(-currentContentOffset);
                this.currentContentOffset = 0;
                this.menuCurrentState = MenuState.CLOSED;
                this.menu.setVisibility(View.GONE);
                break;
        }

        this.invalidate();
    }  //end toggleMenu

    private int getMenuWidth() {
        return this.menu.getLayoutParams().width;
    }  //end getMenuWidth

    private void calculateChildDimensions() {
        this.content.getLayoutParams().height = this.getHeight();
        this.content.getLayoutParams().width = this.getWidth();

        this.menu.getLayoutParams().width = this.getWidth() - menuMargin;
        this.menu.getLayoutParams().height = this.getHeight();
    }  //end calculateChildDimensions
}  //end FlyOutContainer
