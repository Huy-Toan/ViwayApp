package com.example.test.info;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

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

public class EditInfoCustomerActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout hoVaTen, eMail,ngaySinh, ngheNghiep, themDiaChi;
    private TextView tvPhone, tvHoTen, tvEmail, tvNgaySinh, tvNgheNghiep, tvDiaChi;
    private Button btnUpdate;
    private RadioGroup gioiTinh;
    private RadioButton rdNam, rdNu;
    private String token;
    private Integer userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.updateinfo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnBack = findViewById(R.id.UpdateInfo_btnBack);
        btnUpdate = findViewById(R.id.UpdateInfo_btnCapNhat);
        tvPhone = findViewById(R.id.UpdateInfo_tvSdt);
        tvHoTen = findViewById(R.id.UpdateInfo_tvHoVaTen);
        tvEmail = findViewById(R.id.UpdateInfo_tvEmail);
        tvNgaySinh = findViewById(R.id.UpdateInfo_tvNgaySinh);
        tvNgheNghiep = findViewById(R.id.UpdateInfo_tvNgheNghiep);
        tvDiaChi = findViewById(R.id.UpdateInfo_tvDiaChi);
        hoVaTen = findViewById(R.id.UpdateInfo_HoVaTen);
        eMail = findViewById(R.id.UpdateInfo_Email);
        ngaySinh = findViewById(R.id.UpdateInfo_NgaySinh);
        ngheNghiep = findViewById(R.id.UpdateInfo_NgheNghiep);
        themDiaChi = findViewById(R.id.UpdateInfo_ThemDiaChi);
        gioiTinh = findViewById(R.id.UpdateInfo_RadioGroup);
        rdNam = findViewById(R.id.UpdateInfo_RadioNam);
        rdNu = findViewById(R.id.UpdateInfo_RadioNu);

        InfoCustomerResponse info = (InfoCustomerResponse) getIntent().getSerializableExtra("infoUser");
        if (info != null) {
            tvHoTen.setText(info.getFullName());
            tvEmail.setText(info.getEmail());
            tvPhone.setText(info.getPhone());
            tvNgaySinh.setText(info.getDateOfBirth());
            tvNgheNghiep.setText(info.getJob());
            tvDiaChi.setText(info.getAddress());

            String gender = info.getSex();
            if (gender != null && gender.equalsIgnoreCase("Nam")) {
                rdNam.setChecked(true);
            } else if (gender != null && gender.equalsIgnoreCase("Nữ")) {
                rdNu.setChecked(true);
            } else {
                gioiTinh.clearCheck();
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");


        hoVaTen.setOnClickListener(v -> {
            showEditDialog(v, "Họ và tên", tvHoTen);
        });

        eMail.setOnClickListener(v -> {
            showEditDialog(v, "Email", tvEmail);
        });

        ngheNghiep.setOnClickListener(v -> {
            showEditDialog(v, "Nghề nghiệp", tvNgheNghiep);
        });

        themDiaChi.setOnClickListener(v -> {
            showEditDialog(v, "Thêm địa chỉ", tvDiaChi);
        });

        btnUpdate.setOnClickListener(v -> {
            String fullname = tvHoTen.getText().toString();
            String email = tvEmail.getText().toString();
            String address = tvDiaChi.getText().toString();
            String job = tvNgheNghiep.getText().toString();
            String sex = "";
            int checkedId = gioiTinh.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Vui lòng chọn giới tính", Toast.LENGTH_SHORT).show();
                gioiTinh.requestFocus();
                return;
            }

            if (checkedId == R.id.UpdateInfo_RadioNam) {
                sex = "Nam";
            } else if (checkedId == R.id.UpdateInfo_RadioNu) {
                sex = "Nữ";
            }

            updateInfoUser(token, userId, fullname, email, address, job, sex);

        });

        btnBack.setOnClickListener(v -> {
            showBackDialog();
        });

    }

    private void updateInfoUser (String token, Integer userId, String fullname,
                                 String email, String address, String job, String sex) {
        String url = Config.BASE_URL+ "/users/update/" + userId;

        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try{
            data.put("fullname", fullname);
            data.put("email", email);
            data.put("address", address);
            data.put("job", job);
            data.put("sex", sex);
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
                .addHeader("Authorization", "Bearer "+ token)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(EditInfoCustomerActivity.this, "Gửi dữ liệu thất bại " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        showNotifyUpdateSuccess();
                    });
                } else {
                    int statusCode = response.code();
                    runOnUiThread(() -> {
                        Toast.makeText(EditInfoCustomerActivity.this,
                                "Cập nhật thất bại. Mã lỗi: " + statusCode,
                                Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

    }

    public void showEditDialog(View v, String title, TextView targetTextView) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
        View bottomSheetView = LayoutInflater.from(v.getContext())
                .inflate(R.layout.bottom_dialog_edit, null);

        TextView label = bottomSheetView.findViewById(R.id.EditInfo_label);
        TextView tieuDe = bottomSheetView.findViewById(R.id.EditInfo_tieuDe);
        Button btnUpdate = bottomSheetView.findViewById(R.id.EditInfo_btnUpdate);
        EditText noiDung = bottomSheetView.findViewById(R.id.EditInfo_noiDung);

        tieuDe.setText(title);
        label.setText(title);

        noiDung.setHint(targetTextView.getText().toString());

        btnUpdate.setOnClickListener(view -> {
            String content = noiDung.getText().toString();
            targetTextView.setText(content);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showBackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditInfoCustomerActivity.this);
        View view = LayoutInflater.from(EditInfoCustomerActivity.this).inflate(R.layout.dialog_none_update, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = view.findViewById(R.id.NoneUpdate_btnClose);
        Button btnYes = view.findViewById(R.id.NoneUpdate_btnYes);

        btnNo.setOnClickListener(v -> dialog.dismiss());

        btnYes.setOnClickListener(v -> {
            finish();
            dialog.dismiss();
        });
    }

    public void showNotifyUpdateSuccess() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditInfoCustomerActivity.this);
        View view = LayoutInflater.from(EditInfoCustomerActivity.this).inflate(R.layout.dialog_create_account_successfull, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Button btnNo = view.findViewById(R.id.CreateAccountSuccess_btnDong);

        btnNo.setOnClickListener(v -> {
            Intent it = new Intent(EditInfoCustomerActivity.this, InfoCustumerActivity.class);
            startActivity(it);
            dialog.dismiss();
            finish();

        });

    }


}
