package com.windfind.clubassistant.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.windfind.clubassistant.R;

public class BottomBar extends RelativeLayout {

	private Button mBtnLeft;
	private Button mBtnCenter;
	private Button mBtnRight;

	public BottomBar(Context context) {
		this(context, null);
	}

	public BottomBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public BottomBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BottomBar, defStyle, 0);
		LayoutInflater.from(context).inflate(R.layout.bottom_bar, this);
		initView(array);
		array.recycle();
	}

	private void initView(TypedArray array) {
		mBtnLeft = findViewById(R.id.btn_left);
		String leftText = array.getString(R.styleable.BottomBar_left_btn_text);
		mBtnLeft.setText(leftText);

		mBtnCenter = findViewById(R.id.btn_center);
		String centerText = array.getString(R.styleable.BottomBar_center_btn_text);
		if (!TextUtils.isEmpty(centerText)) {
			mBtnCenter.setText(centerText);
			setCenterButtonVisible(true);
			setCenterButtonEnable(true);
		}

		mBtnRight = findViewById(R.id.btn_right);
		String rightText = array.getString(R.styleable.BottomBar_right_btn_text);
		mBtnRight.setText(rightText);
	}

	public void setLeftButtonListener(OnClickListener listener) {
		mBtnLeft.setOnClickListener(listener);
	}

	public void setLeftButtonVisible(boolean visible) {
		mBtnLeft.setVisibility(visible ? VISIBLE : GONE);
	}

	public void setCenterButtonListener(OnClickListener listener) {
		mBtnCenter.setOnClickListener(listener);
	}

	public void setCenterButtonEnable(boolean isEnable) {
		mBtnCenter.setEnabled(isEnable);
	}

	public void setCenterButtonVisible(boolean visible) {
		mBtnCenter.setVisibility(visible ? VISIBLE : GONE);
	}

	public void setCenterButtonText(String text) {
		mBtnCenter.setText(text);
	}

	public void setRightButtonListener(OnClickListener listener) {
		mBtnRight.setOnClickListener(listener);
	}

	public void setRightButtonEnable(boolean isEnable) {
		mBtnRight.setEnabled(isEnable);
	}

	public void setRightButtonVisible(boolean visible) {
		mBtnRight.setVisibility(visible ? VISIBLE : GONE);
	}

	public void setRightButtonText(String text) {
		mBtnRight.setText(text);
	}

}
