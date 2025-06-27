package com.example.test.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.CustomSpinner;
import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.SelectedSeatActivity;
import com.example.test.adapter.DateAdapter;
import com.example.test.adapter.TicketAdapter;
import com.example.test.config.Config;
import com.example.test.item.DateItem;
import com.example.test.response.TicketResponse;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;

public class TicketTripFragment extends Fragment implements DateAdapter.OnDateClickListener{

    private RecyclerView dateRecyclerView;
    private DateAdapter dateAdapter;
    private List<DateItem> dateList;
    private TicketAdapter ticketAdapter;
    private Integer selectedGiaVeIndex= 0, selectedLoaiGheIndex = 0, selectedGioDiIndex = 0;
    private String DiemDi, DiemDen, SoLuongVe, token;
    private Calendar calendarNgayDi,calendarNgayVe;
    private Long ngayDiMillis;
    private TextView topDiemDi, topDiemDen,currentDateHeader;
    private LinearLayout textNoData;
    private Boolean isKhuHoi;
    private ImageButton btnBack;
    private Spinner spinnerGiaVe, spinnerLoaiGhe, spinnerGioDi;
    List<TicketResponse> orginalTicketList = new ArrayList<>();
    List<TicketResponse> filterTicketList = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_trip, container, false);

        btnBack = view.findViewById(R.id.TicketResult_btnBack);
        currentDateHeader = view.findViewById(R.id.TicketResult_ThoiGianHeader);
        dateRecyclerView = view.findViewById(R.id.dateRecyclerView);
        topDiemDi = view.findViewById(R.id.TicketResult_DiemDiHeader);
        topDiemDen = view.findViewById(R.id.TicketResult_DiemDenHeader);
        textNoData = view.findViewById(R.id.TicketResult_noData);

        // -------------------Dữ liệu gửi qua--------------------//
        Bundle arguments = getArguments();
        if(arguments != null){
            DiemDi = arguments.getString("KEY_DIEM_DI");
            DiemDen = arguments.getString("KEY_DIEM_DEN");
            Long ngayDiMillis = arguments.getLong("KEY_NGAY_DI_MILLIS", -1L);
            Long ngayVeMillis = arguments.getLong("KEY_NGAY_VE_MILLIS", -1L);
            SoLuongVe = arguments.getString("KEY_SO_LUONG_VE");

            if (ngayVeMillis != -1L) {
                isKhuHoi = true;
                calendarNgayVe = Calendar.getInstance();
                calendarNgayVe.setTimeInMillis(ngayVeMillis);
            } else {
                isKhuHoi = false;
            }

            String ngayDiFormatted = "N/A";
            if (ngayDiMillis != -1L) {
                calendarNgayDi = Calendar.getInstance();
                calendarNgayDi.setTimeInMillis(ngayDiMillis);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
                ngayDiFormatted = sdf.format(calendarNgayDi.getTime());
            }

            String ngayVeFormatted = "N/A";
            if (isKhuHoi && calendarNgayVe != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
                ngayVeFormatted = sdf.format(calendarNgayVe.getTime());
            }
            topDiemDi.setText(DiemDi);
            topDiemDen.setText(DiemDen);
            currentDateHeader.setText(ngayDiFormatted);
        }
        // -------------------------------------------- //

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("VIWAY", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");


        // ------------------ Hiển thị vé và thanh ngày đi----------------------//
        dateList = new ArrayList<>();
        dateAdapter = new DateAdapter(dateList, token);
        dateAdapter.setOnDateClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateAdapter);

        RecyclerView ticketRecyclerView = view.findViewById(R.id.item_Ticket);
        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<TicketResponse> ticketList = new ArrayList<>();
        ticketAdapter = new TicketAdapter(ticketList, ticket -> {
            Intent intent = new Intent(getContext(), SelectedSeatActivity.class);
            intent.putExtra("ticket", ticket);
            intent.putExtra("ngayDiHeader", ngayDiMillis);
            startActivity(intent);
        });
        orginalTicketList = ticketList;
        filterTicketList = new ArrayList<>(ticketList);
        ticketRecyclerView.setAdapter(ticketAdapter);
        //----------------------------------------------------//



        // ----------------------- Lọc vé --------------------------- //
        spinnerGiaVe = view.findViewById(R.id.spinnerGiaVe);
        spinnerLoaiGhe = view.findViewById(R.id.spinnerLoaiGhe);
        spinnerGioDi = view.findViewById(R.id.spinnerGioDi);

        List<String> itemGiaVeList = Arrays.asList("Tất cả", "Dưới 200K", "200K - 500K", "Trên 500K");
        List<String> itemLoaiGheList = Arrays.asList("Tất cả", "Ghế", "Limousine", "Giường");
        List<String> itemGioDiList = Arrays.asList("Tất cả", "Buổi sáng", "Buổi chiều", "Buổi tối");

        setupCustomSpinner(spinnerGiaVe, itemGiaVeList, "Giá", selectedGiaVeIndex, pos -> selectedGiaVeIndex = pos);
        setupCustomSpinner(spinnerLoaiGhe, itemLoaiGheList, "Loại ghế", selectedLoaiGheIndex, pos -> selectedLoaiGheIndex = pos);
        setupCustomSpinner(spinnerGioDi, itemGioDiList, "Giờ", selectedGioDiIndex, pos -> selectedGioDiIndex = pos);
        // --------------------------------------------------------//


        // ----------------- Nút quay lại -------------------------//
        btnBack.setOnClickListener(v -> {
            getActivity().finish();
        });
        //---------------------------------------------------------//

        // ------------Gửi yêu cầu lên server--------------//
        updateDateDisplay();
        sendDataToServer(token);
        //--------------------------------------------------//

        return view;
    }

    private interface OnSpinnerItemSelected {
        void onItemSelected(int position);
    }

    private void setupCustomSpinner(
            Spinner spinner,
            List<String> items,
            String hint,
            int selectedIndex,
            OnSpinnerItemSelected listener
    ) {
        CustomSpinner adapter = new CustomSpinner(
                requireContext(),
                R.layout.spinner_layout,
                items,
                hint,
                selectedIndex
        );
        adapter.setShowSelectedText(false);
        spinner.setAdapter(adapter);
        spinner.setDropDownVerticalOffset(130);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                listener.onItemSelected(position);
                adapter.setSelectedIndex(position);
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    @Override
    public void onDateClick(DateItem dateItem, String token) {
        calendarNgayDi = dateItem.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
        currentDateHeader.setText(sdf.format(calendarNgayDi.getTime()));
        sendDataToServer(token);
    }

    private void sendDataToServer(String token) {
        String baseUrl = Config.BASE_URL+"/trip/search-trips";
        OkHttpClient client = new OkHttpClient();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", new Locale("vi", "VN"));
        String ngayDiFormatted = sdf.format(calendarNgayDi.getTime());
        ngayDiMillis = calendarNgayDi.getTimeInMillis();

        JSONObject data = new JSONObject();
        try{
            data.put("origin", DiemDi);
            data.put("destination", DiemDen);
            data.put("departure_date", ngayDiFormatted);

            if (isKhuHoi && calendarNgayVe != null) {
                String ngayVeFormatted = sdf.format(calendarNgayVe.getTime());
                data.put("return_date", ngayVeFormatted);
                data.put("is_round_trip", true);
            } else {
                data.put("return_date", JSONObject.NULL);
                data.put("is_round_trip", false);
            }

            data.put("passengers", SoLuongVe);

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }


        RequestBody requestBody = RequestBody.create(
                data.toString(),
                MediaType.parse("application/json")
        );

        Log.d("Url", baseUrl);
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer "+ token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
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

                    Log.d("Data send to server", responseBody);
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        List<TicketResponse> newTicketList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            TicketResponse ticket = new TicketResponse(
                                    obj.getInt("id"),
                                    obj.getString("time_start"),
                                    obj.getString("time_end"),
                                    obj.getInt("price"),
                                    obj.getString("vehicle_kind"),
                                    obj.getInt("available_seats"),
                                    obj.getString("start_location"),
                                    obj.getInt("distance"),
                                    obj.getString("estimated_duration"),
                                    obj.getString("end_location")
                            );
                            newTicketList.add(ticket);
                        }

                        requireActivity().runOnUiThread(() -> {
                            orginalTicketList.clear();
                            orginalTicketList.addAll(newTicketList);

                            if (newTicketList.isEmpty()) {
                                textNoData.setVisibility(View.VISIBLE);
                            } else {
                                textNoData.setVisibility(View.GONE);
                                applyFilter();
                            }

                            ticketAdapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    requireActivity().runOnUiThread(() -> {
                        if (response.code() == 400) {
                            textNoData.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(getContext(), "Lỗi phản hồi từ server", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateDateDisplay() {
        Calendar today = Calendar.getInstance();
        dateList.clear();

        for (int i = 0; i <= 60; i++) {
            Calendar date = (Calendar) today.clone();
            date.add(Calendar.DATE, i);
            dateList.add(new DateItem(date));
        }

        int positionToScrollTo = 0;

        if (calendarNgayDi != null) {
            for (int i = 0; i < dateList.size(); i++) {
                DateItem item = dateList.get(i);
                if (item.getDate().get(Calendar.YEAR) == calendarNgayDi.get(Calendar.YEAR) &&
                        item.getDate().get(Calendar.MONTH) == calendarNgayDi.get(Calendar.MONTH) &&
                        item.getDate().get(Calendar.DAY_OF_MONTH) == calendarNgayDi.get(Calendar.DAY_OF_MONTH))
                {
                    positionToScrollTo = i;
                    //item.setSelected(true);
                    break;
                }
            }
        } else {
            //dateList.get(0).setSelected(true);
            positionToScrollTo = 0;
        }

        dateAdapter.notifyDataSetChanged();
        dateAdapter.setSelectedPosition(positionToScrollTo);

        int finalScrollPosition = Math.max(0, positionToScrollTo - 2);
        dateRecyclerView.scrollToPosition(finalScrollPosition);
    }

    private void applyFilter() {
        String giaVeFilter = spinnerGiaVe.getSelectedItem().toString();
        String loaiGheFilter = spinnerLoaiGhe.getSelectedItem().toString();
        String gioDiFilter = spinnerGioDi.getSelectedItem().toString();

        filterTicketList.clear();

        for (TicketResponse ticket : orginalTicketList) {
            boolean matches = true;

            // Lọc giá vé
            int giaVe = ticket.getGiaVe();
            if (giaVeFilter.equals("Dưới 200K") && giaVe >= 200000) matches = false;
            if (giaVeFilter.equals("200K - 500K") && (giaVe < 200000 || giaVe > 500000)) matches = false;
            if (giaVeFilter.equals("Trên 500K") && giaVe <= 500000) matches = false;

            // Lọc loại ghế
            if (!loaiGheFilter.equals("Tất cả") && !ticket.getLoaiGhe().equalsIgnoreCase(loaiGheFilter)) {
                matches = false;
            }

            // Lọc giờ đi
            String gio = ticket.getGioDi();
            int hour = Integer.parseInt(gio.split(":")[0]);
            if (gioDiFilter.equals("Buổi sáng") && !(hour >= 5 && hour < 12)) matches = false;
            if (gioDiFilter.equals("Buổi chiều") && !(hour >= 12 && hour < 18)) matches = false;
            if (gioDiFilter.equals("Buổi tối") && !(hour >= 18 || hour < 5)) matches = false;

            if (matches) {
                filterTicketList.add(ticket);
            }
        }
        ticketAdapter.updateList(filterTicketList);
    }

}
