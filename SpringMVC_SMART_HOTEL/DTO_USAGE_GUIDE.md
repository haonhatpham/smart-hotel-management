# DTO Usage Guide for Reservation System

## Overview
Để đơn giản hóa việc tạo đơn hàng (reservation), chúng ta đã tạo các DTO classes để quản lý dữ liệu một cách dễ dàng hơn.

## DTO Classes

### 1. ReservationCreateDTO
DTO chính để tạo reservation mới.

**Fields:**
- `checkIn`: Date - Ngày check-in
- `checkOut`: Date - Ngày check-out  
- `customerId`: Long - ID của khách hàng
- `status`: String - Trạng thái đặt phòng (mặc định: "HELD")
- `rooms`: List<ReservationRoomDTO> - Danh sách phòng
- `services`: List<ServiceOrderDTO> - Danh sách dịch vụ

### 2. ReservationRoomDTO
DTO cho thông tin phòng trong đơn đặt.

**Fields:**
- `roomId`: Long - ID của phòng
- `pricePerNight`: BigDecimal - Giá mỗi đêm
- `notes`: String - Ghi chú (optional)

### 3. ServiceOrderDTO
DTO cho đơn hàng dịch vụ.

**Fields:**
- `serviceId`: Long - ID của dịch vụ
- `qty`: int - Số lượng
- `unitPrice`: BigDecimal - Giá đơn vị
- `amount`: BigDecimal - Tổng tiền
- `orderedAt`: Date - Thời gian đặt (mặc định: hiện tại)
- `notes`: String - Ghi chú (optional)

## API Usage

### Frontend (Checkout.js)
```javascript
const reservationData = {
    checkIn: "2024-01-15",
    checkOut: "2024-01-17", 
    customerId: 123,
    status: "HELD",
    rooms: [
        {
            roomId: 1,
            pricePerNight: 500000,
            notes: null
        }
    ],
    services: [
        {
            serviceId: 1,
            qty: 2,
            unitPrice: 100000,
            amount: 200000,
            orderedAt: "2024-01-15T10:00:00Z",
            notes: "Service for room: Deluxe"
        }
    ]
};

// Gửi POST request đến /api/reservations
const res = await authApis().post(endpoints['reservations'], reservationData);
```

### Backend (ApiReservationController)
```java
@PostMapping("/reservations")
public ResponseEntity<?> create(@RequestBody ReservationCreateDTO dto) {
    Reservations savedReservation = this.reservationService.createFromDTO(dto);
    // Trả về response với ID của reservation đã tạo
}
```

## Benefits

1. **Đơn giản hóa**: Không cần tạo các object phức tạp với relationships
2. **Dễ validate**: DTO có thể được validate dễ dàng hơn
3. **Tách biệt**: Tách biệt giữa API layer và domain layer
4. **Dễ maintain**: Dễ dàng thay đổi cấu trúc mà không ảnh hưởng đến entity classes
5. **Performance**: Giảm overhead khi serialize/deserialize JSON

## Migration Notes

- Frontend giờ chỉ cần gửi IDs thay vì full objects
- Backend sẽ tự động resolve các relationships
- Service layer xử lý việc mapping từ DTO sang Entity
- Error handling được cải thiện với validation rõ ràng hơn

