# REST Assured API Framework

Framework automation API dùng **Java + REST Assured + TestNG + Allure** để test các endpoint của [Restful Booker](https://restful-booker.herokuapp.com).

## 1) Tech stack

- Java 17
- Maven
- REST Assured 5.5.1
- TestNG 7.10.2
- Allure (`allure-testng` + `allure-maven`)
- Gson, AssertJ, JavaFaker

## 2) Cấu trúc project

```text
src/main/java/com/heroku_app/
├─ commons/              # Constants chung
├─ config/               # PropertiesConfig
├─ endpoints/            # API paths
├─ utils/                # BuildBodyJSON
└─ pojos/
   ├─ request/           # Booking, Bookingdates, Auth
   └─ reponse/           # BookingResponse, TokenResponse

src/test/java/com/heroku_app/
├─ apis/                 # AuthAPI, BookingAPI
├─ asserts/              # AssertActions
├─ base/                 # BaseTest
├─ client/               # RestClient
├─ dataproviders/        # DataProvider cho booking test
├─ factories/            # PayloadFactory
├─ spec/                 # RequestSpecFactory
└─ tests/
   ├─ crud/
   │  ├─ TestCreateToken.java
   │  ├─ TestCreateBooking.java
   │  └─ TestCreateBookingHeaderPayload.java
   └─ e2e_integration/
      └─ TestIntegrationFlow1.java

src/test/resources/
├─ testng.xml            # Suite CRUD mặc định
├─ testng_e2e.xml        # Suite E2E
├─ config.properties     # Cấu hình mặc định cho môi trường test
└─ schemas/
   └─ booking-response-schema.json
```

## 3) Sơ đồ kiến trúc

Xem chi tiết sơ đồ kiến trúc, luồng thực thi và flow E2E tại [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

## 4) Danh sách testcase

### 4.1) Authentication

| ID | Testcase | Group | Priority | Mô tả |
|---|---|---|---:|---|
| TC-AUTH-001 | `testTokenPOST` | `reg` | 1 | Tạo token bằng thông tin đăng nhập hợp lệ và xác minh token không null |

### 4.2) Create Booking

Các testcase được phân theo group nghiệp vụ và vẫn giữ `priority` để TestNG xác định thứ tự ưu tiên chạy trong cùng suite/class.

| ID | Testcase | Group | Priority | Mô tả |
|---|---|---|---:|---|
| TC-BOOKING-001 | `createBooking_withMinimumValidPayload_shouldSucceed` | `smoke` | 1 | Tạo booking với payload tối thiểu |
| TC-BOOKING-002 | `createBooking_withFullValidPayload_shouldSucceed` | `happy_path` | 1 | Tạo booking với payload đầy đủ và dữ liệu hợp lệ |
| TC-BOOKING-003 | `createBooking_shouldReturnValidResponseSchema` | `happy_path` | 2 | Kiểm tra JSON response theo schema |
| TC-BOOKING-006 | `createBooking_shouldStoreRetrievableBookingData` | `happy_path` | 2 | Tạo booking rồi truy xuất và kiểm tra dữ liệu |
| TC-BOOKING-007 | `createBooking_withMissingRequiredField_shouldBeRejected` | `required_fields` | 1 | Kiểm tra payload thiếu required field |
| TC-BOOKING-008 | `createBooking_withValidBoundaryValues_shouldSucceed` | `boundary_values` | 1 | Tạo booking với giá trị biên hợp lệ |
| TC-BOOKING-009 | `createBooking_withInvalidFirstname_shouldBeRejected` | `field_validation` | 4 | Kiểm tra firstname không hợp lệ |
| TC-BOOKING-010 | `createBooking_withInvalidLastname_shouldBeRejected` | `field_validation` | 4 | Kiểm tra lastname không hợp lệ |
| TC-BOOKING-011 | `createBooking_withInvalidTotalPrice_shouldBeRejected` | `field_validation` | 2 | Kiểm tra total price không hợp lệ |
| TC-BOOKING-012 | `createBooking_withInvalidCheckinDate_shouldBeRejected` | `field_validation` | 2 | Kiểm tra check-in date không hợp lệ |
| TC-BOOKING-013 | `createBooking_withInvalidCheckoutDate_shouldBeRejected` | `field_validation` | 2 | Kiểm tra check-out date không hợp lệ |
| TC-BOOKING-014 | `createBooking_withInvalidBookingDates_shouldBeRejected` | `field_validation` | 2 | Kiểm tra cấu trúc booking dates không hợp lệ |
| TC-BOOKING-015 | `createBooking_withCheckinAfterCheckout_shouldBeRejected` | `field_validation` | 2 | Kiểm tra check-in không được sau check-out |
| TC-BOOKING-016 | `createBooking_withInvalidDepositPaid_shouldBeRejected` | `field_validation` | 2 | Kiểm tra deposit paid không hợp lệ |
| TC-BOOKING-017 | `createBooking_withInvalidAdditionalNeeds_shouldBeRejected` | `field_validation` | 2 | Kiểm tra additional needs không hợp lệ |
| TC-BOOKING-004 | `createBooking_shouldReturnInputData` | `data_integrity` | 3 | Kiểm tra response khớp dữ liệu đầu vào |
| TC-BOOKING-005 | `createBooking_withSamePayload_shouldCreateDifferentBookings` | `data_integrity` | 2 | Tạo hai booking bằng cùng payload và kiểm tra booking ID khác nhau |
| TC-BOOKING-018 | `createBooking_withClientBookingId_shouldIgnoreItAndGenerateNewId` | `data_integrity` | 2 | Bỏ qua booking ID do client gửi |
| TC-BOOKING-019 | `createBooking_withExistingBookingId_shouldNotOverwriteExistingBooking` | `data_integrity` | 2 | Không ghi đè booking hiện có |

Các group là: `smoke`, `happy_path`, `required_fields`, `boundary_values`, `field_validation` và `data_integrity`. Các testcase sử dụng `BookingDataProviders` có thể sinh nhiều testcase con từ data provider.

### 4.3) Header, Payload và HTTP Method

| ID | Testcase | Group | Priority | Mô tả |
|---|---|---|---:|---|
| TC-HEADER-001 | `createBooking_withApplicationJsonContentType_shouldSucceed` | `header_content_type` | 1 | Tạo booking với `application/json` |
| TC-HEADER-002 | `createBooking_withInvalidContentType_shouldBeRejected` | `header_content_type` | 2 | Kiểm tra `Content-Type` không hợp lệ |
| TC-HEADER-003 | `createBooking_withoutContentType_shouldBeRejected` | `header_content_type` | 2 | Kiểm tra request thiếu `Content-Type` |
| TC-HEADER-004 | `createBooking_withNonJsonAccept_shouldBeRejected` | `header_content_type` | 2 | Kiểm tra `Accept` không phải `application/json` |
| TC-HEADER-005 | `createBooking_withUnexpectedCustomHeader_shouldBeStable` | `header_content_type` | 3 | Gửi custom header |
| TC-HEADER-006 | `createBooking_shouldReturnExpectedResponseHeaders` | `header_content_type` | 3 | Kiểm tra response headers |
| TC-HEADER-007 | `createBooking_withoutAcceptHeader_shouldBeHandled` | `header_content_type` | 2 | Kiểm tra request thiếu `Accept` |
| TC-HTTP-001 | `createBookingEndpoint_withPutMethod_shouldBeRejected` | `http_method` | 3 | Gửi PUT tới create-booking endpoint |
| TC-HTTP-002 | `createBookingEndpoint_withPatchMethod_shouldBeRejected` | `http_method` | 3 | Gửi PATCH tới create-booking endpoint |
| TC-HTTP-003 | `createBookingEndpoint_withDeleteMethod_shouldBeRejected` | `http_method` | 3 | Gửi DELETE tới create-booking collection endpoint |
| TC-PAYLOAD-001 | `createBooking_withEmptyPayload_shouldBeRejected` | `payload_integrity` | 4 | Gửi payload rỗng |
| TC-PAYLOAD-002 | `createBooking_withMalformedJson_shouldBeRejected` | `payload_integrity` | 4 | Gửi malformed JSON |
| TC-PAYLOAD-003 | `createBooking_withWrongJsonStructure_shouldBeRejected` | `payload_integrity` | 4 | Gửi JSON sai cấu trúc |
| TC-PAYLOAD-004 | `createBooking_withExtraUnexpectedFields_shouldBeRejected` | `payload_integrity` | 4 | Gửi field không mong muốn |
| TC-PAYLOAD-005 | `createBooking_withJsonArray_shouldBeRejected` | `payload_integrity` | 4 | Gửi JSON array thay vì booking object |
| TC-PAYLOAD-006 | `createBooking_withNullJsonPayload_shouldBeRejected` | `payload_integrity` | 4 | Gửi JSON `null` |
| TC-SECURITY-001 | `createBooking_withSqlAndXssStrings_shouldBeHandledSafely` | `security_input` | 4 | Kiểm tra chuỗi SQL và XSS-like |

### 4.4) E2E Booking Flow

| Bước | Testcase | Dependency | Mô tả |
|---:|---|---|---|
| 1 | `testCreateBooking` | - | Tạo booking bằng payload Faker và lưu booking ID cùng payload trong field của test class |
| 2 | `testVerifyBookingId` | `testCreateBooking` | Truy xuất booking vừa tạo |
| 3 | `testUpdateBookingByID` | `testVerifyBookingId` | Gọi `getAuthToken()`, cập nhật booking bằng payload mới và xác minh dữ liệu |
| 4 | `testDeleteBookingById` | `testUpdateBookingByID` | Xóa booking bằng token và xác minh GET trả 404 |

Các bước E2E dùng `dependsOnMethods` để giữ thứ tự phụ thuộc. Booking ID và payload được lưu trong field của `TestIntegrationFlow1`; auth token được quản lý qua field `token` của `BaseTest`. Test cleanup `@AfterClass(alwaysRun = true)` sẽ xóa booking nếu booking chưa được xóa trước đó.

### Kết quả xác nhận

Sau khi sửa luồng auth và dữ liệu E2E, suite đã được chạy bằng:

```bash
mvn clean test -Dtest=TestIntegrationFlow1 -DsuiteXmlFile=src/test/resources/testng_e2e.xml
```

Kết quả:

```text
Tests run: 4
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## 5) Cấu hình và chạy test

### Yêu cầu

- JDK 17 trở lên
- Maven 3.8 trở lên
- Kết nối mạng tới [Restful Booker](https://restful-booker.herokuapp.com)

### Cấu hình môi trường

Cấu hình mặc định nằm trong [config.properties](src/test/resources/config.properties):

```properties
base.url=https://restful-booker.herokuapp.com
username=admin
password=password123
```

`PropertiesConfig` đọc giá trị theo thứ tự ưu tiên:

1. Java system property, ví dụ `-Dbase.url=https://...`
2. Environment variable cùng tên, ví dụ `base.url`
3. Environment variable dạng chuẩn hóa, ví dụ `BASE_URL`
4. Giá trị trong `config.properties`

Không commit credentials thật vào repository. Khi chạy CI/CD, nên truyền `username` và `password` qua system properties hoặc environment variables.

POM sử dụng `${xml}.xml` làm tên suite TestNG. Giá trị mặc định của Maven profile là `testng`, tương ứng [testng.xml](src/test/resources/testng.xml).

### Chạy suite mặc định

```bash
mvn clean test
```

Suite mặc định hiện gồm:

- `TestCreateToken`
- `TestCreateBooking`
- `TestCreateBookingHeaderPayload`

### Chạy suite E2E

```bash
mvn clean test -Dxml=testng_e2e
```

Hoặc chỉ định trực tiếp suite bằng Surefire khi cần:

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng_e2e.xml
```

### Chạy một test class

```bash
mvn clean test -Dtest=TestCreateBooking
```

## 6) Allure report

Kết quả Allure được ghi vào:

- `target/allure-results`

Tạo report tĩnh:

```bash
mvn allure:report
```

Mở report trên macOS:

```bash
open target/site/allure-maven-plugin/index.html
```

Hoặc mở nhanh bằng server tạm:

```bash
mvn allure:serve
```

## 7) Ghi chú quan trọng

- File `restassured-api-framework.SKILL.md` trong project ban đầu được tải từ **QAskill.sh** để làm tài liệu tham khảo cho framework.
- Project sử dụng package `com.heroku_app...` trong source và TestNG suite.
