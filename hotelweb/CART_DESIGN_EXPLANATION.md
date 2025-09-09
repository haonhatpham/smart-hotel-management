# THIẾT KẾ CART CHO HỆ THỐNG KHÁCH SẠN THÔNG MINH

## 📋 **PHÂN TÍCH YÊU CẦU ĐỀ TÀI**

### Đặc điểm đề tài khách sạn:
- ✅ **1 đơn đặt phòng** có thể chứa **NHIỀU PHÒNG**
- ✅ **Mỗi phòng** có thể kèm theo **NHIỀU DỊCH VỤ** (spa, nhà hàng, đưa đón...)
- ✅ **Khách hàng** có thể đặt phòng cho nhiều ngày khác nhau
- ✅ **Thanh toán tổng** cho toàn bộ đơn hàng

### So sánh với bán hàng thông thường:
| Bán hàng | Khách sạn |
|----------|-----------|
| 1 sản phẩm = 1 item | 1 phòng + dịch vụ = 1 booking item |
| Quantity = số lượng sản phẩm | Quantity = số đêm ở |
| Giá cố định | Giá theo ngày + dịch vụ |

---

## 🛒 **THIẾT KẾ CART CHO KHÁCH SẠN**

### 1. **Cấu trúc Cart hiện tại (từ Home.js)**
```javascript
// Cart structure trong cookie
cart = {
    "roomId": {
        "id": roomId,
        "roomNumber": "101",
        "roomType": "Deluxe",
        "price": 1000000,
        "quantity": 1  // Số đêm
    }
}
```

### 2. **Vấn đề với thiết kế hiện tại:**
❌ **Thiếu thông tin quan trọng:**
- Ngày check-in, check-out
- Dịch vụ kèm theo
- Thông tin khách hàng

❌ **Logic không phù hợp:**
- `quantity` chỉ là số đêm, không thể đặt cùng phòng nhiều khoảng thời gian
- Không quản lý được dịch vụ

---

## 🔧 **THIẾT KẾ CART CẢI TIẾN**

### 1. **Cấu trúc Cart mới đề xuất:**
```javascript
cart = {
    // Thông tin chung đơn đặt
    "reservationInfo": {
        "customerName": "",
        "customerEmail": "",
        "customerPhone": "",
        "totalAmount": 0
    },
    
    // Danh sách phòng đặt
    "rooms": {
        "room_101_20240915_20240917": {
            "roomId": 1,
            "roomNumber": "101",
            "roomType": "Deluxe",
            "checkIn": "2024-09-15",
            "checkOut": "2024-09-17",
            "nights": 2,
            "pricePerNight": 1000000,
            "roomTotal": 2000000,
            "services": {
                "spa_1": {
                    "serviceId": 1,
                    "serviceName": "Spa Massage",
                    "price": 500000,
                    "quantity": 1,
                    "date": "2024-09-16"
                },
                "restaurant_2": {
                    "serviceId": 2,
                    "serviceName": "Dinner",
                    "price": 300000,
                    "quantity": 2,
                    "date": "2024-09-15"
                }
            },
            "serviceTotal": 1100000,
            "itemTotal": 3100000
        },
        
        "room_102_20240918_20240920": {
            "roomId": 2,
            "roomNumber": "102",
            "roomType": "Suite",
            "checkIn": "2024-09-18",
            "checkOut": "2024-09-20",
            "nights": 2,
            "pricePerNight": 1500000,
            "roomTotal": 3000000,
            "services": {},
            "serviceTotal": 0,
            "itemTotal": 3000000
        }
    }
}
```

---

## 💻 **GIẢI THÍCH CODE CHI TIẾT**

## 🔗 **HEADER.JS - DYNAMIC NAVIGATION**

### 1. **Tại sao cần load danh mục động?**

#### **Vấn đề với cách cũ (hardcode):**
```javascript
// ❌ Cách cũ: hardcode trong JSX
<Link to="/rooms/standard" className="dropdown-item">Phòng Standard</Link>
<Link to="/rooms/deluxe" className="dropdown-item">Phòng Deluxe</Link>
```
**Nhược điểm:**
- Không linh hoạt khi thêm/xóa loại phòng
- Giá cả không cập nhật real-time
- Phải sửa code mỗi khi thay đổi

