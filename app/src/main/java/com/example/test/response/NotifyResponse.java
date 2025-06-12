package com.example.test.response;
import com.google.gson.annotations.SerializedName;
public class NotifyResponse {

    @SerializedName("TieuDe")
    private String tieuDeThongBao;

    @SerializedName("ThoiGian")
    private String thoiGianThongBao;

    @SerializedName("NoiDungThongBao")
    private String noiDungThongBao;

    public NotifyResponse(String tieuDeThongBao, String thoiGianThongBao, String noiDungThongBao){
        this.tieuDeThongBao = tieuDeThongBao;
        this.thoiGianThongBao = thoiGianThongBao;
        this.noiDungThongBao = noiDungThongBao;
    }

    public String getTieuDeThongBao() {
        return tieuDeThongBao;
    }

    public String getThoiGianThongBao() {
        return thoiGianThongBao;
    }

    public String getNoiDungThongBao() {
        return noiDungThongBao;
    }

}
