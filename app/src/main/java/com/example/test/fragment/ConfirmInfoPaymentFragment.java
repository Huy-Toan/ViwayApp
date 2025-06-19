package com.example.test.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.test.R;
import com.example.test.response.TicketResponse;
import com.example.test.response.UserInfoResponse;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ConfirmInfoPaymentFragment extends Fragment {

    private TextView diemDiHeader, diemDenHeader, thoiGianHeader,
                        tuyenXe, thoiGianKhoiHanh, soLuongVe, viTriGhe, diemLenXe,
                        thoiGianTrungChuyen, diemXuongXe, fullname, sdt, email, thoiGianGiuCho,
                        giaVe, phiThanhToan, tongThanhToan;

    private Integer tongTien = 0;
    private String ngaydi, ticketId = null;
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
            }



            Log.d("SelectedSeatFragment", "TicketID: " + ticketId);
        }






        btnBack.setOnClickListener(v -> {
            getActivity().finish();
        });



        return view;
    }

}
