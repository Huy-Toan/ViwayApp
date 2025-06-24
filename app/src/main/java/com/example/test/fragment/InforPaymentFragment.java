package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.test.ConfirmInfoPaymentActivity;
import com.example.test.R;
import com.example.test.config.Config;
import com.example.test.response.TicketResponse;
import com.example.test.response.UserInfoResponse;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InforPaymentFragment extends Fragment {

    private RadioGroup radioGroup;
    private RadioButton radioBenXeVp, radioTrungChuyen;
    private EditText edtTrungChuyen;

    private ImageButton btnBack, btnEdit;
    private Button btnNext;
    private TextView diemDiHeader, diemDenHeader, ngayDiHeader,
            fullName, phone, email, gheDaChon,giaVe, loaiGhe, gioDi,
            gioDen, diemLenXe,khoangCach, diemXuongXe, benXeMacDinh;

    private ImageView imgDiemDi, imgDiemDen, imgLineDown;
    private String ngaydi, viTriDonKhach, token;
    private Integer ticketId, userId;
    private Boolean trungChuyen = false;
    private TicketResponse ticket;
    private ArrayList<String> selectedSeats;

    private UserInfoResponse userInfo;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.information_payment, container, false);

        btnBack = view.findViewById(R.id.btn_thongTin_back);
        btnEdit = view.findViewById(R.id.btn_thonTinEdit);

        btnNext = view.findViewById(R.id.thongTin_btnNext);
        diemDenHeader = view.findViewById(R.id.thongTin_diemDen);
        diemDiHeader = view.findViewById(R.id.thongTin_diemDi);
        ngayDiHeader = view.findViewById(R.id.thongTin_thoiGianDi);
        fullName = view.findViewById(R.id.thongTin_tvHoVaTen);
        phone = view.findViewById(R.id.thongTin_tvSdt);
        email = view.findViewById(R.id.thongTin_tvEmail);
        gheDaChon = view.findViewById(R.id.thongTin_viTriGhe);
        giaVe = view.findViewById(R.id.thongTin_GiaVe);
        loaiGhe = view.findViewById(R.id.thongTin_LoaiGhe);
        gioDi = view.findViewById(R.id.thongTin_GioXuatPhat);
        gioDen = view.findViewById(R.id.thongTin_GioDen);
        diemLenXe = view.findViewById(R.id.thongTin_diemLenXe);
        khoangCach = view.findViewById(R.id.thongTin_khoangCach);
        diemXuongXe = view.findViewById(R.id.thongTin_diemXuongXe);
        imgDiemDi = view.findViewById(R.id.img_diemDi);
        imgDiemDen = view.findViewById(R.id.img_diemDen);
        imgLineDown = view.findViewById(R.id.img_lineDown);

        radioGroup = view.findViewById(R.id.thongTin_radioGroup);
        radioBenXeVp = view.findViewById(R.id.thongTin_radioBenXe);
        radioTrungChuyen = view.findViewById(R.id.thongTin_radioTrungChuyen);
        edtTrungChuyen = view.findViewById(R.id.thongtin_edtTrungChuyen);
        benXeMacDinh = view.findViewById(R.id.thongTin_tvBenXeMacDinh);

        Bundle bundle = getArguments();
        if (bundle != null) {
            ngaydi = bundle.getString("ngayDiHeader");
            ticket = (TicketResponse) bundle.getSerializable("ticket");
            selectedSeats = bundle.getStringArrayList("selectedSeats");

            if (ticket != null && ngaydi != null) {
                ticketId = ticket.getTicketId();

                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                String giaVeFormatted = formatter.format(ticket.getGiaVe()) + " VNĐ";
                String khoangCachFormatted = "Khoảng cách " + formatter.format(ticket.getKhoangCach()) + " km - " + ticket.getThoiGianDi();

                diemDiHeader.setText(ticket.getDiemDi());
                diemDenHeader.setText(ticket.getDiemDen());
                ngayDiHeader.setText(ngaydi);
                gioDi.setText(ticket.getGioDi());
                giaVe.setText(giaVeFormatted);
                loaiGhe.setText(ticket.getLoaiGhe());
                gioDen.setText(ticket.getGioDen());
                gioDi.setText(ticket.getGioDi());
                diemLenXe.setText(ticket.getDiemDi());
                khoangCach.setText(khoangCachFormatted);
                diemXuongXe.setText(ticket.getDiemDen());
                gheDaChon.setText(TextUtils.join(", ", selectedSeats));

                imgDiemDi.setBackgroundResource(R.drawable.ic_diemdi);
                imgDiemDen.setBackgroundResource(R.drawable.ic_diemden);
                imgLineDown.setBackgroundResource(R.drawable.ic_linedown);

            }

        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");
        GetUserInfo(userId, token);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (radioBenXeVp.isChecked()) {
                    edtTrungChuyen.setVisibility(View.GONE);
                    benXeMacDinh.setVisibility(View.VISIBLE);
                    benXeMacDinh.setText(ticket.getDiemDi());
                } else if (radioTrungChuyen.isChecked()) {
                    edtTrungChuyen.setVisibility(View.VISIBLE);
                    benXeMacDinh.setVisibility(View.GONE);
                }
            }
        });

        btnEdit.setOnClickListener(v -> {
            showEditDialog();
        });

        btnBack.setOnClickListener(v -> {
            getActivity().finish();
        });

        btnNext.setOnClickListener(v -> {
            if (radioTrungChuyen.isChecked()) {
                viTriDonKhach = edtTrungChuyen.getText().toString().trim();
                trungChuyen = true;
            } else {
                viTriDonKhach = ticket.getDiemDi();
                trungChuyen = false;
            }

            Intent intent = new Intent(getContext(), ConfirmInfoPaymentActivity.class);
            intent.putExtra("ticket", ticket);
            intent.putExtra("ngayDiHeader", ngaydi);
            intent.putExtra("diemDon", viTriDonKhach);
            intent.putExtra("trungChuyen", trungChuyen);
            ArrayList<String> selectedList = new ArrayList<>(selectedSeats);
            intent.putStringArrayListExtra("selectedSeats", selectedList);
            intent.putExtra("userInfo", userInfo);

            startActivity(intent);

        });

        return view;
    }

    private void GetUserInfo(Integer userId, String token) {
        String baseUrl = Config.BASE_URL+ "/users/get-info/" + userId;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    try {
                        JSONObject obj = new JSONObject(responseBody);

                        String fullname = obj.getString("fullname");
                        String phone = obj.getString("phone_number");
                        String email = obj.getString("email");

                        userInfo = new UserInfoResponse(fullname, phone, email);

                        requireActivity().runOnUiThread(() -> updateDisplay(userInfo));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void updateDisplay(UserInfoResponse userInfo) {
        fullName.setText(userInfo.getFullname());
        phone.setText(userInfo.getPhone());
        email.setText(userInfo.getEmail());
    }

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_user, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

        EditText edtFullname = view.findViewById(R.id.edtFullname);
        EditText edtPhone = view.findViewById(R.id.edtPhone);
        EditText edtEmail = view.findViewById(R.id.edtEmail);
        Button btnSave = view.findViewById(R.id.btnSave);

        // Gán sẵn thông tin cũ vào EditText
        edtFullname.setText(fullName.getText().toString());
        edtPhone.setText(phone.getText().toString());
        edtEmail.setText(email.getText().toString());

        btnSave.setOnClickListener(v -> {
            String newFullname = edtFullname.getText().toString().trim();
            String newPhone = edtPhone.getText().toString().trim();
            String newEmail = edtEmail.getText().toString().trim();

            fullName.setText(newFullname);
            phone.setText(newPhone);
            email.setText(newEmail);

            userInfo.setFullname(newFullname);
            userInfo.setPhone(newPhone);
            userInfo.setEmail(newEmail);

            dialog.dismiss();
        });
    }

//    private void updateUserInfoToServer(String userId, String fullname, String phone, String email) {
//        String url = "https://6851a3e58612b47a2c0ad35e.mockapi.io/getInfo/" + userId;
//
//        OkHttpClient client = new OkHttpClient();
//
//        JSONObject json = new JSONObject();
//        try {
//            json.put("fullname", fullname);
//            json.put("phone", phone);
//            json.put("email", email);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody body = RequestBody.create(
//                json.toString(),
//                MediaType.get("application/json")
//
//        );
//
//        Request request = new Request.Builder()
//                .url(url)
//                .put(body)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//                requireActivity().runOnUiThread(() ->
//                        Toast.makeText(getContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
//                );
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    requireActivity().runOnUiThread(() -> {
//                            Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
//                            GetUserInfo(userId);
//                    });
//                } else {
//                    requireActivity().runOnUiThread(() ->
//                            Toast.makeText(getContext(), "Lỗi server", Toast.LENGTH_SHORT).show()
//                    );
//                }
//            }
//        });
//    }




}
