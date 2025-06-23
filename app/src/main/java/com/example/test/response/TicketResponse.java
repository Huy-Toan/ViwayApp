package com.example.test.response;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TicketResponse implements Serializable {

    @SerializedName("id")
    private Integer ticketId;
    @SerializedName("timeStart")
    private String gioDi;
    @SerializedName("timeEnd")
    private String gioDen;
    @SerializedName("price")
    private Integer giaVe;
    @SerializedName("vehicleKind")
    private String loaiGhe;
    @SerializedName("availableSeats")
    private Integer soLuongGhe;
    @SerializedName("startLocation")
    private String diemDi;
    @SerializedName("distance")
    private Integer khoangCach;
    @SerializedName("estimatedDuration")
    private String thoiGianDi;
    @SerializedName("endLocation")
    private String diemDen;

    public TicketResponse(Integer ticketId,String gioDi, String gioDen, Integer giaVe, String loaiGhe, Integer soLuongGhe, String diemDi,Integer khoangCach,String thoiGianDi, String diemDen){
        this.ticketId = ticketId;
        this.gioDi = gioDi;
        this.gioDen = gioDen;
        this.giaVe = giaVe;
        this.loaiGhe = loaiGhe;
        this.soLuongGhe = soLuongGhe;
        this.diemDi = diemDi;
        this.khoangCach = khoangCach;
        this.thoiGianDi = thoiGianDi;
        this.diemDen = diemDen;
    }

    public String getGioDi() {
        return gioDi;
    }

    public String getGioDen() {
        return gioDen;
    }

    public Integer getGiaVe() {
        return giaVe;
    }

    public String getLoaiGhe() {
        return loaiGhe;
    }

    public Integer getSoLuongGhe() {
        return soLuongGhe;
    }

    public String getDiemDi() {
        return diemDi;
    }

    public Integer getKhoangCach() {
        return khoangCach;
    }

    public String getDiemDen() {
        return diemDen;
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public String getThoiGianDi() {
        return thoiGianDi;
    }
}
