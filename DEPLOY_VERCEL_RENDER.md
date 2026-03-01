# Hướng dẫn deploy: Railway (Backend + MySQL) + Vercel (Frontend)

**Cách 1:** Backend và MySQL trên Railway, Frontend trên Vercel — tất cả trong 1 project Railway, dễ quản lý.

> **Lưu ý:** Railway dùng credit (~$5 tháng đầu, ~$1/tháng sau). Cần thẻ để đăng ký.

---

## Chuẩn bị

- Tài khoản **GitHub** (code đã push lên repo).
- Tài khoản **Vercel**: https://vercel.com/signup
- Tài khoản **Railway**: https://railway.app (cần thẻ)

---

## Phần 1: Tạo project Railway (MySQL + Backend)

### Bước 1.1: Tạo project và MySQL

1. Đăng nhập https://railway.app
2. **New Project** → **Add Service** → **Database** → **MySQL**.
3. Đợi MySQL khởi động. Vào service MySQL → tab **Variables** hoặc **Connect** → lưu **MYSQLHOST**, **MYSQLPORT**, **MYSQLUSER**, **MYSQLPASSWORD**, **MYSQLDATABASE**.
4. Trong cùng project → **Add Service** → **GitHub Repo** → chọn repo **smart-hotel-management** (tạo service backend).

### Bước 1.2: Chạy schema và seed

1. Dùng MySQL client (Workbench, DBeaver, `mysql` CLI) kết nối với thông tin từ Bước 1.1.
2. Chạy lần lượt nội dung: `db/schema.sql` rồi `db/seed_compact.sql`.

*(Hoặc dùng **Query** trên Railway dashboard MySQL nếu có.)*

### Bước 1.3: Cấu hình service Backend

1. Chọn service backend (từ GitHub) → **Settings** → **Root Directory** đặt **`SpringMVC_SMART_HOTEL`**.
3. **Settings** → **Build**:
   - **Builder**: chọn **Dockerfile**
   - **Dockerfile Path**: `Dockerfile` (trong Root Directory)
4. **Settings** → **Deploy**:
   - **Watch Paths**: có thể để trống hoặc `SpringMVC_SMART_HOTEL/**`

### Bước 1.4: Kết nối Backend với MySQL

1. Vào service **Backend** → **Variables**.
2. **Connect** service MySQL (nút **Connect** hoặc **Add Variable Reference** → chọn MySQL service). Railway sẽ inject `MYSQLHOST`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`, `MYSQLPORT`.
3. Thêm biến `FRONTEND_BASE_URL` = `https://ten-app.vercel.app` *(cập nhật sau khi deploy Vercel xong)*

*Nếu không Connect được: thêm thủ công `HIBERNATE_CONNECTION_URL`, `HIBERNATE_CONNECTION_USERNAME`, `HIBERNATE_CONNECTION_PASSWORD` (ghép từ thông tin MySQL ở Bước 1.1).*

### Bước 1.5: Lấy URL Backend

1. Vào service Backend → **Settings** → **Networking** → **Generate Domain**.
2. URL dạng: **`https://xxx.up.railway.app`** (không có dấu `/` cuối).
3. Thêm biến `BACKEND_BASE_URL` = URL vừa có (vd: `https://smart-hotel-api.up.railway.app`).
4. **URL API đầy đủ**: **`https://xxx.up.railway.app/SpringMVC_SMART_HOTEL/api`**  
   → Dùng cho frontend (Vercel).

---

## Phần 2: Frontend trên Vercel

### Bước 2.1: Import project

1. Đăng nhập https://vercel.com
2. **Add New…** → **Project** → **Import** repo **smart-hotel-management**.

### Bước 2.2: Cấu hình build

3. **Root Directory**: **Edit** → đặt **`hotelweb`**.
4. **Framework Preset**: Create React App (tự nhận).
5. **Build Command**: `npm run build`
6. **Output Directory**: `build`

### Bước 2.3: Biến môi trường

7. **Environment Variables** → thêm:
   - **Name**: `REACT_APP_API_URL`
   - **Value**: `https://xxx.up.railway.app/SpringMVC_SMART_HOTEL/api`  
     *(Thay `xxx.up.railway.app` bằng URL thật từ Bước 1.5.)*

8. **Deploy** → đợi build xong.

### Bước 2.4: Cập nhật CORS

9. Sau khi Vercel deploy xong, có URL frontend (vd: `https://smart-hotel-xxx.vercel.app`).
10. Vào **Railway** → service Backend → **Variables** → sửa `FRONTEND_BASE_URL` thành **đúng URL Vercel**.
11. Railway sẽ auto redeploy; CORS sẽ cho phép frontend gọi API.

---

## Phần 3: Thanh toán & callback (VNPay / MoMo)

- **frontend.baseUrl** dùng qua `FRONTEND_BASE_URL`.
- **Callback URL** (vnpay.returnUrl, momo.redirectUrl, momo.ipnUrl) được set tự động qua biến `BACKEND_BASE_URL` trong `docker-entrypoint.sh` — không cần sửa `payment-config.properties`.

