package com.example.fortress;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_gameOver extends AppCompatActivity {

    Button btn_quit;
    TextView result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gameover);

        Intent intent = getIntent();

        btn_quit = findViewById(R.id.quit);
        result = findViewById(R.id.result);

        String data = intent.getStringExtra("player");
        if (data != null) {
            result.setText(String.format("%s win!", data));
        }

        //quit 버튼
        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("result", "quit");
                setResult(RESULT_OK, intent);

                finish();
            }
        });
    }

    //팝업창 이외의 공간을 터치해도 닫히지 않도록
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

}
