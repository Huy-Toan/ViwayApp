package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.test.CustomSpinner;
import com.example.test.MainActivity;
import com.example.test.R;
import com.example.test.TicketTripActivity;
import com.example.test.adapter.DateAdapter;
import com.example.test.adapter.TicketAdapter;
import com.example.test.item.DateItem;
import com.example.test.response.TicketResponse;

import java.net.URLEncoder;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TicketTrip extends Fragment implements DateAdapter.OnDateClickListener{

    private RecyclerView dateRecyclerView;
    private DateAdapter dateAdapter;
    private List<DateItem> dateList;
    private Handler handler;
    private Runnable updateTimeRunnable;
    private TicketAdapter ticketAdapter;
    private Integer selectedGiaVeIndex= 0;
    private Integer selectedLoaiGheIndex = 0;
    private Integer selectedGioDiIndex = 0;

    private String DiemDi, DiemDen, SoLuongVe;
    private Calendar calendarNgayDi,calendarNgayVe;
    private TextView topDiemDi, topDiemDen,currentDateHeader;
    private Boolean isKhuHoi;
    private ImageButton btnBack;

    Spinner spinnerGiaVe, spinnerLoaiGhe, spinnerGioDi;
    List<TicketResponse> orginalTicketList = new ArrayList<>();
    List<TicketResponse> filterTicketList = new ArrayList<>();

    RecyclerView recyclerView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ticket_result, container, false);

        btnBack = view.findViewById(R.id.btn_ticket_back);
        currentDateHeader = view.findViewById(R.id.time_ticket_go);
        dateRecyclerView = view.findViewById(R.id.dateRecyclerView);
        topDiemDi = view.findViewById(R.id.ticket_current_location);
        topDiemDen = view.findViewById(R.id.ticket_target_location);
        dateList = new ArrayList<>();
        dateAdapter = new DateAdapter(dateList);
        dateAdapter.setOnDateClickListener(this);

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
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
                ngayDiFormatted = sdf.format(calendarNgayDi.getTime());
            }

            String ngayVeFormatted = "N/A";
            if (isKhuHoi && calendarNgayVe != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
                ngayVeFormatted = sdf.format(calendarNgayVe.getTime());
            }

            topDiemDi.setText(DiemDi);
            topDiemDen.setText(DiemDen);
            currentDateHeader.setText(ngayDiFormatted);

        }

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateAdapter);

        RecyclerView ticketRecyclerView = view.findViewById(R.id.item_Ticket);
        ticketRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<TicketResponse> ticketList = new ArrayList<>();
        ticketAdapter = new TicketAdapter(ticketList);
        orginalTicketList = ticketList;
        filterTicketList = new ArrayList<>(ticketList);
        ticketRecyclerView.setAdapter(ticketAdapter);

        spinnerGiaVe = view.findViewById(R.id.spinnerGiaVe);
        spinnerLoaiGhe = view.findViewById(R.id.spinnerLoaiGhe);
        spinnerGioDi = view.findViewById(R.id.spinnerGioDi);

        String[] itemGiaVe = {"Tất cả", "Dưới 200K", "200K - 500K", "Trên 500K"};
        String[] itemLoaiGhe = {"Tất cả", "Ghế", "Limousine", "Giường"};
        String[] itemGioDi = {"Tất cả", "Buổi sáng", "Buổi chiều", "Buổi tối"};

        List<String> itemGiaVeList = Arrays.asList(itemGiaVe);
        List<String> itemLoaiGheList = Arrays.asList(itemLoaiGhe);
        List<String> itemGioDiList = Arrays.asList(itemGioDi);

        CustomSpinner adapterGiaVe = new CustomSpinner(
                requireContext(),
                R.layout.spinner_layout,
                itemGiaVeList,
                "Giá",
                selectedGiaVeIndex
        );
        adapterGiaVe.setShowSelectedText(false);
        spinnerGiaVe.setAdapter(adapterGiaVe);
        spinnerGiaVe.setDropDownVerticalOffset(130);

        spinnerGiaVe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGiaVeIndex = position;
                adapterGiaVe.setSelectedIndex(position);
                applyFilter();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        CustomSpinner adapterLoaiGhe = new CustomSpinner(
                requireContext(),
                R.layout.spinner_layout,
                itemLoaiGheList,
                "Loại ghế",
                selectedLoaiGheIndex
        );
        adapterLoaiGhe.setShowSelectedText(false);
        spinnerLoaiGhe.setAdapter(adapterLoaiGhe);
        spinnerLoaiGhe.setDropDownVerticalOffset(130);

        spinnerLoaiGhe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLoaiGheIndex = position;
                adapterLoaiGhe.setSelectedIndex(position);
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        CustomSpinner adapterGioDi = new CustomSpinner(
                requireContext(),
                R.layout.spinner_layout,
                itemGioDiList,
                "Giờ",
                selectedGioDiIndex
        );
        adapterGioDi.setShowSelectedText(false);
        spinnerGioDi.setAdapter(adapterGioDi);
        spinnerGioDi.setDropDownVerticalOffset(130);

        spinnerGioDi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGioDiIndex = position;
                adapterGioDi.setSelectedIndex(position);
                applyFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return view;
    }

    @Override
    public void onDateClick(DateItem dateItem) {
        calendarNgayDi = dateItem.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault());
        currentDateHeader.setText(sdf.format(calendarNgayDi.getTime()));
        sendDataToServer();
    }

    private void sendDataToServer() {
        String diemDi = DiemDi;
        String diemDen = DiemDen;
        String soLuongVe = SoLuongVe;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String ngayDiFormatted = sdf.format(calendarNgayDi.getTime());

        // Thay đường dẫn này thành link đến server
        String baseUrl = "https://684c3de1ed2578be881e322c.mockapi.io/getTicket";
        String url = baseUrl + "?" +
                "DiemDi=" + diemDi +
                "&DiemDen=" + diemDen +
                "&NgayDi=" + ngayDiFormatted;

        String ngayVeFormatted = "";
        if (isKhuHoi && calendarNgayVe != null) {
            ngayVeFormatted = sdf.format(calendarNgayVe.getTime());
            url += "&NgayVe=" + ngayVeFormatted;
        }

        Log.d("MockServerURL", "URL gọi đến: " + url);
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<TicketResponse> newTicketList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            TicketResponse ticket = new TicketResponse(
                                    obj.getString("ThoiGianDi"),
                                    obj.getString("ThoiGianDen"),
                                    obj.getString("GiaVe"),
                                    obj.getString("LoaiGhe"),
                                    obj.getString("SoLuongGhe"),
                                    obj.getString("DiemDi"),
                                    obj.getString("KhoangCach"),
                                    obj.getString("DiemDen")
                            );
                            newTicketList.add(ticket);
                        }

                        // Cập nhật RecyclerView
                        orginalTicketList.clear();
                        orginalTicketList.addAll(newTicketList);
                        applyFilter();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Lỗi phân tích dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                }
        );


        queue.add(request);
    }


    private List<TicketResponse> generateMockTicketsForDate(Calendar date) {
        List<TicketResponse> mockList = new ArrayList<>();

        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH);
        int year = date.get(Calendar.YEAR);

        return mockList;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 60 * 1000);
            }
        };

        updateDateDisplay();
        sendDataToServer();
        handler.post(updateTimeRunnable);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null && updateTimeRunnable != null) {
            handler.removeCallbacks(updateTimeRunnable);
        }
    }

    private void applyFilter() {
        String giaVeFilter = spinnerGiaVe.getSelectedItem().toString();
        String loaiGheFilter = spinnerLoaiGhe.getSelectedItem().toString();
        String gioDiFilter = spinnerGioDi.getSelectedItem().toString();

        filterTicketList.clear();

        for (TicketResponse ticket : orginalTicketList) {
            boolean matches = true;

            // Lọc giá vé
            int giaVe = Integer.parseInt(ticket.getGiaVe().replace(".", "").replace("đ", "").trim());
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


    private String normalizeString(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", ""); // Xóa dấu
        normalized = normalized.replaceAll("\\s+", "");
        normalized = normalized.replaceAll("Đ", "d");
        return normalized.toLowerCase();
    }


}