---

## Bảng biến môi trường

### Railway (Backend service)

| Biến | Bắt buộc | Nguồn / Ghi chú |
|------|----------|-----------------|
| `MYSQLHOST`, `MYSQLUSER`, `MYSQLPASSWORD`, `MYSQLDATABASE`, `MYSQLPORT` | ✓ | Tự động từ Connect MySQL |
| `FRONTEND_BASE_URL` | ✓ | URL Vercel (vd: `https://xxx.vercel.app`) |
| `BACKEND_BASE_URL` | ✓ | URL Backend Railway (vd: `https://xxx.up.railway.app`) |
| `GEMINI_API_KEY` | (tuỳ chọn) | Chatbot AI; bỏ trống = dùng rule-based |
| `GOOGLE_CLIENT_ID` | **Cần cho đăng nhập Google** | OAuth Client ID từ Google Cloud Console |
| `GOOGLE_CLIENT_SECRET` | (tuỳ chọn) | OAuth Client Secret (hiện tại chỉ dùng Client ID) |
| `MOMO_PARTNER_CODE`, `MOMO_ACCESS_KEY`, `MOMO_SECRET_KEY` | (tuỳ chọn) | Thanh toán MoMo sandbox |
| `VNPAY_TMN_CODE`, `VNPAY_HASH_SECRET` | (tuỳ chọn) | Thanh toán VNPay sandbox |

### Vercel (Frontend)

| Biến | Bắt buộc | Ghi chú |
|------|----------|---------|
| `REACT_APP_API_URL` | ✓ | URL API: `https://xxx.up.railway.app/SpringMVC_SMART_HOTEL/api` |

---

## Tóm tắt URL

| Thành phần | URL ví dụ |
|------------|-----------|
| Frontend (Vercel) | `https://smart-hotel-xxx.vercel.app` |
| Backend API (Railway) | `https://xxx.up.railway.app/SpringMVC_SMART_HOTEL/api` |
| MySQL | Nội bộ Railway (MYSQLHOST, …) |

---

## Checklist nhanh

- [ ] Railway: tạo project, thêm MySQL, chạy `schema.sql` + `seed_compact.sql`
- [ ] Railway: thêm service Backend (GitHub, Root Directory `SpringMVC_SMART_HOTEL`, Dockerfile)
- [ ] Railway: Connect MySQL → Backend (hoặc add Variable Reference)
- [ ] Railway: thêm `FRONTEND_BASE_URL`, `BACKEND_BASE_URL` (URL backend), Generate Domain cho Backend
- [ ] Vercel: Root Directory `hotelweb`, biến `REACT_APP_API_URL` = URL API Railway
- [ ] Sau Vercel: cập nhật `FRONTEND_BASE_URL` trên Railway
- [ ] (Tuỳ chọn) Cấu hình callback thanh toán production
- [ ] (Đăng nhập Google) Cấu hình `GOOGLE_CLIENT_ID` — xem mục dưới

Sau khi xong, mở link Vercel trong trình duyệt để dùng web. Nếu lỗi, mở Console (F12) và tab Network để kiểm tra request API.

---

## Phần 4: Đăng nhập bằng Google (OAuth)

Nếu gặp lỗi **"The OAuth client was not found"** hoặc **"Lỗi 401: invalid_client"** khi đăng nhập Google:

### Bước 4.1: Tạo OAuth credentials trên Google Cloud Console

1. Truy cập https://console.cloud.google.com
2. Tạo project mới hoặc chọn project có sẵn
3. Vào **APIs & Services** → **Credentials** → **Create Credentials** → **OAuth client ID**
4. Nếu chưa có, cấu hình **OAuth consent screen** (chọn External, điền tên app, email hỗ trợ)
5. Chọn loại **Web application**
6. **Authorized JavaScript origins** — thêm:
   - `https://smart-hotel-management.vercel.app` (hoặc URL Vercel của bạn)
   - `http://localhost:3000` (cho dev local)
7. Tạo xong → copy **Client ID** (dạng `xxx.apps.googleusercontent.com`)

### Bước 4.2: Cấu hình trên Railway

1. Vào Railway → service Backend → **Variables**
2. Thêm biến `GOOGLE_CLIENT_ID` = *(dán Client ID vừa copy)*
3. Backend sẽ tự redeploy và dùng Client ID mới

### Bước 4.3: Cấu hình local (phát triển)

Tạo file `SpringMVC_SMART_HOTEL/src/main/resources/gugle-local.properties`:

```properties
client_id=YOUR_ACTUAL_GOOGLE_CLIENT_ID
client_secret=YOUR_ACTUAL_GOOGLE_CLIENT_SECRET
```

*(Lưu ý: `gugle-local.properties` đã nằm trong `.gitignore` để tránh commit thông tin nhạy cảm.)*
