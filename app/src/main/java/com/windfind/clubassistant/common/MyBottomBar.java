package com.windfind.clubassistant.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class MyBottomBar extends View {

	private Context mContext;

	private List<Class> mFragmentClassList = new ArrayList<>();
	private List<Fragment> mFragmentList = new ArrayList<>();
	private List<String> mTitleList = new ArrayList<>();
	private List<Integer> mIconResNormalList = new ArrayList<>();
	private List<Integer> mIconResFocusedList = new ArrayList<>();
	private List<Bitmap> mIconBitmapNormalList = new ArrayList<>();
	private List<Bitmap> mIconBitmapFocusedList = new ArrayList<>();
	private List<Rect> mIconRectList = new ArrayList<>();

	private Paint mPaint = new Paint();

	private int mContainerId;
	private int mItemCount;
	private int mFocusedIndex;

	private int mTitleColorNormal = Color.parseColor("#999999");
	private int mTitleColorFocused = Color.parseColor("#ff5d5e");

	private int mTitleSizeInDp = 12;
	private int mIconWidth = 20;
	private int mIconHeight = 20;
	private int mTitleIconMargin = 8;

	public MyBottomBar(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public MyBottomBar setContainer(int containerId) {
		mContainerId = containerId;
		return this;
	}

	public void setTitleBeforeAndAfterColor(String beforeResCode, String AfterResCode) {//支持"#333333"这种形式
		mTitleColorNormal = Color.parseColor(beforeResCode);
		mTitleColorFocused = Color.parseColor(AfterResCode);
	}

	public void addItem(Class fragmentClass, String title, int iconResBefore, int iconResAfter) {
		mFragmentClassList.add(fragmentClass);
		mTitleList.add(title);
		mIconResNormalList.add(iconResBefore);
		mIconResFocusedList.add(iconResAfter);
	}

	public void build() {
		mItemCount = mFragmentClassList.size();

		Bitmap normalIcon;
		Bitmap focusedIcon;
		//预创建bitmap的Rect并缓存，预创建icon的Rect并缓存
		for (int i = 0; i < mItemCount; i++) {
			normalIcon = getBitmap(mIconResNormalList.get(i));
			mIconBitmapNormalList.add(normalIcon);

			focusedIcon = getBitmap(mIconResFocusedList.get(i));
			mIconBitmapFocusedList.add(focusedIcon);

			Rect rect = new Rect();
			mIconRectList.add(rect);

			Class clx = mFragmentClassList.get(i);
			try {
				Fragment fragment = (Fragment) clx.newInstance();
				mFragmentList.add(fragment);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		mFocusedIndex = 0;
		switchFragment(mFocusedIndex);

		invalidate();
	}

	private Bitmap getBitmap(int resId) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) mContext.getResources().getDrawable(resId);
		return bitmapDrawable.getBitmap();
	}

	//////////////////////////////////////////////////
	//初始化数据基础
	//////////////////////////////////////////////////

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		initParam();
	}

	private int titleBaseLine;
	private List<Integer> titleXList = new ArrayList<>();

	private int parentItemWidth;

	private void initParam() {
		if (mItemCount != 0) {
			//单个item宽高
			parentItemWidth = getWidth() / mItemCount;
			int parentItemHeight = getHeight();

			//图标边长
			int iconWidth = dp2px(mIconWidth);//先指定20dp
			int iconHeight = dp2px(mIconHeight);

			//图标文字margin
			int textIconMargin = dp2px(((float) mTitleIconMargin)/2);//先指定5dp，这里除以一半才是正常的margin，不知道为啥，可能是图片的原因

			//标题高度
			int titleSize = dp2px(mTitleSizeInDp);//这里先指定10dp
			mPaint.setTextSize(titleSize);
			Rect rect = new Rect();
			mPaint.getTextBounds(mTitleList.get(0), 0, mTitleList.get(0).length(), rect);
			int titleHeight = rect.height();

			//从而计算得出图标的起始top坐标、文本的baseLine
			int iconTop = (parentItemHeight - iconHeight - textIconMargin - titleHeight)/2;
			titleBaseLine = parentItemHeight - iconTop;

			//对icon的rect的参数进行赋值
			int firstRectX = (parentItemWidth - iconWidth) / 2;//第一个icon的左
			for (int i = 0; i < mItemCount; i++) {
				int rectX = i * parentItemWidth + firstRectX;

				Rect temp = mIconRectList.get(i);

				temp.left = rectX;
				temp.top = iconTop ;
				temp.right = rectX + iconWidth;
				temp.bottom = iconTop + iconHeight;
			}

			//标题（单位是个问题）
			for (int i = 0; i < mItemCount; i ++) {
				String title = mTitleList.get(i);
				mPaint.getTextBounds(title, 0, title.length(), rect);
				titleXList.add((parentItemWidth - rect.width()) / 2 + parentItemWidth * i);
			}
		}
	}

	private int dp2px(float dpValue) {
		float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	//////////////////////////////////////////////////
	//根据得到的参数绘制
	//////////////////////////////////////////////////

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);//这里让view自身替我们画背景 如果指定的话

		if (mItemCount != 0) {
			// 画顶线
			mPaint.setColor(Color.parseColor("#aaaaaa"));
			canvas.drawLine(0, 0, parentItemWidth * mItemCount, 0, mPaint);
			canvas.drawLine(0, 1, parentItemWidth * mItemCount, 1, mPaint);

			//画背景
			mPaint.setAntiAlias(false);
			for (int i = 0; i < mItemCount; i++) {
				Bitmap bitmap;
				if (i == mFocusedIndex) {
					bitmap = mIconBitmapFocusedList.get(i);
				} else {
					bitmap = mIconBitmapNormalList.get(i);
				}
				Rect rect = mIconRectList.get(i);
				canvas.drawBitmap(bitmap, null, rect, mPaint);//null代表bitmap全部画出
			}

			//画文字
			mPaint.setAntiAlias(true);
			for (int i = 0; i < mItemCount; i ++) {
				String title = mTitleList.get(i);
				if (i == mFocusedIndex) {
					mPaint.setColor(mTitleColorFocused);
				} else {
					mPaint.setColor(mTitleColorNormal);
				}
				int x = titleXList.get(i);
				canvas.drawText(title, x, titleBaseLine, mPaint);
			}
		}
	}

	//////////////////////////////////////////////////
	//点击事件:我观察了微博和掌盟，发现down和up都在该区域内才响应
	//////////////////////////////////////////////////

	int target = -1;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				target = withinWhichArea((int)event.getX());
				break;
			case MotionEvent.ACTION_UP :
				if (event.getY() < 0) {
					break;
				}
				if (target == withinWhichArea((int)event.getX())) {
					//这里触发点击事件
					switchFragment(target);
					mFocusedIndex = target;
					invalidate();
				}
				target = -1;
				break;
		}
		return true;
		//这里return super为什么up执行不到？是因为return super的值，全部取决于你是否
		//clickable，当你down事件来临，不可点击，所以return false，也就是说，而且你没
		//有设置onTouchListener，并且控件是ENABLE的，所以dispatchTouchEvent的返回值
		//也是false，所以在view group的dispatchTransformedTouchEvent也是返回false，
		//这样一来，view group中的first touch target就是空的，所以intercept标记位
		//果断为false，然后就再也进不到循环取孩子的步骤了，直接调用dispatch-
		// TransformedTouchEvent并传孩子为null，所以直接调用view group自身的dispatch-
		// TouchEvent了
	}

	private int withinWhichArea(int x) { return x/parentItemWidth; }//从0开始

	//////////////////////////////////////////////////
	//碎片处理代码
	//////////////////////////////////////////////////
	private Fragment currentFragment;

	//注意 这里是只支持AppCompatActivity 需要支持其他老版的 自行修改
	protected void switchFragment(int whichFragment) {
		Fragment fragment = mFragmentList.get(whichFragment);
		int frameLayoutId = mContainerId;

		if (fragment != null) {
			FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();
			if (fragment.isAdded()) {
				if (currentFragment != null) {
					transaction.hide(currentFragment).show(fragment);
				} else {
					transaction.show(fragment);
				}
			} else {
				if (currentFragment != null) {
					transaction.hide(currentFragment).add(frameLayoutId, fragment);
				} else {
					transaction.add(frameLayoutId, fragment);
				}
			}
			currentFragment = fragment;
			transaction.commit();
		}
	}
}
