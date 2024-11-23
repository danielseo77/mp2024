package com.example.fortress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Thread renderThread;
    private boolean running = false;
    private Paint paint;
    private Tank tank;
    private Cannon cannon;
    private Bitmap tankBitmap;
    private Bitmap cannonBitmap;

    private float dx;
    private float dy;

    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        paint = new Paint();
        tank = new Tank(0,0);
        cannon = new Cannon(context, tank);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dx = displayMetrics.widthPixels;  // 화면 너비
        dy = displayMetrics.heightPixels; // 화면 높이

        tankBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tank_image); //이미지 가져오기
        tankBitmap = Bitmap.createScaledBitmap(tankBitmap, tank.TankSizeX, tank.TankSizeY, true);  //size맞추기

        cannonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot);
        cannonBitmap = Bitmap.createScaledBitmap(cannonBitmap, cannon.CannonSizeX, cannon.CannonSizeY, true);  //size맞추기

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
//        isCrushed(canvas);
        int screenHeight = canvas.getHeight();

//        float left = (float) tank.get_TankX() - tankBitmap.getWidth() / 2;
//        float top = (float) tank.get_TankY() - tankBitmap.getHeight() / 2;

        float Tankleft = (float) tank.get_TankX();
        float Tanktop = screenHeight - (float) tank.get_TankY() - tankBitmap.getHeight();


        float Cannonleft = (float) cannon.get_CannonX();
        float Cannontop = screenHeight - (float) cannon.get_CannonY()- cannonBitmap.getHeight();
        //canvas.drawBitmap(tankBitmap, left, top, paint);
        canvas.drawBitmap(tankBitmap, Tankleft, Tanktop, paint);
        canvas.drawBitmap(cannonBitmap, Cannonleft, Cannontop, paint);

    }

    public enum Direction {
        Up(0), Down(1), Right(2), Left(3);
        private final int value;
        private Direction(int value) {this.value = value ; }
        public int value() { return value ; }
        public static Direction stateFromInteger(int value) {
            switch (value) {
                case 0: return Up;
                case 1: return Down;
                case 2: return Right;
                case 3: return Left;
                default: return null;
            }
        }
    }

    public void move(Direction dir) { // 위아래로 움직이면 대포의 각도를 조정하고, 좌우로 움직이면 탱크의 위치를 조정함

        switch (dir) {

            case Up:
                if (cannon.CannonDir != cannon.MaxCannonDir) { //
                    cannon.CannonDir += cannon.CannonMove;
                }
                break;
            case Down:
                if (cannon.CannonDir != cannon.MinCannonDir) {
                    cannon.CannonDir -= cannon.CannonMove;
                }
                break;
            case Right:
                tank.TankX += 10;
                cannon.CannonX += 10;
                if (isCrushed()) {  // 충돌했으면 되돌림
                    tank.TankX -= 10;
                    cannon.CannonX -= 10;
                }
                break;
            case Left:
                tank.TankX -= 10;
                cannon.CannonX -=10;
                if (isCrushed()) {
                    tank.TankX += 10;
                    cannon.CannonX += 10;
                }
                break;
        }
    }

    private boolean isCrushed() {
        if (tank.TankX == 0 || tank.TankX + tank.TankSizeX == dx) {
            return true;
        }
        return false;
    }
//    public void moveTank(Tank.Direction direction) {
//        tank.move(direction);
//        Log.d("Tank", "Updated Tank X: " + tank.get_TankX() + ", Y: " + tank.get_TankY());
//    }
}
