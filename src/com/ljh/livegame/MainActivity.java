package com.ljh.livegame;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity implements SeekBar.OnSeekBarChangeListener{
	private SeekBar mBurnBar = null;
	private SeekBar mLiveBar = null;
	private SeekBar mMultiplyRateBar = null;
	private TextView mBurnText = null;
	private TextView mLiveText = null;
	private TextView mMultiplyRateText = null;
	private GameSurfaceView mGameView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mBurnBar = (SeekBar)findViewById(R.id.burn_bar);
		mLiveBar = (SeekBar)findViewById(R.id.live_bar);
		mMultiplyRateBar = (SeekBar)findViewById(R.id.multiply_rate_bar);
		
		mBurnText = (TextView)findViewById(R.id.burn_text);
		mLiveText = (TextView)findViewById(R.id.live_text);
		mMultiplyRateText = (TextView)findViewById(R.id.multiply_rate_text);
		
		mGameView = (GameSurfaceView)findViewById(R.id.game_view);
		
		mBurnBar.setOnSeekBarChangeListener(this);
		mLiveBar.setOnSeekBarChangeListener(this);
		mMultiplyRateBar.setOnSeekBarChangeListener(this);
		
		updateProgressValue();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, 3, 0, "3x3");
		menu.add(Menu.NONE, 4, 0, "4x4");
		menu.add(Menu.NONE, 8, 0, "8x8");
		menu.add(Menu.NONE, 16, 0, "16x16");
		menu.add(Menu.NONE, 32, 0, "32x32");
		menu.add(Menu.NONE, 64, 0, "64x64");
		menu.add(Menu.NONE, 96, 0, "96x96");
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		super.onOptionsItemSelected(item);
		mGameView.setRowAndCol(item.getItemId());
		return true;
	}
	
	protected void updateProgressValue(){
		mBurnText.setText(String.valueOf(mBurnBar.getProgress()));
		mLiveText.setText(String.valueOf(mLiveBar.getProgress()));
		mMultiplyRateText.setText(String.valueOf(mMultiplyRateBar.getProgress()+1));
	}
	
	public void onRandomMap(View view){
		mGameView.pauseGame();
		mGameView.randomMap();
	}
	
	public void onClearMap(View view){
		mGameView.pauseGame();
		mGameView.clearMap();
	}
	
	public void onStart(View view){
		if(mGameView.isGamePause()){
			mGameView.setBurnValue(mBurnBar.getProgress());
			mGameView.setLiveValue(mLiveBar.getProgress());
			mGameView.setMultiplyRate(mMultiplyRateBar.getProgress()+1);
			mGameView.startGame();
			
			((Button)view).setText(R.string.pause_game);
		}else{
			mGameView.pauseGame();
			
			((Button)view).setText(R.string.start_game);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		updateProgressValue();
		
		switch(seekBar.getId()) {
	        case R.id.burn_bar:{
	        	mGameView.setBurnValue(seekBar.getProgress());
	            break;
	        }
	        case R.id.live_bar: {
	        	mGameView.setLiveValue(seekBar.getProgress());
	            break;
	        }
	        case R.id.multiply_rate_bar:{
	        	mGameView.setMultiplyRate(seekBar.getProgress()+1);
	        	
	        	break;
	        }
	        default:
	            break;
		}	
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}

}
