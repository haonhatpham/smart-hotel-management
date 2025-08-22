## Smart Hotel Management – Topic Requirement & Database Schema

### Giới thiệu
Hệ thống quản lý khách sạn thông minh phục vụ đặt phòng trực tuyến, quản lý dịch vụ lưu trú, thanh toán và báo cáo thống kê. Mục tiêu là nâng cao trải nghiệm khách hàng và tối ưu vận hành cho ban quản lý.

### Yêu cầu môn học (Topic requirement)
- **Kiến trúc**: Backend Spring MVC (Spring Boot), Spring Security cho xác thực/ủy quyền; Client phát triển ReactJS; giao tiếp qua RESTful API.
- **Đăng ký/đăng nhập**: Tài khoản hệ thống và có thể tích hợp OAuth2 (Google/Facebook). Mật khẩu mã hóa BCrypt, JWT cho client.
- **Phân hệ Admin**: Quản trị người dùng/quyền, danh mục phòng/loại phòng, quản lý đặt phòng nhiều phòng/đơn, dịch vụ, thanh toán, hóa đơn, housekeeping, duyệt/ẩn đánh giá, báo cáo.
- **Phân hệ Client**: Tra cứu phòng trống theo ngày/loại/giá, đặt phòng trực tuyến (nhiều phòng/đơn), đặt dịch vụ kèm, thanh toán, nhận hóa đơn, đánh giá, quản lý hồ sơ.
- **Báo cáo/Thống kê**: Tỷ lệ lấp phòng, doanh thu theo thời gian, top dịch vụ, điểm đánh giá.
- **Vai trò**: `ADMIN`, `RECEPTION`, `HOUSEKEEPING`, `ACCOUNTANT`, `CUSTOMER` với phân quyền tương ứng endpoint.

### Database Schema Diagram (Hình ảnh)

![Database Schema](db-schema.png)

### Ghi chú
- `RESERVATION_ROOMS` có ràng buộc UNIQUE `(reservation_id, room_id)` để tránh trùng một phòng nhiều lần trong cùng đơn.
- Chỉ mục đi kèm phục vụ tra cứu nhanh theo ngày, trạng thái và thống kê doanh thu.
- Để hình hiển thị, hãy lưu file sơ đồ tên `db-schema.png` cùng thư mục với file README (PNG/JPG đều được, đúng tên và đường dẫn như trên).
