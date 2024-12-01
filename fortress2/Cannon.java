package com.example.fortress;

import android.content.Context;

public class Cannon {

    private Tank tank;

    public static final int CannonSizeX = 30; // cannon width
    public static final int CannonSizeY = 5; // cannon height

    protected double CannonX ; // 대포 위치 - 대각선 위에 있도록
    protected double CannonY ;

    protected double get_CannonX(){return CannonX ; }
    protected double get_CannonY(){return CannonY ; }

    protected float CannonDir = 0; //
    protected float MaxCannonDir = (float) Math.toDegrees(Math.PI);
    protected float MinCannonDir = 0;

    protected final float CannonMove = (float)Math.PI / 5; // 위아래로 움직임에 따라 각도가 바뀜

    public Cannon(Context context, Tank tank) {

        this.tank = tank;

        // 초기 대포 위치 설정
        this.CannonX = tank.get_TankX() + Tank.TankSizeX * 0.50; // 대포 위치
        this.CannonY = tank.get_TankY() + Tank.TankSizeY * 0.92 - CannonSizeY;

    }


}
