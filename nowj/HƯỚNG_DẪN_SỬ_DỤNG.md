# HƯỚNG DẪN SỬ DỤNG HỆ THỐNG JWT AUTHENTICATION

## 🎉 Chúc mừng! Dự án đã được chuyển đổi thành công sang xác thực JWT

Hệ thống hiện tại hỗ trợ **2 cách xác thực**:
1. **API JWT** - Cho client/mobile app (trả về JSON)
2. **Web Form** - Cho giao diện web (redirect và lưu JWT vào cookie)

## 🚀 Cách chạy ứng dụng

```bash
cd d:\GitHub\smart-contract-risk-analyzer\nowj
mvn spring-boot:run
```

Hoặc build và chạy:
```bash
mvn clean package
java -jar target/nowj-0.0.1-SNAPSHOT.jar
```

Ứng dụng sẽ chạy tại: **http://localhost:8080**

## 🌐 Sử dụng giao diện WEB (như ứng dụng thông thường)

### 1. Truy cập trang chủ
- URL: http://localhost:8080
- Tự động redirect đến trang login

### 2. Đăng ký tài khoản mới
- URL: http://localhost:8080/register
- Điền đầy đủ thông tin: Email, Họ tên, Mật khẩu, Xác nhận mật khẩu
- Click "Đăng ký"
- Sau khi thành công, tự động chuyển về trang login

### 3. Đăng nhập
- URL: http://localhost:8080/login
- Nhập Email và Mật khẩu đã đăng ký
- Click "Đăng nhập"
- Hệ thống sẽ:
  - Tạo JWT token
  - Lưu JWT vào cookie HTTP-only
  - Chuyển đến dashboard

### 4. Truy cập Dashboard
- URL: http://localhost:8080/dashboard
- Hiển thị thông tin tổng quan của user
- Chỉ truy cập được khi đã đăng nhập

### 5. Đăng xuất
- URL: http://localhost:8080/logout
- Xóa JWT cookie và chuyển về trang login

### 6. Các trang khác
- **Upload hợp đồng**: http://localhost:8080/upload
- **Phân tích hợp đồng**: http://localhost:8080/conversation
- **Cài đặt**: http://localhost:8080/settings

## 🔌 Sử dụng API JWT (cho developers/client apps)

### 1. Đăng ký qua API
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "fullName": "Test User",
    "password": "123456",
    "confirmPassword": "123456"
  }'
```

**Response thành công:**
```json
{
  "success": true,
  "message": "User registered successfully"
}
```

### 2. Đăng nhập qua API
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "123456"
  }'
```

**Response thành công:**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer"
}
```

### 3. Sử dụng JWT Token để truy cập API
```bash
curl -X GET http://localhost:8080/dashboard \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

## 🔒 Bảo mật và Phân quyền

### Endpoints Public (không cần xác thực):
- `/` - Trang chủ
- `/login` - Trang đăng nhập
- `/register` - Trang đăng ký
- `/logout` - Đăng xuất
- `/css/**`, `/js/**`, `/images/**` - Static files
- `/api/auth/**` - API đăng nhập/đăng ký

### Endpoints Protected (cần xác thực):
- `/dashboard` - Dashboard chính
- `/upload` - Upload hợp đồng
- `/conversation/**` - Phân tích hợp đồng
- `/settings` - Cài đặt
- Tất cả các endpoint khác

### JWT Token Properties:
- **Secret Key**: Được cấu hình trong `application.properties`
- **Thời gian hết hạn**: 24 giờ (86400000ms)
- **Cookie**: HTTP-only, Path="/", MaxAge=24h

## 🛠️ Cấu trúc hệ thống

### Controllers:
- **WebAuthController**: Xử lý login/register qua form web
- **JwtAuthController**: API login/register trả về JWT
- **DashboardController**: Dashboard chính
- **ViewController**: Các view khác (upload, conversation, settings)

### Security Components:
- **JwtTokenProvider**: Tạo và validate JWT token
- **JwtAuthenticationFilter**: Filter kiểm tra JWT từ header hoặc cookie
- **JwtAuthenticationEntryPoint**: Xử lý lỗi xác thực
- **SecurityConfig**: Cấu hình Spring Security

### DTO Classes:
- **LoginRequest**: Dữ liệu đăng nhập
- **RegistrationRequestDTO**: Dữ liệu đăng ký
- **JwtAuthenticationResponse**: Response chứa JWT token
- **ApiResponse**: Response chung cho API

## 🧪 Test các chức năng

### 1. Test đăng ký web:
1. Mở http://localhost:8080/register
2. Điền form và submit
3. Kiểm tra redirect về login với thông báo thành công

### 2. Test đăng nhập web:
1. Mở http://localhost:8080/login
2. Nhập email/password và submit
3. Kiểm tra redirect về dashboard
4. Kiểm tra cookie JWT trong DevTools

### 3. Test bảo mật:
1. Truy cập http://localhost:8080/dashboard không đăng nhập
2. Sẽ được redirect về login
3. Đăng nhập thành công sẽ redirect về dashboard

### 4. Test đăng xuất:
1. Từ dashboard, truy cập http://localhost:8080/logout
2. Kiểm tra redirect về login
3. Kiểm tra cookie JWT đã bị xóa

## ⚙️ Cấu hình JWT

Trong file `src/main/resources/application.properties`:

```properties
# JWT Configuration
jwt.secret=mySecretKey123456789012345678901234567890
jwt.expiration-ms=86400000
```

- **jwt.secret**: Khóa bí mật để ký JWT (nên dùng khóa mạnh hơn trong production)
- **jwt.expiration-ms**: Thời gian hết hạn token (24h = 86400000ms)

## 🐛 Troubleshooting

### Lỗi "Port 8080 already in use":
```bash
netstat -ano | findstr :8080
taskkill /PID <PID_NUMBER> /F
```

### Lỗi "JWT token expired":
- Token hết hạn sau 24h
- Đăng nhập lại để nhận token mới

### Lỗi xác thực:
- Kiểm tra email/password đúng
- Kiểm tra user đã được tạo trong database

### Lỗi database:
- Kiểm tra MySQL đang chạy
- Kiểm tra connection string trong application.properties

## 📝 Notes

- Hệ thống tương thích ngược - UI giữ nguyên trải nghiệm người dùng
- JWT được lưu trong HTTP-only cookie cho bảo mật cao
- Hỗ trợ cả JWT header cho API và JWT cookie cho web
- Token tự động refresh khi user tương tác với hệ thống
- Logout sẽ xóa hoàn toàn JWT cookie

## 🎯 Kết luận

Hệ thống đã được chuyển đổi thành công từ xác thực truyền thống sang JWT authentication nhưng vẫn giữ nguyên trải nghiệm người dùng quen thuộc. Bạn có thể:

✅ Sử dụng như ứng dụng web thông thường (đăng nhập qua form)
✅ Tích hợp API JWT cho client/mobile apps  
✅ Bảo mật cao với HTTP-only cookies
✅ Khả năng mở rộng tốt cho các hệ thống khác

**Chúc bạn sử dụng hệ thống hiệu quả!** 🚀
