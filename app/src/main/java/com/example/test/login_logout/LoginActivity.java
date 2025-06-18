package com.example.test.login_logout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.MainActivity;
import com.example.test.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.Request;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextNameLogin, editTextPassLogin;

    private Button btnLogin;
    private TextView forgotPass,tvRegister;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextNameLogin = findViewById(R.id.editText_UsernameLogin);
        editTextPassLogin = findViewById(R.id.editText_PassWordLogin);
        forgotPass = findViewById(R.id.forgot_password);
        tvRegister = findViewById(R.id.textView_Register);
        btnLogin = findViewById(R.id.btn_Login);

        btnLogin.setOnClickListener(v -> {
            String userName = editTextNameLogin.getText().toString().trim();
            String password = editTextPassLogin.getText().toString().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(userName, password);

        });


        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void loginUser (String Email, String Password) {
        String url = "https://684c3de1ed2578be881e322c.mockapi.io/loginUser?email=" +
                Uri.encode(Email) + "&password=" + Uri.encode(Password);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Log.d("LoginResponse", responseData);

                    try {
                        JSONArray jsonArray = new JSONArray(responseData);
                        if(jsonArray.length() == 0) {
                            runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Sai thông tin đăng nhập", Toast.LENGTH_SHORT).show());
                            return;
                        }

                        JSONObject user = jsonArray.getJSONObject(0);
                        String name = user.optString("name", "Chưa có tên");
                        String userId = user.optString("userId","Chưa có ID");
                        String token = user.optString("token", "Chưa có token");

                        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("name", name);
                        editor.putString("userId", userId);
                        editor.putString("token", token);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        });

                    } catch (JSONException e) {
                        runOnUiThread(() ->
                                Toast.makeText(LoginActivity.this, "Lỗi xử lý dữ liệu", Toast.LENGTH_SHORT).show()
                        );
                    }

                } else {
                    runOnUiThread(() ->
                            Toast.makeText(LoginActivity.this, "Sai thông tin đăng nhập hoặc lỗi server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });

    }

}
