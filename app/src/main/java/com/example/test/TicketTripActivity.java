package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;

import com.example.test.fragment.TicketTripFragment;

public class TicketTripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ticket_trip);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ticket_Result), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle bundle = new Bundle();

            if(intent != null){
                String diemDi = intent.getStringExtra("DIEM_DI");
                String diemDen = intent.getStringExtra("DIEM_DEN");
                Long ngayDiMillis = intent.getLongExtra("NGAY_DI", -1L);
                Long ngayVeMillis = intent.getLongExtra("NGAY_VE", -1L);
                String soLuongVe = intent.getStringExtra("SO_LUONG_VE");

                bundle.putString("KEY_DIEM_DI", diemDi);
                bundle.putString("KEY_DIEM_DEN", diemDen);
                bundle.putLong("KEY_NGAY_DI_MILLIS", ngayDiMillis);
                bundle.putString("KEY_SO_LUONG_VE", soLuongVe);

                if (ngayVeMillis != -1L) {
                    bundle.putLong("KEY_NGAY_VE_MILLIS", ngayVeMillis);
                }

            }

            TicketTripFragment ticketTripFragment = new TicketTripFragment();
            ticketTripFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ticket_Result, ticketTripFragment)
                    .commit();
        }
    }
}