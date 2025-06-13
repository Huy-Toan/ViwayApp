package com.example.test.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.MainActivity;
import com.example.test.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextNameLogin, editTextPassLogin;
    private String userName, password;
    private Button btnLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextNameLogin = findViewById(R.id.editText_UsernameLogin);
        editTextPassLogin = findViewById(R.id.editText_PassWordLogin);
        btnLogin = findViewById(R.id.btn_Login);

        btnLogin.setOnClickListener(v -> {
            userName = editTextNameLogin.getText().toString().trim();
            password = editTextPassLogin.getText().toString().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // URL chỉ chứa endpoint, không có query
            String url = "https://684c3de1ed2578be881e322c.mockapi.io/loginUser";

            // Tạo body JSON
            JSONObject loginData = new JSONObject();
            try {
                loginData.put("username", userName);
                loginData.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Gửi POST request
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, loginData,
                    response -> {
                        try {
                            String username = response.getString("username");
                            String userId = response.getString("id");
                            String token = response.has("token") ? response.getString("token") : "";

                            // Lưu thông tin đăng nhập
                            SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", username);
                            editor.putString("userId", userId);
                            editor.putString("token", token);
                            editor.putBoolean("isLoggedIn", true);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Lỗi xử lý dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Sai thông tin đăng nhập hoặc lỗi server", Toast.LENGTH_SHORT).show();
                    });

            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(request);
        });
    }
}
