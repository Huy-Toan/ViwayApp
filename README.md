# 🚍 VIWAY App Clone

Ứng dụng đặt vé xe khách mô phỏng theo hệ thống của FUTA Bus (Phương Trang).  
Người dùng có thể **tìm chuyến xe, chọn ghế, đặt vé, thanh toán và quản lý vé** ngay trên ứng dụng.

---

## ✨ Tính Năng Chính

- **Đăng ký / Đăng nhập**  
  - Hỗ trợ Email / SĐT
  - Xác thực OTP qua SMS/Email
  
- **Tìm kiếm & Đặt vé**  
  - Chọn điểm đi – điểm đến  
  - Lọc kết quả theo giá, giờ khởi hành, loại xe (ghế/giường)
  - Chọn số lượng vé & chọn ghế trên sơ đồ
  - Chọn điểm lên xe ( Bến xe đón hoặc trung chuyển)
  - Chỉnh sửa thông tin người đặt vé

- **Quản lý chuyến đi**  
  - Xem danh sách chuyến đã đặt  
  - Hủy vé xe
  - Xem chi tiết lịch sử vé
  - Xem danh sách feedback
  
- **Thanh toán**  
  - Thanh toán qua VNPay / Momo   

- **Tính năng phụ**  
  - Thông báo lịch trình
  - Feedback (gửi nội dung hỗ trợ, ảnh hỗ trợ nếu cần)
  - Chỉnh sửa trang cá nhân ( tên, email, nghề nghiệp, địa chỉ, ảnh đại diện...)
  - Đăng xuất
  - Xóa tài khoản
  - Quên mật khẩu

---

## 🛠 Công Nghệ Sử Dụng

### **Frontend (Mobile App)**  
- Java (Android)  
- XML layout 
- RecyclerView, ViewPager, Fragment, Spinner... 

### **Backend API**  
- SpringBoot3
- MySQL (Database quản lý chuyến xe, vé, người dùng)

---

## 📥 Cài Đặt
- Android Studio
- JDK 8 trở lên
- Android SDK đầy đủ (API 30+)

## Cách chạy ứng dụng
1. Mở Android Studio
2. Chọn "Open" và mở thư mục dự án đã clone
3. Chờ Gradle tự động sync (mất vài phút nếu lần đầu)
4. Kết nối thiết bị thật **hoặc** khởi chạy AVD (Android Virtual Device)
5. Nhấn nút Run ▶️ để khởi chạy ứng dụng

```bash
https://github.com/Huy-Toan/ViwayApp.git