#### **Cách mới (dynamic loading):**
```javascript
// ✅ Cách mới: load từ API
const [roomTypes, setRoomTypes] = useState([]);
const loadRoomTypes = async () => {
    let res = await Apis.get(endpoints['room-types']);
    setRoomTypes(res.data || []);
};
```

### 2. **State Management trong Header:**
```javascript
const [roomTypes, setRoomTypes] = useState([]);
const [loading, setLoading] = useState(true);
```
**Ý nghĩa:**
- `roomTypes`: Danh sách loại phòng từ database
- `loading`: Trạng thái loading để hiển thị "Đang tải..."

### 3. **API Call Logic:**
```javascript
const loadRoomTypes = async () => {
    try {
        console.log("=== HEADER: Loading Room Types ===");
        let res = await Apis.get(endpoints['room-types']);
        setRoomTypes(res.data || []);
    } catch (error) {
        console.error("Failed to load room types:", error);
    } finally {
        setLoading(false);
    }
};
```
**Ý nghĩa:**
- Gọi API `/api/room-types` để lấy danh mục
- Console log để debug
- Error handling nếu API fail
- Set loading = false khi xong

### 4. **Dynamic Dropdown Rendering:**
```javascript
<NavDropdown 
    title={loading ? "Đang tải..." : "Loại phòng"} 
    disabled={loading}
>
    {roomTypes.length > 0 ? (
        roomTypes.map(roomType => (
            <Link 
                key={roomType.id}
                to={`/?typeId=${roomType.id}`} 
                className="dropdown-item"
            >
                {roomType.name} - {roomType.price?.toLocaleString()} VNĐ
            </Link>
        ))
    ) : (
        !loading && <span className="dropdown-item-text">Không có loại phòng</span>
    )}
</NavDropdown>
```
**Ý nghĩa:**
- **Title động**: "Đang tải..." → "Loại phòng"
- **Disabled khi loading**: Không cho click khi đang tải
- **Map qua roomTypes**: Tạo Link cho mỗi loại phòng
- **URL với typeId**: `/?typeId=1` để filter ở Home
- **Hiển thị giá**: `Standard - 800,000 VNĐ`
- **Fallback**: Hiển thị "Không có loại phòng" nếu empty

### 5. **URL Linking Strategy:**
```javascript
to={`/?typeId=${roomType.id}`}
```
**Tại sao dùng query parameter?**
- ✅ **SEO friendly**: URL clean `/`
- ✅ **State management**: Home component nhận `typeId` từ URL
- ✅ **Bookmarkable**: User có thể bookmark link filter
- ✅ **Back/Forward**: Browser history hoạt động đúng

### 6. **Header ↔ Home Communication Flow:**
```
1. User click "Standard - 800,000 VNĐ" trong Header dropdown
2. Navigate to "/?typeId=1"
3. Home component nhận typeId từ useSearchParams
4. Home gọi API với filter: /api/rooms?page=1&roomTypeId=1
5. Chỉ hiển thị phòng Standard
```

**Code flow chi tiết:**
```javascript
// Header.js - Tạo link
<Link to={`/?typeId=${roomType.id}`}>
    {roomType.name} - {roomType.price?.toLocaleString()} VNĐ
</Link>

// Home.js - Nhận và xử lý
const [q] = useSearchParams();
let typeId = q.get("typeId");
if (typeId) 
    url = `${url}&roomTypeId=${typeId}`;
```

---

## 🏠 **HOME.JS - MAIN COMPONENT**

### 1. **Tại sao thiết kế như hiện tại?**

#### **State Management:**
```javascript
const [rooms, setRooms] = useState([]);
const [page, setPage] = useState(1);
const [loading, setLoading] = useState(true);
```
**Ý nghĩa:**
- `rooms`: Danh sách phòng available hiển thị trang chủ
- `page`: Pagination để load thêm phòng (UX tốt)
- `loading`: Spinner khi đang tải dữ liệu

