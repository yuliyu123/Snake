package com.example.faw.snake;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by faw on 16/10/4.
 */

public class SnakeView extends TileView{

	private static final String TAG = "SnakeView";

    private int mMode = READY;
    public final static int PAUSE = 0;
	public final static int READY = 1;
	public final static int RUNNING = 2;
	public final static int LOSE = 3;

    private int mDirection = NORTH;
    private int mNextDirection = NORTH;
    private final static int NORTH = 1;
    private final static int SOUTH = 2;
    private final static int EAST = 3;
    private final static int WEST = 4;

    private final static int RED_STAR = 1;
    private final static int YELLOW_STAR = 2;
    private final static int GREEN_STAR = 3;

    private long mScore = 0;
    private long mMoveDelay = 600;
    
    private Bitmap[] mTileArray;

    private long mLastMove;

    private TextView mStatusText;

    private ArrayList<Coordiate> mSnakeTrail = new ArrayList<Coordiate>();
    private ArrayList<Coordiate> mAppleList = new ArrayList<Coordiate>();

    private static final Random RNG = new Random();


	private RefreshHandler mReddrawHandler = new RefreshHandler();

	class RefreshHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			SnakeView.this.update();
			SnakeView.this.invalidate();
		}

		public void sleep(long delayMills){
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0),delayMills);
		}

	};

	public SnakeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSnakeView();
	}

	public SnakeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initSnakeView();
	}

	private void initSnakeView() {
		setFocusable(true);

		Resources r = this.getContext().getResources();

		resetTiles(4);
		loadTile(RED_STAR,r.getDrawable(R.drawable.redstar));
		loadTile(YELLOW_STAR,r.getDrawable(R.drawable.redstar));
		loadTile(GREEN_STAR,r.getDrawable(R.drawable.redstar));

	}

	public void initNewGame(){
		mSnakeTrail.clear();
		mAppleList.clear();

		mSnakeTrail.add(new Coordiate(7,7));
		mSnakeTrail.add(new Coordiate(6,7));
		mSnakeTrail.add(new Coordiate(5,7));
		mSnakeTrail.add(new Coordiate(4,7));
		mSnakeTrail.add(new Coordiate(3,7));
		mSnakeTrail.add(new Coordiate(2,7));

		mNextDirection = NORTH;

		addRandomApple();
		addRandomApple();

		mMoveDelay = 600;
		mScore = 0;


	}


    private int[] coordArrayListToArray(ArrayList<Coordiate> cvec){
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count ; index ++){
            Coordiate c= cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }


    public Bundle saveState(){
        Bundle map = new Bundle();

        map.putIntArray("mAppleList",coordArrayListToArray(mAppleList));
        map.putInt("mDirection",Integer.valueOf(mDirection));
        map.putInt("mNextDirection",Integer.valueOf(mNextDirection));
        map.putLong("mMoveDelay",Long.valueOf(mMoveDelay));
        map.putLong("mScore",Long.valueOf(mScore));
        map.putIntArray("mSnakeTrail",coordArrayListToArray(mSnakeTrail));

        return map;
    }

	private ArrayList<Coordiate> coordArrayToArrayList(int[] rawArray){
		ArrayList<Coordiate> coordArrayList = new ArrayList<Coordiate>();

		int coordCount = rawArray.length;
		for (int index = 0; index < coordCount ; index ++){
			Coordiate c = new Coordiate(rawArray[index],rawArray[index + 1]);
			coordArrayList.add(c);

		}
		return coordArrayList;

	}

    public void restoreState(Bundle icicle){
        setMode(PAUSE);

        mAppleList = coordArrayToArrayList(icicle.getIntArray("mAppleList"));
        mDirection = icicle.getInt("mDirection");
        mNextDirection = icicle.getInt("mNextDirection");
        mMoveDelay = icicle.getLong("mMoveDelay");
        mScore = icicle.getLong("mScore");
        mSnakeTrail = coordArrayToArrayList(icicle.getIntArray("mSnakeTrail"));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
            if (mMode == READY || mMode == LOSE){

                initNewGame();
                setMode(RUNNING);
                update();
                return (true);
            }
            if (mMode == PAUSE){
                setMode(RUNNING);
                update();
	            return (true);
            }

            if (mDirection != SOUTH){
                mNextDirection = NORTH;
            }
	        return (true);

        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            if (mDirection != NORTH){
	            mNextDirection = SOUTH;
            }
	        return (true);

        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){

            if (mDirection != EAST){
                mNextDirection = WEST;
            }
	        return (true);

        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){

            if (mDirection != WEST){
	            mNextDirection = EAST;
            }
	        return (true);
        }
        return super.onKeyDown(keyCode, event);
    }





    private void addRandomApple(){
        Coordiate newCoord = null;
        boolean found = false;
        while(!found){

            int newX = 1 + RNG.nextInt(mXTileCount - 2);
            int newY = 1 + RNG.nextInt(mYTileCount - 2);
            newCoord = new Coordiate(newX , newY);

            boolean collision = false;
            int snakeLength = mSnakeTrail.size();
            for (int index = 0; index < snakeLength ; index ++){
                if (mSnakeTrail.get(index).equals(newCoord)){
                    collision = true;
                }
            }

            found = !collision;
        }

        if (newCoord == null){
            Log.e(TAG , "somehow ended up with a null Coord");
        }
        mAppleList.add(newCoord);
    }

    public void update(){
        if (mMode == RUNNING){
            long now = System.currentTimeMillis();

            if (now - mLastMove > mMoveDelay){
                clearTiles();
                updateApples();
                updateSnakes();
                undateWales();
                mLastMove = now;
            }
            mReddrawHandler.sleep(mMoveDelay);
        }
    }

    private void undateWales() {
        for (int x = 0; x < mXTileCount; x ++){
            setTile(GREEN_STAR, x, 0);
            setTile(GREEN_STAR, x, mYTileCount - 1);
        }

        for (int y = 1 ; y < mYTileCount ; y++){
            setTile(GREEN_STAR , 0 , y);
            setTile(GREEN_STAR , mXTileCount - 1 , y);
        }
    }

    private void updateSnakes() {
        boolean growSnake = false;

        Coordiate head = mSnakeTrail.get(0);
        Coordiate newHead = new Coordiate(1,2);

        mDirection = mNextDirection;

        switch (mDirection){
            case EAST:
                newHead = new Coordiate(head.x + 1, head.y);
                break;

            case WEST:
                newHead = new Coordiate(head.x - 1, head.y);
                break;

            case NORTH:
                newHead = new Coordiate(head.x , head.y - 1);
                break;

            case SOUTH:
                newHead = new Coordiate(head.x , head.y + 1);
                break;
        }

        if (newHead.x < 1 || newHead.x < 1 || newHead.x > mXTileCount - 2 || newHead.y > mYTileCount - 2){
            setMode(LOSE);
            return;
        }

        int snakeLength = mSnakeTrail.size();
        for (int snakeIndex = 0; snakeIndex < snakeLength; snakeIndex++){
            Coordiate c= mSnakeTrail.get(snakeIndex);
            if (c.equals(newHead)){
                return;
            }
        }

        int appleCount = mAppleList.size();
        for (int appleIndex = 0 ; appleIndex < appleCount; appleIndex ++){
            Coordiate c = mAppleList.get(appleIndex);
            if (c.equals(newHead)){
                mAppleList.remove(c);
                addRandomApple();

                mScore++;
                mMoveDelay *= 0.9;
                 growSnake = true;
            }
        }

        mSnakeTrail.add(0 , newHead);
         if (!growSnake){
             mSnakeTrail.remove(mSnakeTrail.size() - 1);
         }

        int index = 0;
        for (Coordiate c : mSnakeTrail){
            if (index == 0){
                setTile(YELLOW_STAR , c.x ,c.y);
            }else{
                setTile(RED_STAR , c.x , c.y);
            }

            index++;

        }

    }

    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == RUNNING & oldMode != RUNNING){
            mStatusText.setVisibility(View.INVISIBLE);
            update();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == PAUSE){
            str = res.getText(R.string.mode_pause);
        }
        if (newMode == LOSE){
            str = res.getString(R.string.mode_lose_prefix) + mScore + res.getString(R.string.mode_lose_suffix);
        }
        if (newMode == READY){
            str = res.getText(R.string.mode_ready);
        }

        mStatusText.setText(str);
        mStatusText.setVisibility(View.VISIBLE);
    }

    public void setTextView(TextView newView){
        mStatusText= newView;
    }

    private void updateApples() {
        for (Coordiate c : mAppleList){
            setTile(YELLOW_STAR , c.x , c.y);
        }
    }


    public void resetTiles(int tileCount) {
        mTileArray = new Bitmap[tileCount];
    }

	private class Coordiate{
        public int x;
        public int y;

        public Coordiate(int newX , int newY){
            x = newX;
            y = newY;
        }

        public boolean equals(Coordiate other){
            if (x == other.x && y == other.y){
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Coordinate: [" + x + "," + y + "]";
        }
    }

}
