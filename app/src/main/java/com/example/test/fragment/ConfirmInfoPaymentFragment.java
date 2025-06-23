package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.SheetPaymentBottom;
import com.example.test.response.TicketResponse;
import com.example.test.response.UserInfoResponse;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;


public class ConfirmInfoPaymentFragment extends Fragment {

    private TextView diemDiHeader, diemDenHeader, thoiGianHeader,
                        tuyenXe, thoiGianKhoiHanh, soLuongVe, viTriGhe, diemLenXe,
                        thoiGianTrungChuyen, diemXuongXe, fullname, sdt, email, thoiGianGiuCho,
                        giaVe, phiThanhToan, tongThanhToan;

    private Integer tongTien = 0, ticketId;
    private String ngaydi, userId;
    private ImageButton btnBack;
    private Button btnThanhToan;
    private TicketResponse ticket;
    private UserInfoResponse userInfo;
    private ArrayList<String> selectedSeats;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirm_infor_payment, container, false);

        diemDiHeader = view.findViewById(R.id.confirm_DiemDiHeader);
        diemDenHeader = view.findViewById(R.id.confirm_DiemDenHeader);
        thoiGianHeader = view.findViewById(R.id.confirm_ThoiGianHeader);
        tuyenXe = view.findViewById(R.id.confirm_tvTuyenXe);
        thoiGianKhoiHanh = view.findViewById(R.id.confirm_tvThoiGianDi);
        thoiGianTrungChuyen = view.findViewById(R.id.confirm_tvThoiGianTrungChuyen);
        soLuongVe = view.findViewById(R.id.confirm_tvSoLuongVe);
        viTriGhe = view.findViewById(R.id.confirm_tvViTriGhe);
        diemLenXe = view.findViewById(R.id.confirm_tvDiemLenXe);
        diemXuongXe = view.findViewById(R.id.confirm_tvDiemXuongXe);
        fullname = view.findViewById(R.id.confirm_tvHoVaTen);
        sdt = view.findViewById(R.id.confirm_tvSdt);
        email = view.findViewById(R.id.confirm_tvEmail);
        thoiGianGiuCho = view.findViewById(R.id.confirm_tvThoiGianGiuVe);
        giaVe = view.findViewById(R.id.confirm_tvGiaTien);
        phiThanhToan = view.findViewById(R.id.confirm_tvPhiThanhToan);
        tongThanhToan = view.findViewById(R.id.confirm_tvTongTien);

        btnBack = view.findViewById(R.id.confirm_btn_back);
        btnThanhToan = view.findViewById(R.id.confirm_btnThanhToan);

        Bundle bundle = getArguments();
        if (bundle != null) {
            ngaydi = bundle.getString("ngayDiHeader");
            ticket = (TicketResponse) bundle.getSerializable("ticket");
            userInfo = (UserInfoResponse) bundle.getSerializable("userInfo") ;
            selectedSeats = bundle.getStringArrayList("selectedSeats");

            if (ticket != null && ngaydi != null && userInfo != null) {
                ticketId = ticket.getTicketId();

                diemDiHeader.setText(ticket.getDiemDi());
                diemDenHeader.setText(ticket.getDiemDen());
                thoiGianHeader.setText(ngaydi);
                tuyenXe.setText(ticket.getDiemDi());
                thoiGianKhoiHanh.setText(ticket.getGioDi() +" "+ ngaydi);
                soLuongVe.setText(selectedSeats.size() + " Vé");
                viTriGhe.setText(TextUtils.join(", ", selectedSeats));
                diemLenXe.setText(ticket.getDiemDi());
                diemXuongXe.setText(ticket.getDiemDen());

                fullname.setText(userInfo.getFullname());
                sdt.setText(userInfo.getPhone());
                email.setText(userInfo.getEmail());

                tongTien = selectedSeats.size() * ticket.getGiaVe();
            }

            Log.d("SelectedSeatFragment", "TicketID: " + ticketId);
        }

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        btnThanhToan.setOnClickListener(v -> {
            SheetPaymentBottom sheetPaymentBottom = new SheetPaymentBottom(method -> {
                sendBookingToServer(method);
            });

            sheetPaymentBottom.show(getParentFragmentManager(), "PaymentSheet");
        });


        btnBack.setOnClickListener(v -> {
            getActivity().finish();
        });

        return view;
    }


    private void sendBookingToServer(String method) {
        String baseUrl = "https://6851a3e58612b47a2c0ad35e.mockapi.io/getInfo";

        String total = tongTien.toString();

        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();
        urlBuilder.addQueryParameter("userId", userId);
        urlBuilder.addQueryParameter("tongTien", total);

        String url = urlBuilder.build().toString();

        JSONObject data = new JSONObject();
        try {
            data.put("userId", userId);
            data.put("ticketId", ticketId);
            data.put("userName", userInfo.getFullname());
            data.put("phone", userInfo.getPhone());
            data.put("email", userInfo.getEmail());
            data.put("selectedSeats", new JSONArray(selectedSeats));
            data.put("paymentMethod", method);
            data.put("departureDate", ngaydi);
            data.put("totalPrice", total);

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
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Gửi thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject result = new JSONObject(body);
                            String paymentUrl = result.getString("paymentUrl");

                            openPaymentGateway(paymentUrl);

                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi từ server!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void openPaymentGateway(String paymentUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(intent);
    }

}