#### **API Call Logic:**
```javascript
const loadRooms = async () => {
    let url = `${endpoints['rooms']}?page=${page}`;
    let typeId = q.get("typeId");
    if (typeId) 
        url = `${url}&typeId=${typeId}`;
}
```
**Ý nghĩa:**
- Gọi API `/api/rooms` (chỉ phòng AVAILABLE)
- Filter theo `typeId` từ dropdown Header
- Pagination với `page` parameter

#### **Cart Logic hiện tại:**
```javascript
const addToCart = (room) => {
    let cart = cookie.load('cart') || null;
    if (cart === null) cart = {}
    
    if (room.id in cart) {
        cart[room.id]["quantity"]++;
    } else {
        cart[room.id] = {
            "id": room.id,
            "roomNumber": room.roomNumber,
            "roomType": room.roomType?.name,
            "price": room.price,
            "quantity": 1
        }
    }
}
```

**Ý nghĩa:**
- Lưu cart trong **cookie** (persistent across sessions)
- Key = `room.id` (unique cho mỗi phòng)
- `quantity++` khi thêm cùng phòng lần 2

---

## ⚠️ **VẤN ĐỀ VÀ GIẢI PHÁP**

### 1. **Vấn đề với thiết kế hiện tại:**

❌ **Không thể đặt cùng phòng nhiều khoảng thời gian:**
```javascript
// Nếu khách muốn đặt phòng 101:
// - Từ 15-17/9
// - Từ 20-22/9
// → Hiện tại sẽ bị conflict vì cùng room.id
```

❌ **Thiếu thông tin booking cần thiết:**
- Không có ngày check-in/check-out
- Không có dịch vụ kèm theo
- Quantity = số đêm không rõ ràng

### 2. **Giải pháp đề xuất:**

✅ **Thay đổi Key structure:**
```javascript
// Thay vì: cart[room.id]
// Dùng: cart[`${room.id}_${checkIn}_${checkOut}`]
```

✅ **Thêm thông tin booking:**
```javascript
const addToCart = (room, checkIn, checkOut, services = []) => {
    const bookingKey = `${room.id}_${checkIn}_${checkOut}`;
    const nights = calculateNights(checkIn, checkOut);
    
    cart[bookingKey] = {
        roomId: room.id,
        roomNumber: room.roomNumber,
        roomType: room.roomType?.name,
        checkIn: checkIn,
        checkOut: checkOut,
        nights: nights,
        pricePerNight: room.price,
        roomTotal: room.price * nights,
        services: services,
        serviceTotal: calculateServiceTotal(services),
        itemTotal: (room.price * nights) + calculateServiceTotal(services)
    };
}
```

---

## 🎯 **LUỒNG HOẠT ĐỘNG ĐỀ XUẤT**

### 1. **Trang chủ (Home.js):**
- Hiển thị phòng available
- Nút "Thêm vào giỏ" → Redirect đến trang booking chi tiết

### 2. **Trang Booking:**
- Chọn ngày check-in/check-out
- Chọn dịch vụ kèm theo
- Xác nhận thêm vào cart

### 3. **Trang Cart:**
- Hiển thị tất cả phòng đã đặt
- Tính tổng tiền
- Checkout

### 4. **Flow hoàn chỉnh:**
```
Trang chủ → Chọn phòng → Booking Form → Cart → Payment → Confirmation
```

---

## 📊 **KẾT LUẬN**

### **Thiết kế hiện tại phù hợp cho:**
- ✅ Demo nhanh concept
- ✅ Hiển thị danh sách phòng
- ✅ Basic cart functionality

### **Cần cải tiến để phù hợp đề tài:**
- 🔧 Thêm thông tin ngày tháng
- 🔧 Quản lý dịch vụ kèm theo
- 🔧 Support multiple bookings
- 🔧 Tính toán giá phức tạp hơn

### **Bước tiếp theo:**
1. Tạo trang Booking Form chi tiết
2. Cải tiến Cart structure
3. Tạo trang Cart management
4. Implement checkout flow

**Thiết kế hiện tại là foundation tốt, chỉ cần mở rộng thêm để đáp ứng yêu cầu đề tài khách sạn!** 🏨
