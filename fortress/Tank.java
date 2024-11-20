package com.example.fortress;

public class Tank {

    public static final int TankSizeX = 160; // tank width
    public static final int TankSizeY = 100; // tank height
    public static final int MaxTankhealth = 5 ; // tank health
    public static int Tankhealth = 5 ; // 탱크의 현재 체력;

    private double TankX ; // tank position X -> 탱크 중앙의 X좌표
    private double TankY ; // tank position Y -> 탱크 중앙의 Y좌표

    public Tank(int x, int y) {
        this.TankX = x;
        this.TankY = y;
    }


    public static final int dy = 300; // 게임판 크기 : 300으로 설정 추후 변경
    public static final int dx = 300;

    public double get_TankX(){return TankX ; }
    public double get_TankY(){return TankY ; }

    private double CannonX = TankX+1; // 대포 위치 - 대각선 위에 있도록
    private double CannonY = TankY-1;
    private double CannonDir = Math.PI / 4; // 처음에 45도 각도로 시작
    private final double CannonMove = Math.PI / 45; // 위아래로 움직임에 따라 4도씩 각도가 바뀜
    private final double MaxCannonDir = Math.PI / 2.25; // 최대 각도 : 80도
    private final double MinCannonDir = Math.PI / 6; // 최소 각도 : 30도

    private static boolean Crushed;
    public static boolean takeDamage;

    public static boolean Charging;
    public static boolean fired;

    private static final int MaxCannonGauge = 10; //
    private static final int MinCannonGauge = 0; //
    private static int CannonGauge = MinCannonGauge; // 포탄 게이지를 0~10의 출력으로 충전
    private double CannonPower;
    private double CannonPowerX;
    private double CannonPowerY;
    private final double gravitational_acceleration = 0.09806;


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
                if (CannonDir != MaxCannonDir) { //
                    CannonDir += CannonMove;
                }
                break;
            case Down:
                if (CannonDir != MinCannonDir) {
                    CannonDir -= CannonMove;
                }
                break;
            case Right:
                TankX += 1;
                if (Crushed) {  // 충돌했으면 되돌림
                    TankX -= 1;
                }
                break;
            case Left:
                TankX -= 1;
                if (Crushed) {
                    TankX += 1;
                }
                break;
        }
    }

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

    public boolean isHit() { // main activity에서 수행하도록 옮겨야 할 것 같음
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
