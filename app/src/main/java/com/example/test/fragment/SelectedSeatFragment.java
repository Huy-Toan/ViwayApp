package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;


import androidx.annotation.NonNull;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.test.InforPaymentActivity;
import com.example.test.R;
import com.example.test.SelectedSeatActivity;
import com.example.test.TicketTripActivity;
import com.example.test.response.SeatStatusResponse;
import com.example.test.response.TicketResponse;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SelectedSeatFragment extends Fragment {

    private TextView diemDiHeader, diemDenHeader, ngayDiHeader, gioDi, ngayDi, soLuongVe, giaTien;
    private Button btnNext;
    private ImageButton btnBack;
    private Button btnA1,btnA2,btnA3,btnA4,btnA5,btnA6,btnA7,btnA8,btnA9,btnA10,btnA11,btnA12,btnA13,btnA15,btnA16,btnA17;
    private Button btnB1,btnB2,btnB3,btnB4,btnB5,btnB6,btnB7,btnB8,btnB9,btnB10,btnB11,btnB12,btnB13,btnB14,btnB15,btnB16,btnB17;
    private Integer giaVe = 0, tongTien = 0;
    private String ngaydi, ticketId = null;
    private TicketResponse ticket;
    Map<String, Button> seatButtonsMap = new HashMap<>();
    Set<String> selectedSeats = new HashSet<>();

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.select_chair, container, false);

        diemDiHeader = view.findViewById(R.id.chonGhe_diemDi);
        diemDenHeader = view.findViewById(R.id.chonGhe_diemDen);
        ngayDiHeader = view.findViewById(R.id.chonGhe_thoiGianDi);
        gioDi = view.findViewById(R.id.chonGhe_gioLenXe);
        ngayDi = view.findViewById(R.id.chonGhe_ngayXuatBen);
        soLuongVe = view.findViewById(R.id.chonGhe_SoLuongGhe);
        giaTien = view.findViewById(R.id.chonGhe_TongTien);

        btnBack = view.findViewById(R.id.btn_chonGhe_back);
        btnNext = view.findViewById(R.id.btn_chonGhe_TiepTuc);

        btnA1 = view.findViewById(R.id.btnA01);
        btnA2 = view.findViewById(R.id.btnA02);
        btnA3 = view.findViewById(R.id.btnA03);
        btnA4 = view.findViewById(R.id.btnA04);
        btnA5 = view.findViewById(R.id.btnA05);
        btnA6 = view.findViewById(R.id.btnA06);
        btnA7 = view.findViewById(R.id.btnA07);
        btnA8 = view.findViewById(R.id.btnA08);
        btnA9 = view.findViewById(R.id.btnA09);
        btnA10 = view.findViewById(R.id.btnA10);
        btnA11 = view.findViewById(R.id.btnA11);
        btnA12 = view.findViewById(R.id.btnA12);
        btnA13 = view.findViewById(R.id.btnA13);
        btnA15 = view.findViewById(R.id.btnA15);
        btnA16 = view.findViewById(R.id.btnA16);
        btnA17 = view.findViewById(R.id.btnA17);

        btnB1 = view.findViewById(R.id.btnB01);
        btnB2 = view.findViewById(R.id.btnB02);
        btnB3 = view.findViewById(R.id.btnB03);
        btnB4 = view.findViewById(R.id.btnB04);
        btnB5 = view.findViewById(R.id.btnB05);
        btnB6 = view.findViewById(R.id.btnB06);
        btnB7 = view.findViewById(R.id.btnB07);
        btnB8 = view.findViewById(R.id.btnB08);
        btnB9 = view.findViewById(R.id.btnB09);
        btnB10 = view.findViewById(R.id.btnB10);
        btnB11 = view.findViewById(R.id.btnB11);
        btnB12 = view.findViewById(R.id.btnB12);
        btnB13 = view.findViewById(R.id.btnB13);
        btnB14 = view.findViewById(R.id.btnB14);
        btnB15 = view.findViewById(R.id.btnB15);
        btnB16 = view.findViewById(R.id.btnB16);
        btnB17 = view.findViewById(R.id.btnB17);

        initSeatButtonsMap();

        Bundle bundle = getArguments();
        if (bundle != null) {
            ngaydi = bundle.getString("ngayDiHeader");
            ticket = (TicketResponse) bundle.getSerializable("ticket");

            if (ticket != null && ngaydi != null) {
                diemDiHeader.setText(ticket.getDiemDi());
                diemDenHeader.setText(ticket.getDiemDen());
                ngayDiHeader.setText(ngaydi);
                gioDi.setText(ticket.getGioDi());
                ngayDi.setText(ngaydi);
                giaVe = Integer.parseInt(ticket.getGiaVe());
                ticketId = ticket.getTicketId();
            }

            Log.d("SelectedSeatFragment", "TicketID: " + ticketId);
        }

        fetchSeatStatus(ticketId);

        btnBack.setOnClickListener(v -> {
            getActivity().finish();
        });

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), InforPaymentActivity.class);
            intent.putExtra("ticket", ticket);
            intent.putExtra("ngayDiHeader", ngaydi);
            ArrayList<String> selectedList = new ArrayList<>(selectedSeats);
            intent.putStringArrayListExtra("selectedSeats", selectedList);
            startActivity(intent);
        });

     return view;
    }

    private void fetchSeatStatus (String ticketId) {
        String baseUrl = "https://6851a3e58612b47a2c0ad35e.mockapi.io/getStatus";
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();
        urlBuilder.addQueryParameter("ticketID", ticketId);

        String url = urlBuilder.build().toString();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
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
                        JSONArray jsonArray = new JSONArray(responseBody);
                        if (jsonArray.length() > 0) {
                            JSONObject ticketObj = jsonArray.getJSONObject(0);
                            JSONArray seatsArray = ticketObj.getJSONArray("Seats");

                            List<SeatStatusResponse> seatStatusList = new ArrayList<>();

                            for (int i = 0; i < seatsArray.length(); i++) {
                                JSONObject seatObj = seatsArray.getJSONObject(i);
                                String seatId = seatObj.getString("SeatID");
                                String status = seatObj.getString("Status");

                                seatStatusList.add(new SeatStatusResponse(seatId, status));
                            }

                            requireActivity().runOnUiThread(() -> {
                                updateSeatButtons(seatStatusList);
                            });
                        }
                    } catch (JSONException e) {
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


    private void updateSeatButtons(List<SeatStatusResponse> seats) {
        for (SeatStatusResponse seat : seats) {
            String id = seat.getSeatId();
            String status = seat.getStatus();

            Button seatButton = seatButtonsMap.get(id);

            if (seatButton != null) {
                if (status.equals("available")) {
                    seatButton.setBackgroundColor(Color.parseColor("#99FFFF"));
                    seatButton.setEnabled(true);

                    seatButton.setOnClickListener(v -> {
                        if (selectedSeats.contains(id)) {
                            seatButton.setBackgroundColor(Color.parseColor("#99FFFF"));
                            selectedSeats.remove(id);
                        } else {
                            seatButton.setBackgroundColor(Color.parseColor("#FFD700"));
                            selectedSeats.add(id);
                        }

                        int soLuong = selectedSeats.size();
                        tongTien = soLuong * giaVe;
                        soLuongVe.setText(TextUtils.join(", ", selectedSeats));
                        giaTien.setText(String.format("%,d VNĐ", tongTien));
                    });
                } else if (status.equals("booked")) {
                    seatButton.setBackgroundColor(Color.parseColor("#888888"));
                    seatButton.setTextColor(Color.WHITE);
                    seatButton.setEnabled(false);
                }
            }
        }
    }

    private void initSeatButtonsMap() {
        seatButtonsMap.put("A01", btnA1);
        seatButtonsMap.put("A02", btnA2);
        seatButtonsMap.put("A03", btnA3);
        seatButtonsMap.put("A04", btnA4);
        seatButtonsMap.put("A05", btnA5);
        seatButtonsMap.put("A06", btnA6);
        seatButtonsMap.put("A07", btnA7);
        seatButtonsMap.put("A08", btnA8);
        seatButtonsMap.put("A09", btnA9);
        seatButtonsMap.put("A10", btnA10);
        seatButtonsMap.put("A11", btnA11);
        seatButtonsMap.put("A12", btnA12);
        seatButtonsMap.put("A13", btnA13);
        seatButtonsMap.put("A15", btnA15);
        seatButtonsMap.put("A16", btnA16);
        seatButtonsMap.put("A17", btnA17);

        seatButtonsMap.put("B01", btnB1);
        seatButtonsMap.put("B02", btnB2);
        seatButtonsMap.put("B03", btnB3);
        seatButtonsMap.put("B04", btnB4);
        seatButtonsMap.put("B05", btnB5);
        seatButtonsMap.put("B06", btnB6);
        seatButtonsMap.put("B07", btnB7);
        seatButtonsMap.put("B08", btnB8);
        seatButtonsMap.put("B09", btnB9);
        seatButtonsMap.put("B10", btnB10);
        seatButtonsMap.put("B11", btnB11);
        seatButtonsMap.put("B12", btnB12);
        seatButtonsMap.put("B13", btnB13);
        seatButtonsMap.put("B14", btnB14);
        seatButtonsMap.put("B15", btnB15);
        seatButtonsMap.put("B16", btnB16);
        seatButtonsMap.put("B17", btnB17);
    }

}