package com.example.test.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText hoVaTen, sdt, email, passWord, rePassWord;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        hoVaTen = findViewById(R.id.editText_HoTenRegister);
        sdt = findViewById(R.id.editText_PhoneRegister);
        email = findViewById(R.id.editText_EmailRegister);
        passWord = findViewById(R.id.editText_PassWordRegister);
        rePassWord = findViewById(R.id.editText_RePassWordRegister);
        btnRegister = findViewById(R.id.btn_Register);

        btnRegister.setOnClickListener(v -> {
            String HoVaTen = hoVaTen.getText().toString();
            String SoDienThoai = sdt.getText().toString();
            String PassWord = passWord.getText().toString();
            String Email = email.getText().toString();
            String RePassWord = rePassWord.getText().toString();


            if (!PassWord.equals(RePassWord)) {
                Toast.makeText(this,"Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterUser(HoVaTen, SoDienThoai, Email, PassWord);

        });

    }

    private void RegisterUser (String Hoten, String Sdt,String Email, String Password) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        String json = "{"
                + "\"name\":\"" + Hoten + "\","
                + "\"phone\":\"" + Sdt + "\","
                + "\"email\":\"" + Email + "\","
                + "\"password\":\"" + Password + "\""
                + "}";

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("https://684c3de1ed2578be881e322c.mockapi.io/loginUser")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(()->
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối"+ e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                runOnUiThread(() ->{
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}