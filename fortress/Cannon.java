package com.example.fortress;

import android.content.Context;
import android.util.DisplayMetrics;

public class Cannon {

    private Tank tank;

    private static int dy ;
    private static int dx ;

    public static final int CannonSizeX = 30; // cannon width
    public static final int CannonSizeY = 5; // cannon height

    public double CannonX ; // 대포 위치 - 대각선 위에 있도록
    public double CannonY ;

    public double get_CannonX(){return CannonX ; }
    public double get_CannonY(){return CannonY ; }

    protected float CannonDir = 0; //
    protected float MaxCannonDir = (float) Math.toDegrees(Math.PI);
    protected float MinCannonDir = 0;

    protected final float CannonMove = (float)Math.PI / 5; // 위아래로 움직임에 따라 각도가 바뀜

    public Cannon(Context context, Tank tank) {

        this.tank = tank;
        // 화면 크기를 가져옴
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        dx = displayMetrics.widthPixels;  // 화면 너비
        dy = displayMetrics.heightPixels; // 화면 높이

        // 초기 대포 위치 설정
        this.CannonX = tank.get_TankX() + tank.TankSizeX * 0.40; // 대포 위치
        this.CannonY = tank.get_TankY() + tank.TankSizeY * 0.92 - CannonSizeY;

    }


}
