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

## 💻 **GIẢI THÍCH CODE TRONG HOME.JS**

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

---

## ⚡ **REAL-TIME SEARCH VỚI QUERY PARAMETERS**

### 1. **Cách hoạt động của Real-time Search:**

#### **Cơ chế cơ bản:**
```javascript
// Booking.js
const [searchParams, setSearchParams] = useSearchParams();

// Form values luôn sync với URL
const searchForm = {
    checkIn: searchParams.get('checkIn') || '',
    checkOut: searchParams.get('checkOut') || '',
    guests: searchParams.get('guests') || '2',
    roomType: searchParams.get('roomType') || ''
};

// Khi URL thay đổi → Tự động search
useEffect(() => {
    searchRooms(); // Gọi API với params từ URL
}, [searchParams]);
```

#### **Khi user thay đổi form:**
```javascript
// Input onChange
onChange={(e) => {
    const newParams = { ...Object.fromEntries(searchParams) };
    newParams.checkIn = e.target.value;
    setSearchParams(newParams); // ← Update URL
}}

// Flow:
// 1. User đổi ngày → setSearchParams() 
// 2. URL update: /booking?checkIn=2024-09-15&checkOut=2024-09-17
// 3. useEffect detect searchParams change
// 4. searchRooms() được gọi tự động
// 5. API call → Update kết quả
```

### 2. **So sánh Real-time vs Manual Search:**

#### **Real-time Search (Query Parameter):**
```javascript
// ✅ Mỗi thay đổi form → Tự động search
onChange={(e) => {
    setSearchParams({...params, checkIn: e.target.value});
    // → useEffect trigger → API call → Results update
}}

// Nút "Tìm phòng": KHÔNG CẦN THIẾT!
// Vì search đã tự động khi form thay đổi
```

#### **Manual Search (State-based):**
```javascript
// ❌ Phải click nút để search
const handleSearch = () => {
    const res = await api.get(`/api/search?${params}`);
    setResults(res.data);
};

// Nút "Tìm phòng": CẦN THIẾT!
<Button onClick={handleSearch}>Tìm phòng</Button>
```

### 3. **Ưu nhược điểm Real-time Search:**

#### **✅ Ưu điểm:**
- **UX mượt mà**: Không cần click, results update ngay
- **Instant feedback**: User thấy ngay kết quả khi thay đổi
- **Giống Google**: Search as you type experience
- **Professional**: Như Booking.com, Airbnb

#### **❌ Nhược điểm:**
- **Nhiều API calls**: Mỗi keystroke có thể gọi API
- **Performance**: Cần debouncing cho text inputs
- **Server load**: Tăng số request

### 4. **Khi nào cần/không cần nút "Tìm phòng":**

#### **KHÔNG CẦN nút khi:**
- ✅ **Dropdown/Select**: Thay đổi ngay → Search ngay
- ✅ **Date picker**: Chọn ngày → Search ngay
- ✅ **Simple filters**: Ít options, không cần confirm

#### **VẪN CẦN nút khi:**
- ⚠️ **Text input**: Cần debouncing hoặc user muốn control
- ⚠️ **Complex form**: Nhiều fields, user muốn điền xong mới search
- ⚠️ **Performance concern**: Muốn giảm API calls

### 5. **Implementation chi tiết:**

#### **Option 1: Full Real-time (không cần nút):**
```javascript
// Mọi thay đổi → Instant search
const updateParam = (key, value) => {
    const newParams = { ...Object.fromEntries(searchParams) };
    newParams[key] = value;
    setSearchParams(newParams); // → useEffect → API call
};

<Form.Control 
    type="date"
    value={searchForm.checkIn}
    onChange={(e) => updateParam('checkIn', e.target.value)}
/>
// → User chọn ngày → Ngay lập tức search
```

#### **Option 2: Semi Real-time (có nút backup):**
```javascript
// Dropdown → Real-time, Date → Manual
<Form.Select onChange={(e) => updateParam('guests', e.target.value)}>
// → Instant search

<Form.Control type="date" onChange={(e) => setLocalDate(e.target.value)}>
// → Chỉ update local state

<Button onClick={() => updateParam('checkIn', localDate)}>
    Tìm phòng
</Button>
// → Manual trigger
```

### 6. **Kết luận cho đề tài khách sạn:**

#### **✅ Đề xuất: Real-time cho Date/Dropdown**
```javascript
// Date picker → Real-time search (UX tốt)
// Dropdown loại phòng → Real-time search  
// Dropdown số khách → Real-time search

// → KHÔNG CẦN NÚT "TÌM PHÒNG"
// → Hoặc giữ nút nhưng chỉ để "Làm mới kết quả"
```

#### **🎯 Flow người dùng:**
```
1. Vào trang /booking → Load all available rooms
2. Chọn check-in date → URL update → Auto search → Results update
3. Chọn check-out date → URL update → Auto search → Results update  
4. Chọn số khách → URL update → Auto search → Results update
5. Chọn loại phòng → URL update → Auto search → Results update
6. Chọn phòng → Add to sidebar → Booking complete
```

**Real-time search tạo trải nghiệm mượt mà, chuyên nghiệp như các website booking hàng đầu!** ⚡
