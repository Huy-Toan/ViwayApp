package com.example.test.support;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

public class NoiDungHoTroActivity extends AppCompatActivity {

    private TextView tieuDe;
    private String text;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evaluation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.evaluation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tieuDe = findViewById(R.id.Evaluation_tvHeader);
        btnBack = findViewById(R.id.Evaluation_btnBack);

        Intent intent = getIntent();
        text = intent.getStringExtra("tieuDe");

        tieuDe.setText(text);

        btnBack.setOnClickListener(v -> {
            finish();
        });

    }


}
