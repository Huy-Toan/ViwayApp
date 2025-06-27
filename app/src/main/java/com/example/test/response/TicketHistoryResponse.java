package com.example.test.response;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketHistoryResponse implements Serializable {

    @SerializedName("code_ticket")
    private String maVe;
    @SerializedName("origin")
    private String diemDi;
    @SerializedName("destination")
    private String diemDen;
    @SerializedName("seat_code")
    private String viTriGhe;
    @SerializedName("full_time")
    private String gioXuatBen;

    public TicketHistoryResponse(String maVe, String diemDi, String diemDen, String viTriGhe, String gioXuatBen){
        this.maVe = maVe;
        this.diemDi = diemDi;
        this.diemDen = diemDen;
        this.viTriGhe = viTriGhe;
        this.gioXuatBen = gioXuatBen;
    }

    public String getMaVe() {
        return maVe;
    }

    public String getTuyenDuongDi() {
        return diemDi;
    }

    public String getTuyenDuongDen() {
        return diemDen;
    }

    public String getGioXuatBen() {
        return gioXuatBen;
    }

    public String getViTriGhe() {
        return viTriGhe;
    }

}
