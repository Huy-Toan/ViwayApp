package com.example.test.response;
import com.google.gson.annotations.SerializedName;
public class TicketHistoryResponse {

    @SerializedName("code")
    private String maVe;
    @SerializedName("departureLocation")
    private String tuyenDuongDi;
    @SerializedName("arrivalLocation")
    private String tuyenDuongDen;
    @SerializedName("seatNumber")
    private String viTriGhe;
    @SerializedName("departureTime")
    private String gioXuatBen;

    public TicketHistoryResponse(String maVe, String tuyenDuongDi, String tuyenDuongDen, String viTriGhe, String gioXuatBen){
        this.maVe = maVe;
        this.tuyenDuongDi = tuyenDuongDi;
        this.tuyenDuongDen = tuyenDuongDen;
        this.viTriGhe = viTriGhe;
        this.gioXuatBen = gioXuatBen;
    }

    public String getMaVe() {
        return maVe;
    }

    public String getTuyenDuongDi() {
        return tuyenDuongDi;
    }

    public String getTuyenDuongDen() {
        return tuyenDuongDen;
    }

    public String getGioXuatBen() {
        return gioXuatBen;
    }

    public String getViTriGhe() {
        return viTriGhe;
    }

}
