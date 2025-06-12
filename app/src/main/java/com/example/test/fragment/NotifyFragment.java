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

import com.example.test.response.NotifyResponse;
import com.example.test.R;
import com.example.test.adapter.NotifyAdapter;

public class NotifyFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotifyAdapter adapterNotify;
    private List<NotifyResponse> notifyResponseList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notify, container, false);

        recyclerView = view.findViewById(R.id.item_Notify);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notifyResponseList = new ArrayList<>();
        notifyResponseList.add(new NotifyResponse(
                "Thông báo 1",
                "09:00 01/06/2025",
                "Xe sẽ khởi hành đúng giờ, vui lòng có mặt trước 15 phút."
        ));
        notifyResponseList.add(new NotifyResponse(
                "Thông báo 2",
                "10:30 01/06/2025",
                "Chuyến xe đi Tây Sơn đã sẵn sàng."
        ));
        notifyResponseList.add(new NotifyResponse(
                "Thông báo 3",
                "11:00 01/06/2025",
                "Chuyến xe đã bị hoãn do điều kiện thời tiết."
        ));
        notifyResponseList.add(new NotifyResponse(
                "Thông báo 4",
                "13:45 01/06/2025",
                "Đừng quên mang theo vé điện tử khi lên xe."
        ));
        notifyResponseList.add(new NotifyResponse(
                "Thông báo 5",
                "15:15 01/06/2025",
                "Hệ thống đã cập nhật tuyến đường mới."
        ));

        adapterNotify = new NotifyAdapter(notifyResponseList);
        recyclerView.setAdapter(adapterNotify);

        return view;
    }
}
