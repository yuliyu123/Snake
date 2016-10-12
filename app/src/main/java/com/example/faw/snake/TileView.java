package com.example.faw.snake;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by faw on 16/10/4.
 */

public class TileView extends View {

    protected static int mTileSize;

    protected static int mXTileCount;
    protected static int mYTileCount;


    private static int mXOffset;
    private static int mYOffset;

    private final Paint mPaint = new Paint();

    private Bitmap[] mTileArray;

    private int[][] mTileGrid;

    public TileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.TileView);

        mTileSize = a.getInt(R.styleable.TileView_tileSize,12);

        a.recycle();
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs , R.styleable.TileView);

        mTileSize = a.getInt(R.styleable.TileView_tileSize,12);

        a.recycle();
    }



    public void resetTiles(int tileCount){

        mTileArray = new Bitmap[tileCount];

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mXTileCount = (int) Math.floor(w / mTileSize);
        mYTileCount = (int) Math.floor(w / mTileSize);

        mXOffset = ((w - (mTileSize * mXTileCount)) / 2);
        mYOffset = ((h - (mTileSize * mYTileCount)) / 2);

        mTileGrid = new int[mXTileCount][mYTileCount];
        clearTiles();

    }

    public void loadTile(int key , Drawable tile){
        Bitmap bitmap = Bitmap.createBitmap(mTileSize , mTileSize , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0 , 0 , mTileSize , mTileSize);
        tile.draw(canvas);

        mTileArray[key] = bitmap;
    }

    public void clearTiles(){
        for (int x = 0; x < mXTileCount ; x++){
            for (int y = 0; y < mYTileCount ; y++){
                setTile(0,x,y);
            }
        }
    }

    protected void setTile(int tileIndex , int x, int y) {
        mTileGrid[x][y] = tileIndex;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x = 0; x < mXTileCount ; x+=1){
            for (int y = 0; y < mYTileCount ; y+=1){
                if (mTileGrid[x][y] > 0){
                    canvas.drawBitmap(mTileArray[mTileGrid[x][y]],
                    mXOffset + x* mTileSize,
                    mYOffset + y* mTileSize,
                    mPaint);
                }
            }
        }
    }
}
