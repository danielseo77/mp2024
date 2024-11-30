package com.example.fortress;

import static com.example.fortress.Missile.Charging;
import static com.example.fortress.Missile.fired;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private Thread renderThread;
    private boolean running = false;
    private Paint paint;

    public Tank tank;
    public Cannon cannon;
    public Missile missile;

    private Bitmap tankBitmap;
    private Bitmap cannonBitmap;
    private Bitmap missileBitmap;
    private Bitmap dummyBitmap;
    private Bitmap terrainBitmap;

    private Tank Dummy;

    private float dx;
    private float dy;

    private int width;
    private int height;

    public static int y = 0;
    public static int x = 100;


    public GameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        paint = new Paint();
        tank = new Tank(x + 100, y);
        cannon = new Cannon(context, tank);
        missile = new Missile(context, cannon);


        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dx = displayMetrics.widthPixels;  // 화면 너비
        dy = displayMetrics.heightPixels; // 화면 높이

        Dummy = new Tank((int)dx - tank.TankSizeX - 100, 0);


        tankBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tank_without_cannon_left); //이미지 가져오기
        tankBitmap = Bitmap.createScaledBitmap(tankBitmap, tank.TankSizeX, tank.TankSizeY, true);  //size맞추기

        dummyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tank_without_cannon_right); //이미지 가져오기
        dummyBitmap = Bitmap.createScaledBitmap(dummyBitmap, tank.TankSizeX, tank.TankSizeY, true);  //size맞추기

        cannonBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cannon_left);
        cannonBitmap = Bitmap.createScaledBitmap(cannonBitmap, cannon.CannonSizeX, cannon.CannonSizeY, true);  //size맞추기

        missileBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot);
        missileBitmap = Bitmap.createScaledBitmap(missileBitmap, missile.MissileSizeX, missile.MissileSizeY, true);

        // SurfaceView의 기본 배경 제거
        this.setBackgroundColor(Color.TRANSPARENT);

        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            width = getWidth();
            height = getHeight();

            Log.d("GameSurfaceView", "Width: " + width + ", Height: " + height);

            terrainBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.terrain_image);
            terrainBitmap = Bitmap.createScaledBitmap(terrainBitmap, width, height, true);
        });


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
//        Log.d("fired?" ,"cannonDir? " + cannon.CannonDir);
//        Log.d("fired?" ,"cannonDir? " + cannon.MaxCannonDir);

        //canvas.drawColor(Color.WHITE); // 배경 색상

        int screenHeight = canvas.getHeight();
        int screenWidth = canvas.getWidth();

        float Tankleft = (float) tank.get_TankX();
        //float Tanktop = screenHeight - (float) tank.get_TankY() - tankBitmap.getHeight();
        float Tankbottom = getTerrainTopY((int) Tankleft);
        tank.TankY = Tankbottom - tank.TankSizeY;
        //float top = getTerrainTopY((int) Tankleft);

//        float Dummyleft = (float) screenWidth - Dummy.TankSizeX;
        float Dummyleft = (float) Dummy.get_TankX();
        //float Dummytop = screenHeight - (float) Dummy.get_TankY() - dummyBitmap.getHeight();
        float Dummybottom = getTerrainTopY((int) Dummyleft);
        Dummy.TankY = Dummybottom - tank.TankSizeY;

        float Cannonleft = (float) cannon.get_CannonX() + 6;
        float Cannontop = (float) (tank.get_TankY() + 4);

        float Missileleft = (float) missile.get_MissileX();
        float Missiletop = screenHeight - (float) missile.get_MissileY()- missileBitmap.getHeight();

        //canvas.drawBitmap(tankBitmap, left, top, paint);

        canvas.drawBitmap(terrainBitmap, 0, 0, paint);

        canvas.drawBitmap(tankBitmap, Tankleft, (float) tank.TankY, paint);

        canvas.drawBitmap(dummyBitmap, Dummyleft, (float) Dummy.TankY, paint);



        // 대포의 변환 행렬
        Matrix cannonMatrix = new Matrix();
        cannonMatrix.postScale(1, -1);
        cannonMatrix.postTranslate(0, cannonBitmap.getHeight());
        cannonMatrix.postRotate(-(float) cannon.CannonDir, 0, 0);
        cannonMatrix.postTranslate(Cannonleft, Cannontop);
        canvas.drawBitmap(cannonBitmap, cannonMatrix, paint);


        missile.updatePosition(Dummy); // 미사일 위치 업데이트

        Matrix missileMatrix = new Matrix();
        missileMatrix.postScale(1, -1);
        if (!fired) {
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
                cannon.CannonDir += cannon.CannonMove;
                if (cannon.CannonDir >= cannon.MaxCannonDir) {
                    cannon.CannonDir -= cannon.CannonMove;
                }
                break;
            case Down:
                cannon.CannonDir -= cannon.CannonMove;
                if (cannon.CannonDir <= cannon.MinCannonDir) {
                    cannon.CannonDir += cannon.CannonMove;
                }
                break;
            case Right:
                tank.TankX += 5;
                cannon.CannonX += 5;
                missile.MissileX += 5;
                if (isCrushed()) {  // 충돌했으면 되돌림
                    tank.TankX -= 5;
                    cannon.CannonX -= 5;
                    missile.MissileY -= 5;
                }
                break;
            case Left:
                tank.TankX -= 5;
                cannon.CannonX -=5;
                missile.MissileX -= 5;
                if (isCrushed()) {
                    tank.TankX += 5;
                    cannon.CannonX += 5;
                    missile.MissileX += 5;
                }
                break;
        }
    }

    public void setFire() {
        Charging = true;
        missile.setPower();
    }

    public boolean FireMissile() {
        Charging = false;
        fired = true;
        missile.FireCannon();
//        Log.d("Charging", "Charging = " + Charging);
//        Log.d("fired", "fired = " + fired);
//        if (missile.isHit(Dummy)){
//            tank.Tankhealth -= 1;
//            Log.d("asdf","isHit?" + missile.isHit(Dummy));
//
//        }
//        else {
//            Log.d("asdf","isHit?" + missile.isHit(Dummy));
//        }
//        fired = false;
        return true;
    }

    private boolean isCrushed() {
        if (tank.TankX == 0 || tank.TankX + tank.TankSizeX == dx) {
            return true;
        }
        return false;
    }

    private int getTerrainTopY(int x) {
        // 위에서 아래로 스캔
        for (int y = 0; y < terrainBitmap.getHeight(); y++) {
            int pixelColor = terrainBitmap.getPixel(x, y);

            // 검은색 픽셀 확인
            if ((pixelColor & 0xFFFFFF) == 0x000000) {
                return y; // 검은색 픽셀 Y 좌표 반환
            }
        }

        return -1; // 검은색이 없는 경우
    }
}
