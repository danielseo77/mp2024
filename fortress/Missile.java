package com.example.fortress;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class Missile {

    private Cannon cannon;
//    private Tank tank;

    private static int dy ;
    private static int dx ;

    public static final int MissileSizeX = 7; // tank width 15
    public static final int MissileSizeY = 7; // tank height

    public double MissileX ; // 대포 위치 - 대각선 위에 있도록
    public double MissileY ;

    public double get_MissileX(){return MissileX ; }
    public double get_MissileY(){return MissileY ; }

    public Missile(Context context, Cannon cannon) {

        this.cannon = cannon;
//        tank = new Tank();
//        cannon= new Cannon(context);
        // 화면 크기를 가져옴
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dx = displayMetrics.widthPixels;  // 화면 너비
        dy = displayMetrics.heightPixels; // 화면 높이

        // 초기 대포 위치 설정
        this.MissileX = cannon.get_CannonX() + cannon.CannonSizeX; // 대포 위치
        this.MissileY = cannon.get_CannonY(); // 대포 위치

    }

    private static final int MaxMissileGauge = 6; //
    private static final int MinMissileGauge = 0; //
    private static int MissileGauge = MinMissileGauge; // 포탄 게이지를 0~10의 출력으로 충전
    private float MissilePower;
    private float MissilePowerX;
    private float MissilePowerY;
    private final double gravitational_acceleration = 0.09806;

    public static boolean Charging=false;
    public static boolean fired = false;

    public void setPower() { // 내부 인자를 onclick함수-> 충전 버튼으로 게이지를 채우도록
        if (Charging && !fired ) {
            if (MissileGauge != MaxMissileGauge) {
                MissileGauge += 1;
            }
//            else {
//                MissileGauge = MinMissileGauge; // 임시 코드임 : 게이지를 다 채워도 누르고 있으면 다시 줄어들도록?
//            }
        }
    }

    public void FireCannon() { // 위와 같이 인자를 충전 버튼을 때면 작동하도록
        if (!Charging && fired) {
            double angle = Math.toRadians(cannon.CannonDir);
//            double angle = (cannon.CannonDir);
            Log.d("as","angle = " + angle);
            MissilePower =(float) (MissileGauge * 0.5 * (Math.PI) ) ;
//            MissilePower =(float) ((MissileGauge * 0.4 + 1 )* Math.PI * 1.5) ;
//            double power = (MissileGauge * 0.5) + 5; // 초기 발사 속도
            MissilePowerX = (float) (MissilePower * Math.cos(angle));
            MissilePowerY = (float) (MissilePower * Math.sin(angle));

            fired = true;

        }
//        MissileGauge = MinMissileGauge; // 게이지 초기화
    }

    public void updatePosition(Tank tank) {
        if (fired) {
            MissilePowerY -= 1.2 * gravitational_acceleration; // 중력 가속도 적용
            MissileX += MissilePowerX; // x축 속도에 따른 이동
            MissileY -= MissilePowerY; // y축 속도에 따른 이동

            // 충돌 체크
            if (isHit(tank)) {
                Log.d("GameSurfaceView", "Missile hit the dummy tank!");
                fired = false; // 충돌 시 미사일 비활성화
                resetPosition();
            } else if (MissileX < 0 || MissileX > dx || MissileY > dy || MissileY < 0) {
                // 화면 밖으로 나가면 미사일 발사를 중단
                fired = false;
                resetPosition();
            }
        }
    }


    private void resetPosition() {
        // 초기 위치로 되돌리기
        MissileX = cannon.get_CannonX();
        MissileY = cannon.get_CannonY();
    }

    public boolean isHit(Tank tank) {
        // Y 좌표 변환 적용
        double missileLeft = MissileX;
        double missileRight = MissileX + MissileSizeX;
        double missileTop = MissileY;
        double missileBottom = MissileY - MissileSizeY;

        double tankLeft = tank.get_TankX();
        double tankRight = tank.get_TankX() + tank.TankSizeX;
        double tankTop = tank.get_TankY();
        double tankBottom = tank.get_TankY()- tank.TankSizeY;


        // 정확한 충돌 조건
        boolean collisionX = missileLeft < tankRight && missileRight > tankLeft;
        boolean collisionY = missileTop < tankBottom && missileBottom > tankTop;

        return collisionX && collisionY;
    }


}
