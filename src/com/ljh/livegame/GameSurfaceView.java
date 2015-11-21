package com.ljh.livegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class GameSurfaceView extends SurfaceView
	implements SurfaceHolder.Callback, OnTouchListener{
	private float mCellSize = 5;
	private int mMultiplyRate;
	private int mRow;
	private int mCol;
	private int mBurnValue;
	private int mLiveValue;
	private SurfaceHolder mHolder;
	private Thread mRenderThread = null;
	private boolean mIsPause = true;
	private boolean mIsSurfaceDestory;
	private boolean[][] mOldMap;
	private boolean[][] mNewMap;
	private Paint mPaint = new Paint();
	private RectF mCellRect = new RectF();
	private int mGeneration = 0;
	
	public GameSurfaceView(Context context) {
		super(context);
		init();
	}
	
	public GameSurfaceView(Context context,AttributeSet attrs) {
		super(context,attrs);
		init();
	}
	
	public GameSurfaceView(Context context,AttributeSet attrs, int arg0) {
		super(context,attrs,arg0);
		init();
	}
	
	protected void init(){
		setOnTouchListener(this);
		mHolder = this.getHolder();
		mHolder.addCallback(this);
		setFocusable(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		//正方形
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		mRow = (int) (width/mCellSize);
		mCol = (int) (width/mCellSize);
		System.out.println("row : " + mRow  + " col : " + mCol);
		
		mOldMap = new boolean[mRow][mCol];
		mNewMap = new boolean[mRow][mCol];
	}
	
	public void setBurnValue(int value){
		mBurnValue = value;
	}
	
	public void setLiveValue(int value){
		mLiveValue = value;
	}
	
	public synchronized void setMultiplyRate(int value){
		mMultiplyRate = 1000/value;
	}
	
	public synchronized void startGame(){
		mIsPause = false;
		if(mRenderThread == null){
			mRenderThread = new Thread(new TimeThread());
			mRenderThread.start();
		}
	}
	
	public synchronized void pauseGame(){
		mIsPause = true;
	}
	
	public synchronized boolean isGamePause(){
		return mIsPause;
	}
	
	public void setRowAndCol(int row_and_col){
		mCellSize = (float) (1.0*this.getWidth() / row_and_col);
		mRow = row_and_col;
		mCol = row_and_col;
		System.out.println("row : " + mRow  + " col : " + mCol);
		
		mOldMap = new boolean[mRow][mCol];
		mNewMap = new boolean[mRow][mCol];
		
		pauseGame();
		clearMap();
		render();
	}
	
	public void randomMap(){
		for(int i = 0;i < mRow;++i){
			for(int j = 0;j < mCol;++j){
				mOldMap[i][j] = false;
				mNewMap[i][j] = Math.random() > 0.8;
			}
		}
		render();
	}
	
	public void clearMap(){
		for(int i = 0;i < mRow;++i){
			for(int j = 0;j < mCol;++j){
				mOldMap[i][j] = false;
				mNewMap[i][j] = false;
			}
		}
		render();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int i = (int) (event.getY()/mCellSize);
		int j = (int) (event.getX()/mCellSize);
		if(i >= 0 && j >= 0 && i <mRow && j < mCol){
			if(event.getAction() == MotionEvent.ACTION_DOWN ){
				//Down事件翻转活死状态
				mNewMap[i][j] = !mNewMap[i][j];
			}else if(event.getAction() == MotionEvent.ACTION_MOVE ){
				//Move事件直接设为活状态
				mNewMap[i][j] = true;
			}
			render();
		}
		
		return true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		mIsSurfaceDestory = false;
		render();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		mIsSurfaceDestory = true;
	}
	
	protected int getLivingNumberSurround(boolean[][] map, int rIndex, int cIndex){
		int count = 0;
		for(int i = rIndex - 1;i <= rIndex + 1;++i){
			for(int j = cIndex - 1;j <= cIndex + 1;++j){
				if(i < 0 || j < 0 || i >= mRow || j >= mCol || 
						(i == rIndex && j == cIndex))
					continue;
				if(map[i][j] == true){
					++count;
				}
			}
		}
		return count;
	}
	
	public void update(){
		for(int i = 0;i < mRow;++i){
			for(int j = 0;j < mCol;++j){
				mOldMap[i][j] = mNewMap[i][j];
			}
		}
		
		int count = 0;
		for(int i = 0;i < mRow;++i){
			for(int j = 0;j < mCol;++j){
				count = getLivingNumberSurround(mOldMap, i, j);
				if(count == mBurnValue){
					mNewMap[i][j] = true;
				}else if(count == mLiveValue){
					mNewMap[i][j] = mOldMap[i][j];
				}else{
					mNewMap[i][j] = false;
				}
			}
		}
		
		++mGeneration;
//		TextView view = (TextView)findViewById(R.id.multiply_generation_text);
//		view.setText("第 " + mGeneration + " 代");
	}
	
	public synchronized void render(){
		if(mIsSurfaceDestory == true){
			return;
		}
		
		Canvas canvas = mHolder.lockCanvas();
		canvas.drawColor(Color.WHITE);
		
		mPaint.setColor(0x44888888);
		for(int i = 0;i <= mRow;++i){
			canvas.drawLine(0, i*mCellSize, mCol*mCellSize, i*mCellSize, mPaint);
		}
		for(int j = 0;j <= mCol;++j){
			canvas.drawLine(j*mCellSize, 0, j*mCellSize, mRow*mCellSize, mPaint);
		}
		
		
		for(int i = 0;i < mRow;++i){
			for(int j = 0;j < mCol;++j){
				if(mNewMap[i][j] == true){
					mPaint.setColor(Color.BLACK);
					mCellRect.set(j*mCellSize,i*mCellSize,(j+1)*mCellSize,(i+1)*mCellSize);
					canvas.drawRect(mCellRect, mPaint);
				}
			}
		}
		
		mHolder.unlockCanvasAndPost(canvas);
	}
	
	class TimeThread implements Runnable{
		@Override
		public synchronized void run() {
			while(true){
				try {
					if(mIsPause == false){
						update();
						render();
					}
					
					Thread.sleep(mMultiplyRate);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
