package com.example.fortress;

import static com.example.fortress.Missile.onfire;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity_game extends AppCompatActivity {

    Button btnLeft;
    Button btnRight;
    Button btnUp;
    Button btnDown;
    Button btnFire;
    Button btnPause;
    Button btnQuit;

    private GameSurfaceView gameSurface;

    private GameState gameState = GameState.Running;
    private GameState savedState;
    private enum GameState {
        Error(-1), Running(0), Paused(1) ;
        private final int value;
        private GameState(int value) { this.value = value; }
        public int value() { return value; }
        public static GameState stateFromInteger(int value) {
            switch (value) {
                case -1: return Error;
                case 0: return Running;
                case 1: return Paused;
                default: return null;
            }
        }
    }

    private enum GameCommand {
        NOP(-1), Moving(0), Fired(1), Pause(2);
        private final int value;
        private GameCommand(int value) { this.value = value; }
        public int value() { return value; }
    }
    int stateTransMatrix[][] = { // stateTransMatrix[currGameState][GameCommand] --> nextGameState
            { 0, 0, 1 },    // [Running][Moving] --> Running,
            // [Running][Fired] --> Running(opponent's turn),
            // [Running][Pause] --> Paused,
            { -1, -1, 0 },     // [Paused][Paused] --> Running,
    };

    private static boolean singlePlayer;
    public static PlayerNum players;

    public enum PlayerNum {
        Player1(0), Player2(1);
        private final int value;
        private PlayerNum(int value) { this.value = value; }
    }

    public static PlayerNum ChangePlayer(PlayerNum playerNumber) { // 미사일을 쏘고 나면 플레이어의 차례가 바뀌도록 함
        switch (playerNumber) {
            case Player1 :
                if (!singlePlayer) {
                    return PlayerNum.Player2;
                }
                else {
                    return PlayerNum.Player1;
                }
            case Player2 :
                return PlayerNum.Player1;
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra("playerNumber", true)) {singlePlayer = true;} // echo mode인지 확인함
            players = PlayerNum.Player1;
        }

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game);

        gameSurface = findViewById(R.id.game_surface);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLeft = findViewById(R.id.btn_left);
        btnRight = findViewById(R.id.btn_right);
        btnUp = findViewById(R.id.btn_up);
        btnDown = findViewById(R.id.btn_down);
        btnFire = findViewById(R.id.btn_fire);
        btnPause = findViewById(R.id.btn_pause);
        btnQuit = findViewById(R.id.btn_quit);

        btnLeft.setOnTouchListener(createMoveListener);
        btnRight.setOnTouchListener(createMoveListener);
        btnUp.setOnTouchListener(createMoveListener);
        btnDown.setOnTouchListener(createMoveListener);
        btnFire.setOnTouchListener(createMoveListener);
        btnPause.setOnTouchListener(createMoveListener);

        btnQuit.setOnClickListener(v->finish());
    }


    private View.OnTouchListener createMoveListener = new View.OnTouchListener() {
        private Missile.Direction direction;

        // 핸들러와 Runnable 생성
        private Handler handler = new Handler();

        private Runnable moveRunnable = new Runnable() {
            @Override
            public void run() {
                if (direction != null) {
                    gameSurface.move(direction);
                    handler.postDelayed(this, 10); // 10ms마다 반복
                }
            }
        };
        private Runnable setfireRunnable = new Runnable() {
            @Override
            public void run() {
                gameSurface.setFire(); // 미사일 발사 준비 상태
                handler.postDelayed(this, 100); // 10ms마다 반복
            }
        };

        private Runnable fireRunnable = new Runnable() {
            @Override
            public void run() {
                gameSurface.FireMissile(); // 실제 미사일 발사
                onfire = true;
                handler.removeCallbacks(this);
            }
        };
        private Runnable pauseRunnable = new Runnable() {
            @Override
            public void run() {
//                finish();
                btnLeft.setEnabled(false);
                btnRight.setEnabled(false);
                btnUp.setEnabled(false);
                btnDown.setEnabled(false);
                btnFire.setEnabled(false);
                gameSurface.pause = true;
            }
        };
        private Runnable resumeRunnable = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(pauseRunnable);
                btnLeft.setEnabled(true);
                btnRight.setEnabled(true);
                btnUp.setEnabled(true);
                btnDown.setEnabled(true);
                btnFire.setEnabled(true);
                gameSurface.pause = false;
                handler.removeCallbacks(this);
            }
        };


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            GameCommand gameCommand;

            int id = v.getId();

            if (id == R.id.btn_left) {
                direction = Missile.Direction.Left;
                gameCommand = GameCommand.Moving;

            } else if (id == R.id.btn_right) {
                direction = Missile.Direction.Right;
                gameCommand = GameCommand.Moving;

            } else if (id == R.id.btn_up) {
                direction = Missile.Direction.Up;
                gameCommand = GameCommand.Moving;

            } else if (id == R.id.btn_down) {
                direction = Missile.Direction.Down;
                gameCommand = GameCommand.Moving;

            } else if (id == R.id.btn_fire) {
                gameCommand = GameCommand.Fired;

            } else if (id == R.id.btn_pause) {
                if (gameState == GameState.Running) {
                    gameCommand = GameCommand.Pause;
                }
                else {
                    gameCommand = GameCommand.Pause;
                }
            }
            else {
                direction = Missile.Direction.Error;
                gameCommand = GameCommand.NOP;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if ( direction != null && gameState == GameState.Running && !onfire) {
                        handler.post(moveRunnable); // 버튼 눌렀을 때 반복 시작
                    }
                    else if (gameCommand == GameCommand.Fired && gameState == GameState.Running && !onfire) {
                            handler.post(setfireRunnable); // 미사일 발사 준비
                        }
                    else if (gameCommand == GameCommand.Pause && gameState == GameState.Running) {
                            handler.post(pauseRunnable);
                    }
                    else if (gameCommand == GameCommand.Pause && gameState == GameState.Paused){
                            handler.post(resumeRunnable);

                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    if (direction != null) {
                        handler.removeCallbacks(moveRunnable); // 버튼 떼면 반복 중지
                    }
                    else {
                        if (gameCommand == GameCommand.Fired && setfireRunnable != null && !onfire) {
                            handler.removeCallbacks(setfireRunnable); // 발사 준비 중지
                            handler.post(fireRunnable); // 실제 미사일 발사
                        }

                    }
                    direction = null;
                    gameState = GameState.stateFromInteger(stateTransMatrix[gameState.value()][gameCommand.value()]);
                    Log.d("asdf", "gameState = " + gameState);
                    return true;

                default:
                    return false;
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        savedState = gameState;
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
}