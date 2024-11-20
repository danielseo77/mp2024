package com.example.fortress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Thread renderThread;
    private boolean running = false;
    private Paint paint;
    private Tank tank;
    private Bitmap tankBitmap;


    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        paint = new Paint();
        tank = new Tank(0, 0);  

        tankBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tank_image); //이미지 가져오기
        tankBitmap = Bitmap.createScaledBitmap(tankBitmap, tank.TankSizeX, tank.TankSizeY, true);  //size맞추기


        // SurfaceView의 기본 배경 제거
        this.setBackgroundColor(Color.TRANSPARENT);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("GameSurfaceView", "surfaceCreated call");
        running = true;
        renderThread = new Thread(this);
        renderThread.start();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        try {
            renderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() { // 계속 draw함
        while (running) {
            Canvas canvas = null;
            try {
                canvas = getHolder().lockCanvas();
                if (canvas != null) {
                    drawGame(canvas);
                }
            } finally {
                if (canvas != null) {
                    getHolder().unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(16); // 약 60FPS로 갱신
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void drawGame(Canvas canvas) {
        canvas.drawColor(Color.WHITE); // 배경 색상

        //
        float left = (float) tank.get_TankX() - tankBitmap.getWidth() / 2;
        float top = (float) tank.get_TankY() - tankBitmap.getHeight() / 2;

        float left2 = (float) tank.get_TankX();
        float top2 = (float) tank.get_TankY();
        //canvas.drawBitmap(tankBitmap, left, top, paint);
        canvas.drawBitmap(tankBitmap, left2, top2, paint);

    }

    public void moveTank(Tank.Direction direction) {
        tank.move(direction);
        Log.d("Tank", "Updated Tank X: " + tank.get_TankX() + ", Y: " + tank.get_TankY());
    }
}
