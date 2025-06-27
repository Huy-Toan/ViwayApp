package com.example.test.login_logout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.aabhasjindal.otptextview.OtpTextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EnterOtpActivity extends AppCompatActivity {

    private TextView sdt, guiLaiMa, thoiGian;
    private Button btnNext;
    private ImageButton btnBack;
    private OtpTextView otpTextView;

    private String otp, contact;
    private CountDownTimer countDownTimer;

    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.enter_code);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.entercode), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnNext = findViewById(R.id.Entercode_btnNext);
        btnBack = findViewById(R.id.Entercode_btnBack);
        sdt = findViewById(R.id.Entercode_tvPhone);
        otpTextView = findViewById(R.id.Entercode_otpView);
        guiLaiMa = findViewById(R.id.Entercode_GuiLaiMa);
        thoiGian = findViewById(R.id.Entercode_time);

        otpTextView.requestFocus();
        startCountdownTimer();

        Intent intent = getIntent();

        if (intent != null) {
            contact = intent.getStringExtra("contact");
            sdt.setText(contact);
        }

        guiLaiMa.setOnClickListener(v -> {
            sendPhone(contact);
        });

        btnNext.setOnClickListener(v -> {
            otp = otpTextView.getOTP();
            sendOtp(contact, otp);
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });

    }

    private void sendOtp (String phone, String otp) {
        String url = Config.BASE_URL + "/otp/verify";
        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try{
            data.put("contact", phone);
            data.put("otp", otp);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(
                data.toString(),
                MediaType.parse("application/json")
        );

        Log.d("Login", data.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(EnterOtpActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();

                try {
                    JSONObject json = new JSONObject(body);
                    boolean verify = json.getBoolean("verify");

                    Log.d("Verify", json.toString());
                    runOnUiThread(() -> {
                        if (verify) {
                            Intent intent = new Intent(EnterOtpActivity.this, EnterNameActivity.class);
                            intent.putExtra("contact", contact);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(EnterOtpActivity.this, "OTP không đúng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() ->
                            Toast.makeText(EnterOtpActivity.this, "Lỗi phản hồi JSON", Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }

    private void sendPhone (String contact) {
        String url = Config.BASE_URL + "/otp/send";
        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try{
            data.put("contact", contact);
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
                        Toast.makeText(EnterOtpActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> startCountdownTimer());
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(EnterOtpActivity.this, "Gửi thất bại (response lỗi)", Toast.LENGTH_LONG).show()
                    );
                }
            }

        });
    }

    private void startCountdownTimer() {
        guiLaiMa.setEnabled(false);
        guiLaiMa.setAlpha(0.5f);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                thoiGian.setText(millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                guiLaiMa.setEnabled(true);
                guiLaiMa.setAlpha(1.0f);
                thoiGian.setText("");
            }
        };

        countDownTimer.start();
    }



}
