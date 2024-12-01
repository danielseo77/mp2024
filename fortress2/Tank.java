package com.example.fortress;

public class Tank {

    public static final int TankSizeX = 80; // tank width
    public static int TankSizeY = 50; // tank height

    protected int Tankhealth = 5 ; // 탱크의 현재 체력;

    public double TankX ; // tank position X -> 탱크 중앙의 X좌표
    public double TankY ; // tank position Y -> 탱크 중앙의 Y좌표

    public Tank(int x, int y) {
        this.TankX = x;
        this.TankY = y;
    }

    public void set_TankX(double x) {this.TankX = x; }
    public void set_TankY(double y) {this.TankY = y; }
    public double get_TankX(){return TankX ; }
    public double get_TankY(){return TankY ; }

    public static boolean Crushed = false;
    public static boolean takeDamage;









}
