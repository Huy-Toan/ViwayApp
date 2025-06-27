package com.example.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.fragment.InforPaymentFragment;
import com.example.test.response.TicketResponse;

import java.util.ArrayList;

public class InforPaymentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_information_payment);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.infor_payment), (v, insets) -> {
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

                bundle.putString("ngayDiHeader", ngayDi);
                bundle.putSerializable("ticket", ticket);
                bundle.putStringArrayList("selectedSeats", selectedList);
            }

            InforPaymentFragment inforPaymentFragment = new InforPaymentFragment();
            inforPaymentFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.infor_payment, inforPaymentFragment)
                    .commit();
        }
    }
}
