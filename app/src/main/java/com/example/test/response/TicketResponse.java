package com.example.test.response;
import com.google.gson.annotations.SerializedName;
public class TicketResponse {

    @SerializedName("ThoiGianDi")
    private String gioDi;

    @SerializedName("ThoiGianDen")
    private String gioDen;
    @SerializedName("GiaVe")
    private String giaVe;
    @SerializedName("LoaiGhe")
    private String loaiGhe;
    @SerializedName("SoLuongGhe")
    private String soLuongGhe;
    @SerializedName("DiemDi")
    private String diemDi;
    @SerializedName("KhoangCach")
    private String khoangCach;
    @SerializedName("DiemDen")
    private String diemDen;

    public TicketResponse(String gioDi, String gioDen, String giaVe, String loaiGhe, String soLuongGhe, String diemDi,String khoangCach, String diemDen){
        this.gioDi = gioDi;
        this.gioDen = gioDen;
        this.giaVe = giaVe;
        this.loaiGhe = loaiGhe;
        this.soLuongGhe = soLuongGhe;
        this.diemDi = diemDi;
        this.khoangCach = khoangCach;
        this.diemDen = diemDen;
    }

    public String getGioDi() {
        return gioDi;
    }

    public String getGioDen() {
        return gioDen;
    }

    public String getGiaVe() {
        return giaVe;
    }

    public String getLoaiGhe() {
        return loaiGhe;
    }

    public String getSoLuongGhe() {
        return soLuongGhe;
    }

    public String getDiemDi() {
        return diemDi;
    }

    public String getKhoangCach() {
        return khoangCach;
    }

    public String getDiemDen() {
        return diemDen;
    }
}
