package com.example.fortress;

//import static com.example.fortress.Missile.fired;

import static com.example.fortress.Activity_game.players;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public boolean pause = false;

    private Thread renderThread;
    private boolean running = false;
    private Paint paint;

    public Tank tank;
    public Cannon cannon;
    public Missile missile;

    public Tank Dummy;
    public Cannon dummyCannon;
    public Missile dummyMissile;

    public static Missile Turn;

    private Bitmap tankBitmap;
    private Bitmap cannonBitmap;
    private Bitmap missileBitmap;

    private Bitmap dummyBitmap;
    private Bitmap dummyCannonBitmap;
    private Bitmap dummyMissileBitmap;


    private float dx;
    private float dy;

    public static int y = 0;
    public static int x = 0;


    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        paint = new Paint();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dx = displayMetrics.widthPixels;  // 화면 너비
        dy = displayMetrics.heightPixels; // 화면 높이

        // tank 정의
        tank = new Tank(x, y);
        cannon = new Cannon(context, tank);
        missile = new Missile(context, tank, cannon);

        // dummy tank 정의
        Dummy = new Tank((int)dx - tank.TankSizeX, 0);
        dummyCannon = new Cannon(context, Dummy);
        dummyMissile = new Missile(context, Dummy, dummyCannon);

        // tank bitmap 생성
        tankBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tank_without_cannon_left); //이미지 가져오기
        tankBitmap = Bitmap.createScaledBitmap(tankBitmap, tank.TankSizeX, tank.TankSizeY, true);  //size맞추기

        cannonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cannon_left);
        cannonBitmap = Bitmap.createScaledBitmap(cannonBitmap, cannon.CannonSizeX, cannon.CannonSizeY, true);

        missileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot);
        missileBitmap = Bitmap.createScaledBitmap(missileBitmap, missile.MissileSizeX, missile.MissileSizeY, true);

        // dummy tank bitmap 생성
        dummyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tank_without_cannon_right);
        dummyBitmap = Bitmap.createScaledBitmap(dummyBitmap, tank.TankSizeX, tank.TankSizeY, true);

        dummyCannonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cannon_right);
        dummyCannonBitmap = Bitmap.createScaledBitmap(dummyCannonBitmap, dummyCannon.CannonSizeX, dummyCannon.CannonSizeY, true);

        dummyMissileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot);
        dummyMissileBitmap = Bitmap.createScaledBitmap(dummyMissileBitmap, missile.MissileSizeX, missile.MissileSizeY, true);

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

        if (players == Activity_game.PlayerNum.Player1) { // player가 누구인지에 따라 제어하는 탱크가 다르도록
            Turn = missile;
        }
        else if (players == Activity_game.PlayerNum.Player2){
            Turn = dummyMissile;
        }

        canvas.drawColor(Color.WHITE); // 배경 색상

        int screenHeight = canvas.getHeight();
        int screenWidth = canvas.getWidth();

        // tank, cannon drawing
        float Tankleft = (float) tank.get_TankX() ;
        float Tanktop = screenHeight - (float) tank.get_TankY() - tankBitmap.getHeight();

        float Cannonleft = (float) cannon.get_CannonX();
        float Cannontop = screenHeight - (float) cannon.get_CannonY()- cannonBitmap.getHeight();

