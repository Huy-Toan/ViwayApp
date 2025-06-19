package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.fragment.ConfirmInfoPaymentFragment;
import com.example.test.fragment.InforPaymentFragment;
import com.example.test.response.TicketResponse;
import com.example.test.response.UserInfoResponse;

import java.util.ArrayList;

public class ConfirmInfoPaymentActivity extends AppCompatActivity {


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_infor_payment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.confirm_info_payment), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle bundle = new Bundle();

            if (intent != null) {
                TicketResponse ticket = (TicketResponse) intent.getSerializableExtra("ticket");
                String ngayDi = intent.getStringExtra("ngayDiHeader");
                ArrayList<String> selectedList = intent.getStringArrayListExtra("selectedSeats");
                UserInfoResponse userInfo = (UserInfoResponse) getIntent().getSerializableExtra("userInfo");

                bundle.putString("ngayDiHeader", ngayDi);
                bundle.putSerializable("ticket", ticket);
                bundle.putStringArrayList("selectedSeats", selectedList);
                bundle.putSerializable("userInfo", userInfo);
            }

            ConfirmInfoPaymentFragment confirmInfoPaymentFragment = new ConfirmInfoPaymentFragment();
            confirmInfoPaymentFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.confirm_info_payment, confirmInfoPaymentFragment)
                    .commit();
        }
    }



}
