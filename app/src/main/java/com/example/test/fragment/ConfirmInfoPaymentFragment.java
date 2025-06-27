package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    private Integer tongTien = 0, ticketId, userId, codeTicket;
    private String ngaydi, token, diemDon;
    private Boolean trungChuyen = false;
    private ImageButton btnBack;
    private Button btnThanhToan;
    private TicketResponse ticket;
    private UserInfoResponse userInfo;
    private ArrayList<String> selectedSeats;
    private CountDownTimer countDownTimer;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_info_payment, container, false);

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
            diemDon = bundle.getString("diemDon");
            trungChuyen = bundle.getBoolean("trungChuyen", false);

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

                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                String giaVeFormatted = formatter.format(tongTien) + " VNĐ";

                giaVe.setText(giaVeFormatted);
                phiThanhToan.setText("0đ");
                tongThanhToan.setText(giaVeFormatted);
            }

            Log.d("Điểm đón", diemDon);
        }


        SharedPreferences sharedPreferences = getContext().getSharedPreferences("VIWAY", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", 0);
        token = sharedPreferences.getString("token", "");

        sendBookingInfoToServer(token);
        startCountdownTimer();


        btnThanhToan.setOnClickListener(v -> {
            SheetPaymentBottom sheetPaymentBottom = new SheetPaymentBottom(method -> {
                sendBookingToServer(method, token);
            });

            sheetPaymentBottom.show(getParentFragmentManager(), "PaymentSheet");
        });


        btnBack.setOnClickListener(v -> {
            getActivity().finish();
        });

        return view;
    }


    private void sendBookingInfoToServer(String token) {
        String baseUrl = Config.BASE_URL+ "/ticket";

        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try {
            data.put("trip_id", ticketId);
            data.put("user_id", userId);
            data.put("full_name", userInfo.getFullname());
            data.put("phone_number", userInfo.getPhone());
            data.put("email", userInfo.getEmail());
            data.put("seat_code", new JSONArray(selectedSeats));
            data.put("pick_up_point", diemDon);
            data.put("require_shuttle", trungChuyen);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody requestBody = RequestBody.create(
                data.toString(),
                MediaType.parse("application/json")
        );


        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
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
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    String body = response.body().string();

                    requireActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject result = new JSONObject(body);
                                int idResponse = result.getInt("id");
                                codeTicket = idResponse;
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), "Lỗi JSON từ server", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Lỗi từ server!", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show()
                    );
                }
            }

        });
    }

    private void sendBookingToServer(String method, String token) {
        String url = Config.BASE_URL + "/ticket/payment";

        OkHttpClient client = new OkHttpClient();

        JSONObject data = new JSONObject();
        try {
            data.put("ticket_id", codeTicket);
            data.put("payment_method", method);
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
                .addHeader("Authorization", "Bearer " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Gửi phương thức thất bại", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(getContext(), "Lỗi JSON từ server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Thanh toán thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void openPaymentGateway(String paymentUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(intent);
    }

    private void startCountdownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(600000, 1000) { // 600000 ms = 10 phút
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                String time = String.format("%02d:%02d", minutes, seconds);
                thoiGianGiuCho.setText("Thời gian giữ vé còn lại: "+ time);
            }

            @Override
            public void onFinish() {
                thoiGianGiuCho.setText("Thời gian giu vé còn lại: 00:00");
            }
        };

        countDownTimer.start();
    }


}
