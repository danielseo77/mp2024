package com.example.fortress;

import static com.example.fortress.Activity_game.ChangePlayer;
import static com.example.fortress.Activity_game.players;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class Missile {

    private Tank tank;
    private Cannon cannon;

    private static int dy ;
    private static int dx ;

    private int check;

    public static final int MissileSizeX = 7; // tank width 15
    public static final int MissileSizeY = 7; // tank height

    protected double MissileX ; // 대포 위치 - 대각선 위에 있도록
    protected double MissileY ;

    protected double get_MissileX(){return MissileX ; }
    protected double get_MissileY(){return MissileY ; }

    public Missile(Context context, Tank tank, Cannon cannon) {

        this.tank = tank;
        this.cannon = cannon;

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

    public boolean Charging=false;
    public boolean fired = false;
    public static boolean onfire = false;

    public void setPower() { // 충전 버튼을 길게 누를수록 빠르게 나가도록
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
            MissilePower =(float) (MissileGauge * 0.5 * (Math.PI) ) ;
            MissilePowerX = (float) (MissilePower * Math.cos(angle));
            MissilePowerY = (float) (MissilePower * Math.sin(angle));

            fired = true;

        }
        MissileGauge = MinMissileGauge; // 게이지 초기화
    }

    public int updatePosition(int PlayerNum, Tank tank, Terrain terrain) {
        if (fired) {
            MissilePowerY -= (float) (1.2 * gravitational_acceleration); // 중력 가속도 적용
            MissileY -= MissilePowerY; // y축 속도에 따른 이동

            if (PlayerNum == Activity_game.PlayerNum.Player1.value()) {
                MissileX += MissilePowerX; // x축 속도에 따른 이동
            }
            else if (PlayerNum == Activity_game.PlayerNum.Player2.value()) {
                MissileX -= MissilePowerX; // x축 속도에 따른 이동
            } else { Log.d("Missile.java", "PlayerNUm not exist"); return -1; }

            // 충돌 체크
            if (isHit(tank)) {
                Log.d("GameSurfaceView", "Missile hit the dummy tank!");
                fired = false; // 충돌 시 미사일 비활성화
                resetPosition();
                tank.Tankhealth--;
                return 1;
            } else if (MissileX < 0 || MissileX > dx || MissileY < 0 || terrain.getY((int) MissileX, (int) MissileY)) {
                // 화면 밖으로 나가면 미사일 발사를 중단
                fired = false;
                resetPosition();
                Log.d("Missile", "position : " + MissileX + " " + MissileY);
            }
        }
        return 0;
    }

    private void resetPosition() {
        // 초기 위치로 되돌리기
        MissileX = cannon.get_CannonX();
        MissileY = cannon.get_CannonY();
        onfire = false;
        players = ChangePlayer(players);
    }

    public boolean isHit(Tank tank) {
        // Y 좌표 변환 적용
        double missilex = MissileX + (double) MissileSizeX / 2;
        double missiley = MissileY + (double) MissileSizeY / 2;

        double tankLeft = tank.get_TankX();
        double tankRight = tank.get_TankX() + Tank.TankSizeX;
        double tankTop = tank.get_TankY();
        double tankBottom = tank.get_TankY() + Tank.TankSizeY;

        boolean hitx = missilex > tankLeft && missilex < tankRight;
        boolean hity = missiley > tankTop && missiley < tankBottom;
        return (hitx && hity );
    }

    public enum Direction {
        Error(-1), Up(0), Down(1), Right(2), Left(3);
        private final int value;
        private Direction(int value) {this.value = value ; }
    }

    public void move(Direction dir, Terrain terrain) { // 위아래로 움직이면 대포의 각도를 조정하고, 좌우로 움직이면 탱크의 위치를 조정함

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
                MissileX += 5;
                check = terrain.getTerrainSlope((int) tank.TankX + Tank.TankSizeX / 2, (int) tank.TankY);
                if (check == -1 || isCrushed()) {  // 경사가 높거나 충돌할 경우
                    tank.TankX -= 5;
                    cannon.CannonX -= 5;
                    MissileX -= 5;
                } else { // 이동
                    tank.TankY = check - Tank.TankSizeY;
                    cannon.CannonY = tank.TankY + 5;
                }
                break;
            case Left:
                tank.TankX -= 5;
                cannon.CannonX -=5;
                MissileX -= 5;
                check = terrain.getTerrainSlope((int) tank.TankX + Tank.TankSizeX / 2, (int) tank.TankY);
                if (check == -1 || isCrushed()) {
                    tank.TankX += 5;
                    cannon.CannonX += 5;
                    MissileX += 5;
                } else {
                    tank.TankY = check - Tank.TankSizeY;
                    cannon.CannonY = tank.TankY + 5;
                }
                break;
        }

        //Log.d("GameSurfaceView", "Tankleft : " + tank.TankX + " Tanktop : " + tank.TankY);

    }
    public void setFire() {
        Charging = true;
        setPower();
    }

    public boolean FireMissile() {
        Charging = false;
        fired = true;
        FireCannon();
        return true;
    }

    public boolean isCrushed() {
        return tank.TankX < 0 || (tank.TankX + Tank.TankSizeX > dx);
    }

}
