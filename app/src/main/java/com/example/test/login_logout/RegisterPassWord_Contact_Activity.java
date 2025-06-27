package com.example.test.login_logout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
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

import com.example.test.R;
import com.example.test.config.Config;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterPassWord_Contact_Activity extends AppCompatActivity {

    private TextView chuThuong, chuHoa, kiTuDacBiet, so;
    private ImageButton btnBack;
    private Button btnXacNhan;
    private String contact, username, email, phone;
    private EditText passWord, rePassWord, thongTin;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.create_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.createpass), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        chuThuong = findViewById(R.id.Create_tvLowercase);
        chuHoa = findViewById(R.id.Create_tvUppercase);
        kiTuDacBiet = findViewById(R.id.Create_tvSpecialChar);
        so = findViewById(R.id.Create_tvNumber);
        passWord = findViewById(R.id.Create_Pass);
        rePassWord = findViewById(R.id.Create_rePass);
        thongTin = findViewById(R.id.Create_ThongTin);
        btnXacNhan = findViewById(R.id.Create_btnXacNhan);

        Intent it = getIntent();
        contact = it.getStringExtra("contact");
        username = it.getStringExtra("username");

        if (isEmail(contact)) {
            email = contact;
            thongTin.setHint("Nhập số điện thoại");
        } else if (isPhoneNumber(contact)) {
            phone = contact;
            thongTin.setHint("Nhập email");
        }

        passWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();

                if (password.matches(".*[a-z].*")) {
                    chuThuong.setTextColor(Color.BLUE);
                } else {
                    chuThuong.setTextColor(Color.RED);
                }

                if (password.matches(".*[A-Z].*")) {
                    chuHoa.setTextColor(Color.BLUE);
                } else {
                    chuHoa.setTextColor(Color.RED);
                }

                if (password.matches(".*\\d.*")) {
                    so.setTextColor(Color.BLUE);
                } else {
                    so.setTextColor(Color.RED);
                }

                if (password.matches(".*[@$!%*?&^#()._+=\\-].*")) {
                    kiTuDacBiet.setTextColor(Color.BLUE);
                } else {
                    kiTuDacBiet.setTextColor(Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnXacNhan.setOnClickListener(v -> {
            String pass = passWord.getText().toString();
            String repass = rePassWord.getText().toString();
            String thongtin = thongTin.getText().toString();
            if (isEmail(contact)) {
                phone = thongtin;
            } else if (isPhoneNumber(contact)) {
                email = thongtin;
            }

            if (pass.equals(repass)) {
                sendDataToServer(username, email, phone, pass, repass);
            } else {
                rePassWord.setError("Mật khẩu nhập lại không khớp");
                rePassWord.requestFocus();
            }
        });

    }

    private void sendDataToServer (String username, String email, String phone, String passWord, String rePassWord) {

        String url = Config.BASE_URL + "/users/register";
        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();

        try{
            data.put("fullname", username);
            data.put("phone_number", phone);
            data.put("email", email);
            data.put("password", passWord);
            data.put("retype_password", rePassWord);
            data.put("role_id", 2);

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
                        Toast.makeText(RegisterPassWord_Contact_Activity.this, "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> showNotifyCreateSuccessfull());
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterPassWord_Contact_Activity.this, "Tạo tài khoản thất bại", Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    public boolean isEmail(String input) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    public boolean isPhoneNumber(String input) {
        return android.util.Patterns.PHONE.matcher(input).matches();
    }

    public void showNotifyCreateSuccessfull() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPassWord_Contact_Activity.this);
        View view = LayoutInflater.from(RegisterPassWord_Contact_Activity.this).inflate(R.layout.dialog_create_account_successfull, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = view.findViewById(R.id.CreateAccountSuccess_btnDong);

        btnNo.setOnClickListener(v -> {
            Intent it = new Intent(RegisterPassWord_Contact_Activity.this, InputPhoneActivity.class);
            startActivity(it);
            dialog.dismiss();
            finish();

        });

    }


}
