package com.example.fortress;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Activity_game extends AppCompatActivity {

    private GameSurfaceView gameSurface;


    public enum PlayerNum {
        Player1(0), Player2(1);
        private final int value;
        private PlayerNum(int value) { this.value = value; }
        public int value() { return value; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        Button btnLeft = findViewById(R.id.btn_left);
        Button btnRight = findViewById(R.id.btn_right);
        Button btnUp = findViewById(R.id.btn_up);
        Button btnDown = findViewById(R.id.btn_down);
        Button btnFire = findViewById(R.id.btn_fire);
        Button btnPause = findViewById(R.id.btn_pause);

        btnLeft.setOnTouchListener(createMoveListener(GameSurfaceView.Direction.Left));
        btnRight.setOnTouchListener(createMoveListener(GameSurfaceView.Direction.Right));
        btnUp.setOnTouchListener(createMoveListener(GameSurfaceView.Direction.Up));
        btnDown.setOnTouchListener(createMoveListener(GameSurfaceView.Direction.Down));

        btnFire.setOnTouchListener(createFireListener());

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 나중에는 팝업 창을 띄워서 임시정지로 바꾸기
                finish();
            }
        });
    }



    private View.OnTouchListener createMoveListener(GameSurfaceView.Direction direction) {
        return new View.OnTouchListener() {

            private Handler handler = new Handler();
            private Runnable moveRunnable = new Runnable() {
                @Override
                public void run() {
                    gameSurface.move(direction);
                    handler.postDelayed(this, 10); // 10ms마다 반복
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler.post(moveRunnable); // 버튼 누르면 반복 시작
                        return true;

                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(moveRunnable); // 버튼 떼면 반복 중단
                        return true;

                    default:
                        return false;
                }
            }
        };
    }

    private View.OnTouchListener createFireListener() {
        return new View.OnTouchListener() {

            private Handler handler = new Handler();
            private Runnable setfireRunnable = new Runnable() {
                @Override
                public void run() {
                    gameSurface.setFire(); // 미사일 발사 준비 상태
                    handler.postDelayed(this, 10); // 10ms마다 반복
                }
            };

            private Runnable fireRunnable = new Runnable() {
                @Override
                public void run() {
                    gameSurface.FireMissile(); // 실제 미사일 발사
                    handler.removeCallbacks(this);
//                    handler.postDelayed(this, 10); // 10ms마다 반복
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler.post(setfireRunnable); // 버튼 누르면 발사 준비 반복 시작

                        return true;

                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(setfireRunnable); // 발사 준비 반복 중지
                        handler.post(fireRunnable); // 미사일 발사 반복 시작
                        return true;

                    default:
                        return false;
                }
            }
        };
    }




}