package com.example.fortress;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_pause extends AppCompatActivity {

    Button btn_resume;
    Button btn_quit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pause);

        btn_resume = findViewById(R.id.resume);
        btn_quit = findViewById(R.id.quit);

        //resume 버튼
        btn_resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                intent.putExtra("result", "resume");
                setResult(RESULT_OK, intent);

                finish();
            }
        });

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
