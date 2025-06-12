package com.example.test.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.test.R;
import com.example.test.response.TicketHistoryResponse;
import com.example.test.adapter.TicketHistoryAdapter;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TicketHistoryAdapter adapter;
    private List<TicketHistoryResponse> ticketHistoryList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.item_History);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ticketHistoryList = new ArrayList<>();
        ticketHistoryList.add(new TicketHistoryResponse(
                "VN12345",
                "TP. Hồ Chí Minh",
                "Hà Nội",
                "A1",
                "2025-06-01 07:00"
        ));
        ticketHistoryList.add(new TicketHistoryResponse(
                "VN67890",
                "Đà Nẵng",
                "Huế",
                "B12",
                "2025-06-05 14:30"
        ));
        ticketHistoryList.add(new TicketHistoryResponse(
                "DL11223",
                "Đà Lạt",
                "Nha Trang",
                "C5",
                "2025-06-10 09:00"
        ));
        ticketHistoryList.add(new TicketHistoryResponse(
                "PQ44556",
                "Cần Thơ",
                "Phú Quốc",
                "D8",
                "2025-06-15 11:15"
        ));
        ticketHistoryList.add(new TicketHistoryResponse(
                "SG99887",
                "Hải Phòng",
                "TP. Hồ Chí Minh",
                "E2",
                "2025-06-20 18:00"
        ));
        ticketHistoryList.add(new TicketHistoryResponse(
                "ND12345",
                "Nam Định",
                "Thái Bình",
                "F3",
                "2025-06-25 10:00"
        ));
        ticketHistoryList.add(new TicketHistoryResponse(
                "LA67890",
                "Long An",
                "Tiền Giang",
                "G7",
                "2025-06-30 13:00"
        ));

        adapter = new TicketHistoryAdapter(ticketHistoryList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}