//        float Missileleft = (float) missile.get_MissileX(); // 기본 미사일 위치
//        float Missiletop = screenHeight - (float) missile.get_MissileY()- missileBitmap.getHeight();

        // dummy tank, cannon drawing
        float Dummyleft = (float) Dummy.get_TankX();
        float Dummytop = screenHeight - (float) Dummy.get_TankY() - dummyBitmap.getHeight();

        float dummyCannonleft = (float) dummyCannon.get_CannonX() ;
        float dummyCannontop = screenHeight - (float) dummyCannon.get_CannonY() - dummyCannonBitmap.getHeight();

        //canvas.drawBitmap(tankBitmap, left, top, paint);
        canvas.drawBitmap(tankBitmap, Tankleft, Tanktop, paint);

        canvas.drawBitmap(dummyBitmap, Dummyleft, Dummytop, paint);

        // cannon의 변환 행렬
        Matrix cannonMatrix = new Matrix();
        cannonMatrix.postScale(1, -1);
        cannonMatrix.postTranslate(0, cannonBitmap.getHeight());
        cannonMatrix.postRotate(-(float) cannon.CannonDir, 0, 0);
        cannonMatrix.postTranslate(Cannonleft, Cannontop);
        canvas.drawBitmap(cannonBitmap, cannonMatrix, paint);

        //dummy cannon의 변환행렬
        Matrix dummyCannonMatrix = new Matrix();
        dummyCannonMatrix.postScale(-1,-1);
        dummyCannonMatrix.postTranslate(0, dummyCannonBitmap.getHeight());
        dummyCannonMatrix.postRotate((float) dummyCannon.CannonDir, 0, 0);
        dummyCannonMatrix.postTranslate(dummyCannonleft, dummyCannontop);
        canvas.drawBitmap(dummyCannonBitmap, dummyCannonMatrix, paint);

        // missile의 변환행렬
        if (!pause) {
            missile.updatePosition(Dummy); // 미사일 위치 업데이트

        }

        Matrix missileMatrix = new Matrix();
        missileMatrix.postScale(1, -1);
        if (!missile.fired) {
            missile.MissileX = (float) ((Cannonleft + Math.cos(Math.toRadians(cannon.CannonDir)) * cannon.CannonSizeX));
            missile.MissileY = (float) ((Cannontop - Math.sin(Math.toRadians(cannon.CannonDir)) * cannon.CannonSizeX));
            missileMatrix.postTranslate(0, missileBitmap.getHeight());
            missileMatrix.postRotate(-(float) cannon.CannonDir, 0, 0);
            missileMatrix.postTranslate((float) missile.MissileX, (float) missile.MissileY);
            canvas.drawBitmap(missileBitmap, missileMatrix, paint);
        }
        else {
            missileMatrix.postTranslate((float) missile.get_MissileX(), (float) missile.get_MissileY());
            canvas.drawBitmap(missileBitmap, missileMatrix, paint);
        }

        // dummy missile의 변환행렬
        if (!pause) {
            dummyMissile.updateReversePosition(tank); // 더미 미사일 위치 업데이트

        }

        Matrix dummyMissileMatrix = new Matrix();
        dummyMissileMatrix.postScale(-1, -1);
        if (!dummyMissile.fired) {
            dummyMissile.MissileX = (float) ((dummyCannonleft - Math.cos(Math.toRadians(dummyCannon.CannonDir)) * dummyCannon.CannonSizeX));
            dummyMissile.MissileY = (float) ((dummyCannontop - Math.sin(Math.toRadians(dummyCannon.CannonDir)) * dummyCannon.CannonSizeX));
            dummyMissileMatrix.postTranslate(0, dummyMissileBitmap.getHeight());
            dummyMissileMatrix.postRotate(-(float) dummyCannon.CannonDir, 0, 0);
            dummyMissileMatrix.postTranslate((float) dummyMissile.MissileX, (float) dummyMissile.MissileY);
            canvas.drawBitmap(dummyMissileBitmap, dummyMissileMatrix, paint);
        }
        else {
            dummyMissileMatrix.postTranslate((float) dummyMissile.get_MissileX(), (float) dummyMissile.get_MissileY());
            canvas.drawBitmap(dummyMissileBitmap, dummyMissileMatrix, paint);
        }


    }

    public void move(Missile.Direction dir) { // 위아래로 움직이면 대포의 각도를 조정하고, 좌우로 움직이면 탱크의 위치를 조정함

        switch (dir) {

            case Up:
                Turn.move(dir.Up);
                break;
            case Down:
                Turn.move(dir.Down);
                break;
            case Right:
                Turn.move(dir.Right);
                break;
            case Left:
                Turn.move(dir.Left);
                break;
        }
    }

    public void setFire() {
        Turn.setFire();
    }

    public boolean FireMissile() {
        return Turn.FireMissile();
    }

}
