package com.example.fortress;

public class Tank {

    private Cannon cannon;

    public final int TankSizeX = 160; // tank width
    public final int TankSizeY = 100; // tank height
    public static final int MaxTankhealth = 5 ; // tank health
    public static int Tankhealth = 5 ; // 탱크의 현재 체력;

    public double TankX ; // tank position X -> 탱크 중앙의 X좌표
    public double TankY ; // tank position Y -> 탱크 중앙의 Y좌표

    public Tank(int x, int y) {
        this.TankX = x;
        this.TankY = y;
    }

    public double get_TankX(){return TankX ; }
    public double get_TankY(){return TankY ; }

    public static boolean Crushed = false;
    public static boolean takeDamage;









}
