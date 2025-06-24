package com.example.test.info;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.config.Config;
import com.example.test.response.InfoCustomerResponse;
import com.example.test.response.UserInfoResponse;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoCustumerActivity extends AppCompatActivity {

    private InfoCustomerResponse infoCustomerResponse;
    private ImageButton btnBack;
    private Button btnUpdateInfo, btnLogout;
    private Integer userId;
    private TextView phone, fullname, email, sex, job, dateOfBirth;
    private String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.info), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnUpdateInfo = findViewById(R.id.Info_btnCapNhatThongTin);
        btnLogout = findViewById(R.id.Info_btnDangXuat);
        btnBack = findViewById(R.id.Info_btnBack);
        phone = findViewById(R.id.Info_tvPhone);
        fullname = findViewById(R.id.Info_tvFullName);
        email = findViewById(R.id.Info_tvEmail);
        sex = findViewById(R.id.Info_tvGioiTinh);
        job = findViewById(R.id.Info_tvNgheNghiep);
        dateOfBirth = findViewById(R.id.Info_tvNgaySinh);

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");
        userId = sharedPreferences.getInt("userId", -1);

        getInfo(userId, token);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnUpdateInfo.setOnClickListener(v -> {
            Intent intent = new Intent(InfoCustumerActivity.this, EditInfoCustomerActivity.class);
            startActivity(intent);
        });

    }

    private void getInfo (Integer userId, String token) {
        String baseUrl = Config.BASE_URL + "/users/get-user/" + userId;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(InfoCustumerActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(responseBody);

                        int id = obj.optInt("id", -1);
                        String fullName = obj.optString("fullName", "Trống");
                        String phone = obj.optString("phoneNumber", "Trống");
                        String email = obj.optString("email", "Trống");
                        String address = obj.optString("address", "Trống");
                        String sex = obj.optString("sex", "Trống");
                        String job = obj.optString("job", "Trống");
                        String dateOfBirth = obj.optString("dateOfBirth", "Trống");
                        infoCustomerResponse = new InfoCustomerResponse(id,fullName, phone, email, address, sex, job, dateOfBirth);

                        runOnUiThread(() -> updateDisplay(infoCustomerResponse));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(InfoCustumerActivity.this, "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void updateDisplay(InfoCustomerResponse infoCustomer) {
        fullname.setText(infoCustomer.getFullName());
        phone.setText(infoCustomer.getPhone());
        email.setText(infoCustomer.getEmail());
        sex.setText(infoCustomer.getSex());
        job.setText(infoCustomer.getJob());
        dateOfBirth.setText(infoCustomer.getDateOfBirth());
    }

}
