package com.example.test.login_logout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InputPhoneActivity extends AppCompatActivity {

    private EditText inputPhone;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            startActivity(new Intent(InputPhoneActivity.this, MainActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.enter_phone);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inputphone), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputPhone = findViewById(R.id.phone_EditPhone);
        btnNext = findViewById(R.id.phone_btnNext);


        btnNext.setOnClickListener(v -> {
            String ct = inputPhone.getText().toString().trim();
            if (ct.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập thông tin", Toast.LENGTH_SHORT).show();
            } else {
                sendContact(ct);
            }
        });


    }

    private void sendContact (String contact) {
        String url = Config.BASE_URL+ "/otp/send";
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
                runOnUiThread(() ->
                        Toast.makeText(InputPhoneActivity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONObject jsob = new JSONObject(body);

                        if (jsob.has("registered") && jsob.getBoolean("registered")) {
                            int userId = jsob.optInt("userId");
                            String fullName = jsob.optString("fullName");

                            Intent intent = new Intent(InputPhoneActivity.this, EnterPassActivity.class);
                            intent.putExtra("contact", contact);
                            intent.putExtra("userId", userId);
                            intent.putExtra("fullName", fullName);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        Log.e("RESPONSE", "Không phải JSON: " + body);
                        Intent intent = new Intent(InputPhoneActivity.this, EnterOtpActivity.class);
                        intent.putExtra("contact", contact);
                        startActivity(intent);
                    }
                });
            }

        });
    }
}