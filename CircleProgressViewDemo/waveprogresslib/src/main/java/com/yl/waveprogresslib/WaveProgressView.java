package com.yl.waveprogresslib;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class WaveProgressView extends View {

	private final int DefaultBackgroundColor = Color.GRAY; // 默认的背景颜色
	private final float DefaultCircleRingWidth = 0;
	private final int DefaultCircleRingColor = Color.BLUE;
	private final int DefaultWaveColor = Color.GREEN;
	private final int DefaultMaxWaveNum = 0;
	private final int DefaultWaveNumRange = 0;
	private final float DefaultContentTextSize = 15;
	private final int DefaultContentTextColor = Color.WHITE;
	private final float DefaultContentTextPadding = 12;
	private final float DefaultWaveHeight = 5.0f;
	private final float DefaultWaveHeightRange = 0;
	private final float DefaultWaveProgress = 0.5f;

	private int mWidth; // 控件的宽度

	private Bitmap mDrawableBitmap; // 背景图片
	private int mBackgroudColor; // 圆形背景的颜色
	private float mCircleRingWidth; // 圆环的宽度
	private int mCircleRingColor; // 圆环的颜色
	private int mWaveColor; // 水波的颜色
	private int mMaxWaveNum; // 最大水波数量
	private int mWaveNumRange; //设置水波数量变化的浮动范围
	private float mWaveHeight; // 波的高度
	private float mWaveHeightRange; //水波高度的浮动范围
	private String mContentText; // 圆环中显示的文字,默认显示为空
	private float mContentTextSize; // 圆环中钟显示文本的大小
	private int mContentTextColor; // 圆环中显示文本的颜色
	private float mContentTextPaading; // 圆环中显示文本与背景圆的填充

	private Paint mBackgroundPaint; // 绘制背景圆的画笔
	private Paint mBackgroundDrawablePaint; // 绘制背景图片的画笔
	private Paint mCircleRingPaint; // 绘制圆环的笔画
	private Paint mWavePaint; // 绘制水波的画笔
	private Paint mContentTextPaint; // 绘制文本的画笔

	private int mMinWidth; // 圆形区域最小的直径，也就是包裹文本
	private Rect mTextRect; // 文本矩形区域

	private Path mPath; // 绘制波纹的路径
	private int mCurrentWaveHeight; // 设置当前水波纹的高度(逻辑)
	private float mCurrentWavePercent; // 设置当前水波的高度百分比(表层)

	private boolean isShow; // 是否绘制背景图片

	private Bitmap mBitmap; // 画纸，用于xfermode模式
	private Canvas mCanvas; // 画板，承接画纸对象

	private Random mRandom;  // 创建随机机器
	
	public WaveProgressView(Context context) {
		this(context, null);
	}

	public WaveProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WaveProgressView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initDatas(context, attrs);
	}

	// 初始化参数
	private void initDatas(Context context, AttributeSet attrs) {
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.circleWaveProgressView);
		Drawable mBackgroudDraw = array
				.getDrawable(R.styleable.circleWaveProgressView_backgroundDrawable);
		mBackgroudColor = array.getColor(
				R.styleable.circleWaveProgressView_backgroundColor,
				DefaultBackgroundColor);
		mCircleRingWidth = array.getDimension(
				R.styleable.circleWaveProgressView_circleRingWidth,
				DefaultCircleRingWidth);
		mCircleRingColor = array.getColor(
				R.styleable.circleWaveProgressView_circleRingColor,
				DefaultCircleRingColor);
		mWaveColor = array.getColor(
				R.styleable.circleWaveProgressView_waveColor, DefaultWaveColor);
		mCurrentWavePercent = array.getFloat(R.styleable.circleWaveProgressView_waveProgress, DefaultWaveProgress);
		mMaxWaveNum = array.getInteger(
				R.styleable.circleWaveProgressView_waveMaxNum,
				DefaultMaxWaveNum);
		mWaveNumRange = array.getInteger(R.styleable.circleWaveProgressView_waveNumRange, DefaultWaveNumRange);
		mWaveHeight = array.getDimension(
				R.styleable.circleWaveProgressView_waveHeight,
				DefaultWaveHeight);
		mWaveHeightRange = array.getDimension(R.styleable.circleWaveProgressView_waveHeightRange, DefaultWaveHeightRange);
		mContentText = array
				.getString(R.styleable.circleWaveProgressView_contentText);
		mContentTextSize = array.getDimension(
				R.styleable.circleWaveProgressView_contentTextSize,
				DefaultContentTextSize);
		mContentTextColor = array.getColor(
				R.styleable.circleWaveProgressView_contentTextColor,
				DefaultContentTextColor);
		mContentTextPaading = array.getDimension(
				R.styleable.circleWaveProgressView_contentTextPaddinng,
				DefaultContentTextPadding);
		array.recycle();

		// 初始化所有的画笔
		mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mBackgroundPaint.setColor(mBackgroudColor);
		mBackgroundPaint.setStyle(Style.FILL);

		mBackgroundDrawablePaint = new Paint();
		mBackgroundDrawablePaint
				.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		mCircleRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCircleRingPaint.setColor(mCircleRingColor);
		mCircleRingPaint.setStrokeWidth(mCircleRingWidth);
		mCircleRingPaint.setStyle(Style.STROKE);

		mWavePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mWavePaint.setStyle(Style.FILL);
		mWavePaint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		mWavePaint.setColor(mWaveColor);

		mContentTextPaint = new Paint();
		mContentTextPaint.setTextSize(mContentTextSize);
		mContentTextPaint.setColor(mContentTextColor);
		
		mRandom = new Random();

		// 加载Bitmap图像
		if (mBackgroudDraw != null) {
			Drawable2Bitmap(mBackgroudDraw);
			isShow = true;
		}

		// 获取内容文本的宽高
		mTextRect = new Rect();
		getTextRec();
	}

	/**
	 * 获取文本的宽高，并计算出view的包括内容时的宽度
	 */
	private void getTextRec() {
		if (TextUtils.isEmpty(mContentText))
			mContentText = "";
		mContentTextPaint.getTextBounds(mContentText, 0, mContentText.length(),
				mTextRect);
		mMinWidth = (int) (Math.max(mTextRect.width(), mTextRect.height())
				+ mContentTextPaading * 2 + mCircleRingWidth * 2);
	}

	private void Drawable2Bitmap(Drawable mBackgroudDraw) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) mBackgroudDraw;
		bitmapDrawable.setAntiAlias(true);
		mDrawableBitmap = bitmapDrawable.getBitmap();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		mWidth = MeasureSpec.getSize(widthMeasureSpec);
		int WidthMode = MeasureSpec.getMode(widthMeasureSpec);

		if (WidthMode != MeasureSpec.EXACTLY) {
			mWidth = mMinWidth;
		}

		mBitmap = Bitmap.createBitmap(mWidth, mWidth, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		setmCurrentWaveHeightPercent(mCurrentWavePercent);
		setMeasuredDimension(mWidth, mWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 根据计算控件的宽高来初始化图片的大小
		mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		int center = mWidth / 2;
		// 绘制背景圆
		mCanvas.drawCircle(center, center,
				(mWidth - mCircleRingWidth * 2) / 2, mBackgroundPaint);

		// 是否绘制图片
		if (isShow && mDrawableBitmap != null) {
			// 绘制背景
			Matrix matrix = new Matrix();
			matrix.setScale(mWidth * 1.0f / mDrawableBitmap.getWidth(), mWidth
					* 1.0f / mDrawableBitmap.getHeight());
			mCanvas.drawBitmap(mDrawableBitmap, matrix,
					mBackgroundDrawablePaint);
		}
		// 绘制波纹
		if (mPath == null) {
			mPath = new Path();
		}
		mPath.reset();
		mPath.moveTo(mWidth, mCurrentWaveHeight);
		mPath.lineTo(mWidth, mWidth);
		mPath.lineTo(0, mWidth);
		mPath.lineTo(0, mCurrentWaveHeight);
		
		//当前峰的数目
		int currentWaveNum = mMaxWaveNum;
		if(mWaveNumRange != 0)
			currentWaveNum = mMaxWaveNum - mRandom.nextInt(mWaveNumRange);
		if(currentWaveNum > 0){
			//当前每个峰值的跨长
			float spanWidth = mWidth * 1.0f / currentWaveNum;
			for (int i = 0; i < currentWaveNum; i++) {
				float currentHeight = mWaveHeight - mRandom.nextFloat() * mWaveHeightRange;
				currentHeight = (i % 2 == 0? currentHeight : -currentHeight);
				mPath.rQuadTo(spanWidth / 2, currentHeight, spanWidth, 0);
			}
		}
		mCanvas.drawPath(mPath, mWavePaint);
		// 绘制圆环
		if (mCircleRingWidth != 0) {
			mCanvas.drawCircle(center, center,
					(mWidth - mCircleRingWidth) / 2, mCircleRingPaint);
		}
		// 绘制文字
		mCanvas.drawText(mContentText, (mWidth - mTextRect.width()) / 2,
				mWidth / 2 + mTextRect.height() / 2, mContentTextPaint);
		canvas.drawBitmap(mBitmap, 0, 0, null);
	}

	/**
	 * 获取当前水波纹进度百分比
	 * 
	 * @return 浮点数，取值为0-1.0
	 */
	public float getmCurrentWaveHeightPercent() {
		return mCurrentWavePercent;
	}

	/**
	 * 设置水波纹进度条显示的进度
	 * 
	 * @param percent
	 *            进度百分值 0-1.0
	 */
	public void setmCurrentWaveHeightPercent(float percent) {
		if (percent < 0) {
			percent = 0;
		}
		if (percent > 1) {
			percent = 1;
		}
		mCurrentWavePercent = percent;
		mCurrentWaveHeight = (int) ((1 - percent) * mWidth);
		invalidate();
	}

	/**
	 * 获取显示圆形背景图片显示状态
	 * 
	 * @return
	 */
	public boolean isDrawableShow() {
		return isShow;
	}

	/**
	 * 设置圆形背景图片显示状态，在存在背景图片的情况下，若设置为true，则显示背景图片，否则不显示
	 * 
	 * @param isShow
	 *            设置背景圆形图片能否被显示
	 */
	public void setDrawableShow(boolean isShow) {
		this.isShow = isShow;
	}

	/**
	 * 设置View的显示大小。由于View的OnMeasure方法未被调用，所以在Activity及其子类的OnCreate方法中设置该属性无效！
	 * 
	 * @return 获取View显示的宽度。
	 */
	public int getmWidth() {
		return mWidth;
	}

	/**
	 * 获取View的宽度。在OnMeasure方法未被调用之前获取到的Width = 0。
	 * 
	 * @param mWidth
	 *            展示的高度
	 */
	public void setmWidth(int mWidth) {
		this.mWidth = mWidth;
		invalidate();
	}

	/**
	 * 获取View的高度。在OnMeasure方法未被调用之前获取到的Height = 0;
	 * 
	 * @return view显示的高度
	 */
	public int getmHeight() {
		return mWidth;
	}

	/**
	 * 设置View的高度，由于View的OnMeasure方法未被调用，所以在Activity及其子类的OnCreate方法中设置该属性无效！
	 * 
	 * @param mHeight
	 */
	public void setmHeight(int mHeight) {
		this.mWidth = mHeight;
		invalidate();
	}

	/**
	 * 获取圆形背景区域的图形
	 * 
	 * @return
	 */
	public Bitmap getmBackgroudDraw() {
		return mDrawableBitmap;
	}

	/**
	 * 设置背景圆形区域显示的图片
	 * 
	 * @param mBackgroudDraw
	 *            必须属于bitmapdrawable的子类，否则抛出参数异常！
	 */
	public void setmBackgroudDraw(Drawable mBackgroudDraw) {
		if (mBackgroudDraw instanceof BitmapDrawable) {
			Drawable2Bitmap(mBackgroudDraw);
			isShow = true;
			invalidate();
		} else {
			throw new IllegalArgumentException(
					"argument is instanceof  BitmapDrawable!");
		}
	}

	/**
	 * 返回圆形背景区域颜色
	 * 
	 * @return
	 */
	public int getmBackgroudColor() {
		return mBackgroudColor;
	}

	/**
	 * 设置圆形背景区域的颜色
	 * 
	 * @param mBackgroudColor
	 */
	public void setmBackgroudColor(int mBackgroudColor) {
		this.mBackgroudColor = mBackgroudColor;
		mBackgroundPaint.setColor(mBackgroudColor);
		invalidate();
	}

	/**
	 * 获取外轮廓的宽度,即环的厚度
	 * 
	 * @return
	 */
	public float getmCircleRingWidth() {
		return mCircleRingWidth;
	}

	/**
	 * 设置外轮廓的宽度，即环的厚度
	 * 
	 * @param mCircleRingWidth
	 *            环的厚度
	 */
	public void setmCircleRingWidth(float mCircleRingWidth) {
		this.mCircleRingWidth = mCircleRingWidth;
		mCircleRingPaint.setStrokeWidth(mCircleRingWidth);
		invalidate();
	}

	/**
	 * 获取外轮廓的颜色，即环的颜色
	 * 
	 * @return
	 */
	public int getmCircleRingColor() {
		return mCircleRingColor;
	}

	/**
	 * 设置外轮廓的颜色，即环的颜色
	 * 
	 * @param mCircleRingColor
	 *            外环颜色
	 */
	public void setmCircleRingColor(int mCircleRingColor) {
		this.mCircleRingColor = mCircleRingColor;
		mCircleRingPaint.setColor(mCircleRingColor);
		invalidate();
	}

	/**
	 * 获取进度水纹的颜色
	 * 
	 * @return
	 */
	public int getmWaveColor() {
		return mWaveColor;
	}

	/**
	 * 设置进度波纹的颜色
	 * 
	 * @param mWaveColor
	 *            波纹的颜色
	 */
	public void setmWaveColor(int mWaveColor) {
		this.mWaveColor = mWaveColor;
		mWavePaint.setColor(mWaveColor);
		invalidate();
	}

	/**
	 * 获取横向水波纹起伏最大数目
	 * 
	 * @return
	 */
	public int getmMaxWaveNum() {
		return mMaxWaveNum;
	}

	/**
	 * 设置横向水波纹起伏的最大数目，数值越大，横向水波纹密度越大。
	 * 
	 * @param mMaxWaveNum
	 */
	public void setmMaxWaveNum(int mMaxWaveNum) {
		this.mMaxWaveNum = mMaxWaveNum;
		invalidate();
	}
	
	/**
	 * 获取水波数量相对于最大水波数 MaxWaveNum的最大偏移量，不设定默认为0
	 * @return
	 */
	public int getmWaveNumRange() {
		return mWaveNumRange;
	}

	/**
	 * 设置水波数量波动范围，取值为 0-MaxWaveNum，如果数量大于MaxWaveNum在水波上升过程中就有时会出现平波的状态。
	 * @param mWaveNumRange
	 */
	public void setmWaveNumRange(int mWaveNumRange) {
		this.mWaveNumRange = mWaveNumRange;
		invalidate();
	}

	/**
	 * 获取水波波峰的变化最大落差，不设定默认为0
	 * @return
	 */
	public float getmWaveHeightRange() {
		return mWaveHeightRange;
	}

	/**
	 * 设置水波波峰相对于波峰的最大落差。
	 * @param mWaveHeightRange
	 */
	public void setmWaveHeightRange(float mWaveHeightRange) {
		this.mWaveHeightRange = mWaveHeightRange;
		invalidate();
	}

	/**
	 * 获取波峰的最大高度，即水波起伏的落差。
	 * 
	 * @return
	 */
	public float getmWaveHeight() {
		return mWaveHeight;
	}

	/**
	 * 设置波峰的最大高度，即水波起伏的落差。数值越大，水波起伏越明显。
	 * 
	 * @param mWaveHeight
	 */
	public void setmWaveHeight(float mWaveHeight) {
		this.mWaveHeight = mWaveHeight;
		invalidate();
	}

	/**
	 * 获取控件上面显示的文本，若控件上面没有显示的文本，则返回空字符串。
	 * 
	 * @return
	 */
	public String getmContentText() {
		return mContentText;
	}

	/**
	 * 设置显示文本的内容
	 * 
	 * @param mContentText
	 */
	public void setmContentText(String mContentText) {
		this.mContentText = mContentText;
		resizeDimension();
	}

	/**
	 * 获取显示文本的大小
	 * 
	 * @return
	 */
	public float getmContentTextSize() {
		return mContentTextSize;
	}

	/**
	 * 设置显示文本的大小
	 * 
	 * @param mContentTextSize
	 */
	public void setmContentTextSize(float mContentTextSize) {
		this.mContentTextSize = mContentTextSize;
		mContentTextPaint.setTextSize(mContentTextSize);
		resizeDimension();
	}

	/**
	 * 如果测量模式为 wrap_content,当更改显示的文本或者是更改显示文本字体大小就需要重新设定
	 */
	private void resizeDimension() {
		getTextRec();
		requestLayout();
	}

	/**
	 * 获取显示文本的颜色值
	 * 
	 * @return
	 */
	public int getmContentTextColor() {
		return mContentTextColor;
	}

	/**
	 * 设置显示文本的颜色
	 * 
	 * @param mContentTextColor
	 */
	public void setmContentTextColor(int mContentTextColor) {
		this.mContentTextColor = mContentTextColor;
		mContentTextPaint.setColor(mContentTextColor);
		invalidate();
	}

	/**
	 * 获取显示文本与圆形背景区域的间距
	 * 
	 * @return
	 */
	public float getmContentTextPaading() {
		return mContentTextPaading;
	}

	/**
	 * 设置显示文本与圆形背景区域的间距
	 * 
	 * @param mContentTextPaading
	 */
	public void setmContentTextPaading(float mContentTextPaading) {
		this.mContentTextPaading = mContentTextPaading;
		resizeDimension();
	}

	private int mAnimationPeriod = 200; // 设置动画播放间隔

	/**
	 * 设置水波上升的速度（时间：毫秒）即每隔多少毫秒水波上升一度
	 * @param mAnimationPeriod
	 */
	public void setmAnimationPeriod(int mAnimationPeriod) {
		this.mAnimationPeriod = mAnimationPeriod;
	}

	/**
	 *返回水波上升的速度
	 * @return 毫秒值，即多少毫秒上升一度
	 */
	public int getmAnimationPeriod(){
		return mAnimationPeriod;
	}
	
	private Timer mTimer;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
				setmCurrentWaveHeightPercent(mCurrentWavePercent + 0.01f);
				if(mListener != null)
					mListener.onProgressChange(mCurrentWavePercent);
		}
	};

	/**
	 * 开启水位上升动画，如果开启水位动画循环播放时，需要手动调用mEndAnimation()在合适的时间关闭，如进行界面跳转时、程序退出时。
	 * @param isloop  是否循环播放
	 */
	public void mStartAnimation(final boolean isloop) {
		if (mTimer == null) {
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					mHandler.sendEmptyMessage(0x110);
					if (mCurrentWavePercent == 1.0f) {
						mCurrentWavePercent = 0;
						if (!isloop) {
							mEndAnimation();
						}
					}
				}
			}, 0, mAnimationPeriod);
		}

	}

	/**
	 * 暂停水波上升动画的播放
	 */
	public void mEndAnimation() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	
	private AnimatorProgressListener mListener;

	public interface AnimatorProgressListener{
		/**
		 *  播放进度动画时，当进度发生变化时回调,参数percent的取值范围为：  0 ~ 1
		 */
		void onProgressChange(float percent);
	}
	
	public void setonAnimatorProgressListener(AnimatorProgressListener listener){
		mListener = listener;
	}

}
