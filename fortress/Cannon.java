package com.example.fortress;

import android.content.Context;
import android.util.DisplayMetrics;

public class Cannon {

    private Tank tank;

    public static Context context;

    private static int dy ;
    private static int dx ;

    public static final int CannonSizeX = 30; // tank width
    public static final int CannonSizeY = 30; // tank height

    public double CannonX ; // 대포 위치 - 대각선 위에 있도록
    public double CannonY ;

    public double get_CannonX(){return CannonX ; }
    public double get_CannonY(){return CannonY ; }

    protected double CannonDir = Math.PI / 4; // 처음에 45도 각도로 시작
    protected final double CannonMove = Math.PI / 45; // 위아래로 움직임에 따라 4도씩 각도가 바뀜
    protected final double MaxCannonDir = Math.PI / 2.25; // 최대 각도 : 80도
    protected final double MinCannonDir = Math.PI / 6; // 최소 각도 : 30도

    public Cannon(Context context, Tank tank) {

        // 화면 크기를 가져옴
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dx = displayMetrics.widthPixels;  // 화면 너비
        dy = displayMetrics.heightPixels; // 화면 높이

        // 초기 대포 위치 설정
        this.CannonX = tank.get_TankX() + tank.TankSizeX - CannonSizeX; // 대포 위치
        this.CannonY = tank.get_TankY() + tank.TankSizeY - CannonSizeY;

    }

    private static final int MaxCannonGauge = 10; //
    private static final int MinCannonGauge = 0; //
    private static int CannonGauge = MinCannonGauge; // 포탄 게이지를 0~10의 출력으로 충전
    private double CannonPower;
    private double CannonPowerX;
    private double CannonPowerY;
    private final double gravitational_acceleration = 0.09806;

    public static boolean Charging;
    public static boolean fired;

    public void setPower() { // 내부 인자를 onclick함수-> 충전 버튼으로 게이지를 채우도록
        if (Charging && !fired ) {
            if (CannonGauge != MaxCannonGauge) {
                CannonGauge += 1;
            }
            else {
                CannonGauge = MinCannonGauge; // 임시 코드임 : 게이지를 다 채워도 누르고 있으면 다시 줄어들도록?
            }
        }
        CannonPower = (CannonGauge * 0.05 + 1 )* Math.PI * 1.5 ;
        CannonPowerX = CannonPower * Math.cos(CannonDir);
        CannonPowerY = CannonPower * Math.sin(CannonDir);
    }

    public void FireCannon() { // 위와 같이 인자를 충전 버튼을 때면 작동하도록
        if (!Charging && fired) {
            CannonPowerY -= gravitational_acceleration;
            CannonX = CannonX + CannonPowerX;
            CannonY = CannonY - CannonPowerY;
        }
    }

    public boolean isHit() { 
        if (!Charging && !fired) { // 적중하지 않았을 때: 상대 편에게 닿지 않고 게임판 모서리에 닿음
            if (CannonX <= 0 || CannonX >= dx || CannonY >= dy ) {
                return false;
            }
            else { // 적중했을 때 : 상대 편 모서리에 닿으면 조건 추가 -
                // 상대편의 takeDamage = true 로직 추가
                return true;
            }
        }
        return false; // 이외의 상황(이 일어난다면) 맞지 않은 것으로 간주
    }
}
