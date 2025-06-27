package com.example.test.login_logout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.config.Config;
import com.example.test.response.TicketResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EnterPassActivity extends AppCompatActivity {

    private Button btnDangNhap;
    private ImageButton btnBack;
    private EditText edNhapMatKhau;
    private TextView khongPhaiToi, quenMatKhau, xinChao;
    private String name, contact, passWord;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginpassword), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnDangNhap = findViewById(R.id.login_btnDangNhap);
        btnBack = findViewById(R.id.login_btnBack);
        edNhapMatKhau = findViewById(R.id.login_editPassWord);
        khongPhaiToi = findViewById(R.id.login_tvKhongPhaiToi);
        quenMatKhau = findViewById(R.id.login_tvQuenMatKhau);
        xinChao = findViewById(R.id.login_tvXinChao);

        Intent intent = getIntent();

        if (intent != null) {
            name = intent.getStringExtra("fullName");
            contact = intent.getStringExtra("contact");

            xinChao.setText("Xin chào " + name);
        }

        btnDangNhap.setOnClickListener(v -> {
            passWord = edNhapMatKhau.getText().toString();
            sendInfo(contact, passWord);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void sendInfo (String contact, String passWord) {
        String url = Config.BASE_URL+ "/users/login";
        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try{
            data.put("phone_number", contact);
            data.put("password", passWord);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(
                data.toString(),
                MediaType.parse("application/json")
        );


        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("sendPhone", "Lỗi gửi request: " + e.getMessage(), e);
                runOnUiThread(() ->
                        Toast.makeText(EnterPassActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();

                try {
                    JSONObject json = new JSONObject(body);
                    if (!response.isSuccessful() || json.has("message")) {
                        String message = json.optString("message", "Đăng nhập thất bại");
                        runOnUiThread(() ->
                                Toast.makeText(EnterPassActivity.this, message, Toast.LENGTH_LONG).show()
                        );
                        return;
                    }

                    // Trường hợp đăng nhập thành công
                    String fullName = json.optString("fullName", "Chưa có tên");
                    int userId = json.optInt("userId", 0);
                    String token = json.optString("token", "Chưa có token");

                    // Lưu thông tin vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putInt("userId", userId);
                    editor.putString("fullName", fullName);
                    editor.putString("token", token);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // Chuyển sang màn hình chính sau khi đăng nhập
                    runOnUiThread(() -> {
                        Intent intent = new Intent(EnterPassActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(EnterPassActivity.this, "Lỗi phản hồi JSON", Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }


}
