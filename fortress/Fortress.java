package com.example.fortress;

public class Fortress {
    public enum FortressState {
        Running(0), Paused(1), Finished(2); // 0 : my turn, 1 : opponent's turn, 2 : game over
        private final int value;
        private FortressState(int value) {this.value = value ; }
        public int value() { return value ; }
    }

    private Tank[][] TankPosit = new Tank[Tank.dy][Tank.dx]; // 이 방식이 아니라 다른 방식으로 소수점까지 표현 가능하도록 구현해야할듯
}
