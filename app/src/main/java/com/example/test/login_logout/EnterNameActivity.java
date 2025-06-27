package com.example.test.login_logout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

public class EnterNameActivity extends AppCompatActivity {

    private EditText edName;
    private Button btnNext;
    private String contact, username;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_name);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.entername), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edName = findViewById(R.id.EnterName_editName);
        btnNext = findViewById(R.id.EnterName_btnNext);

        Intent intent = getIntent();
        contact = intent.getStringExtra("contact");

        btnNext.setOnClickListener(v -> {
            username = edName.getText().toString();
            Intent it = new Intent(EnterNameActivity.this, CreatePassWordActivity.class);
            it.putExtra("contact", contact);
            it.putExtra("username", username);
            startActivity(it);
        });

    }

}
