package com.example.test.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.response.TicketHistoryResponse;

import java.util.List;

public class TicketHistoryAdapter extends RecyclerView.Adapter<TicketHistoryAdapter.TicketViewHolder> {

    private List<TicketHistoryResponse> tiketList;
    public TicketHistoryAdapter(List<TicketHistoryResponse> tiketList){
        this.tiketList = tiketList;
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder{
        public TextView maVe;
        public TextView tuyenDuongDi;
        public TextView tuyenDuongDen;
        public TextView viTriGhe;
        public TextView gioXuatBen;
        public ImageButton hoTroButton;

        public TicketViewHolder(View itemView) {
            super(itemView);

            maVe = itemView.findViewById(R.id.maVe);
            tuyenDuongDi = itemView.findViewById(R.id.tuyenDuongDi);
            tuyenDuongDen = itemView.findViewById(R.id.tuyenDuongDen);
            viTriGhe = itemView.findViewById(R.id.viTriGhe);
            gioXuatBen = itemView.findViewById(R.id.gioXuatBen);
            hoTroButton = itemView.findViewById(R.id.hoTro);
        }
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull TicketViewHolder holder, int position){
        TicketHistoryResponse currenTicket = tiketList.get(position);

        holder.maVe.setText(currenTicket.getMaVe());
        holder.tuyenDuongDi.setText(currenTicket.getTuyenDuongDi());
        holder.tuyenDuongDen.setText(currenTicket.getTuyenDuongDen());
        holder.viTriGhe.setText(currenTicket.getViTriGhe());
        holder.gioXuatBen.setText(currenTicket.getGioXuatBen());

        holder.hoTroButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v) {

           }
        });
    }
    @Override
    public int getItemCount() {
        return tiketList.size();
    }
}
