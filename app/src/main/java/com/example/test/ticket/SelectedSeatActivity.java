package com.example.test.ticket;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.test.R;
import com.example.test.fragment.SelectedSeatFragment;
import com.example.test.response.TicketResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SelectedSeatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_seat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.select_seat), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Bundle bundle = new Bundle();

            if (intent != null) {
                long ngayDiMillis = intent.getLongExtra("ngayDiHeader", -1);
                TicketResponse ticket = (TicketResponse) intent.getSerializableExtra("ticket");

                String ngayDiFormatted = "";
                if (ngayDiMillis != -1) {
                    Date date = new Date(ngayDiMillis);
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd-MM-yyyy", new Locale("vi", "VN"));
                    ngayDiFormatted = sdf.format(date);
                }

                bundle.putString("ngayDiHeader", ngayDiFormatted);
                bundle.putSerializable("ticket", ticket);
            }

            SelectedSeatFragment selectedSeatFragment = new SelectedSeatFragment();
            selectedSeatFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_seat, selectedSeatFragment)
                    .commit();
        }
    }
}
