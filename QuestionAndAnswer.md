# Tổng hợp Q&A: Gson Annotations và POJO

## 1) `@SerializedName` và `@Expose` để làm gì?

- `@SerializedName("bookingid")`: map key JSON `bookingid` với field Java tương ứng.
- `@Expose`: đánh dấu field được phép tham gia serialize/deserialize.

Ví dụ trong `BookingResponse`:

```java
@SerializedName("bookingid")
@Expose
private Integer bookingid;

@SerializedName("booking")
@Expose
private Booking booking;
```

Ý nghĩa:
- `bookingid` đọc/ghi từ key JSON `"bookingid"`.
- `booking` đọc/ghi từ key JSON `"booking"`.

> Lưu ý: `@Expose` chỉ có hiệu lực khi khởi tạo Gson bằng `GsonBuilder().excludeFieldsWithoutExposeAnnotation()`.

---

## 2) Ví dụ cụ thể chỗ dùng phần định nghĩa Gson này

### Deserialization (JSON -> Object)

```java
String json = """
{
  \"bookingid\": 1234,
  \"booking\": {
    \"firstname\": \"Nhu\",
    \"lastname\": \"Tran\"
  }
}
""";

Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create();

BookingResponse res = gson.fromJson(json, BookingResponse.class);
```

### Serialization (Object -> JSON)

```java
BookingResponse res = new BookingResponse();
res.setBookingid(5678);
res.setBooking(new Booking());

Gson gson = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .create();

String out = gson.toJson(res);
```

---

## 3) Nếu không dùng `@SerializedName` và `@Expose` thì khác gì với `new Gson()`?

Ví dụ chuẩn:

```java
Gson gson = new Gson();
BookingResponse res = gson.fromJson(json, BookingResponse.class);
```

Khác biệt chính:

- Không có `@SerializedName`:
  - Gson map theo **tên field Java mặc định**.
  - Nếu field Java khác tên JSON key thì có thể không map đúng (thường nhận `null`).

- Có `@Expose` nhưng vẫn dùng `new Gson()`:
  - Gần như **không có tác dụng lọc field**.
  - Muốn `@Expose` có tác dụng, cần:
    - `new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();`

---

## 4) Có phải field Java khác JSON key thì cần `@SerializedName`?

Đúng.

Ví dụ:

```java
@SerializedName("bookingid")
private Integer bookingId;
```

- Field Java: `bookingId`
- JSON key: `bookingid`

Nếu không có `@SerializedName`, Gson mặc định sẽ tìm `bookingId` trong JSON.

---

## 5) Khi nào nên tạo data dưới dạng POJO?

Nên dùng POJO khi:

- Dữ liệu có cấu trúc rõ ràng (API JSON/XML/DB record).
- Cần type-safety và tránh lỗi runtime từ `Map<String, Object>`.
- Dữ liệu dùng lại ở nhiều nơi (client/service/test).
- Muốn serialize/deserialize ổn định, dễ maintain, dễ refactor.

Không nhất thiết dùng POJO khi:

- Dữ liệu rất đơn giản, dùng 1 lần, ngắn hạn.
- Schema thay đổi liên tục, chưa ổn định.
- Chỉ cần forward/log raw JSON mà không xử lý field.

---

## Kết luận nhanh

- Trùng tên field Java và JSON key: có thể không cần `@SerializedName`.
- Khác tên: nên dùng `@SerializedName` để map chắc chắn.
- `@Expose` chỉ hữu ích khi Gson được tạo bằng `excludeFieldsWithoutExposeAnnotation()`.
- Với model dùng lâu dài và dữ liệu nhiều lớp, POJO là lựa chọn nên ưu tiên.

---

## 6) `validatableResponse` dùng để làm gì? Có dư không?

Ví dụ trong test:

```java
validatableResponse = response.then().log().all();
validatableResponse.statusCode(200);
```

- `validatableResponse` là object của RestAssured để chain các assert (status/header/body).
- Trong đoạn trên, biến này chỉ dùng 1 lần nên có thể xem là hơi dư.
- Có thể viết gọn tương đương:

```java
response.then().log().all().statusCode(200);
```

Khi nào nên giữ `validatableResponse`:
- Khi cần nhiều assert liên tiếp trên cùng response.
- Khi muốn style test đồng nhất trong framework.

---

## 7) `verbose="2"` trong `testng.xml` nghĩa là gì?

- Đây là mức độ chi tiết log của TestNG khi chạy test.
- Số càng lớn thì output càng chi tiết.
- `2` là mức trung bình, đủ để theo dõi tiến trình test rõ hơn mặc định.

Thực tế hay dùng:
- `0` hoặc `1`: ít log.
- `2` đến `4`: mức debug vừa phải.
- Cao hơn: rất chi tiết, hữu ích khi debug sâu.

---

## 8) Vì sao chạy test xong có data trong `allure-results`? Config ở đâu?

Do project đã cấu hình Allure cho TestNG trong `pom.xml`:

- Có dependency `allure-testng` để adapter lắng nghe test events và ghi kết quả.
- Có cấu hình system property:

```xml
<allure.results.directory>${project.build.directory}/allure-results</allure.results.directory>
```

Property này được truyền bởi `maven-surefire-plugin` khi chạy test Maven.

Lưu ý:
- Chạy bằng Maven thường ghi vào `target/allure-results`.
- Chạy trực tiếp từ IDE có thể sinh `allure-results` ở root nếu không override property.

---

## 9) Tại sao trong `BaseTest` dùng `@BeforeMethod` thay vì `@BeforeTest` hoặc `@BeforeClass`?

Lý do chính: đảm bảo **isolation** cho từng test case.

- `@BeforeMethod`: chạy trước mỗi `@Test`, mỗi test có state mới, giảm rò rỉ state.
- `@BeforeClass`: chỉ chạy 1 lần mỗi class, dễ shared-state nếu object bị mutate.
- `@BeforeTest`: scope rộng hơn (theo `<test>` trong `testng.xml`), dễ ảnh hưởng chéo nhiều class.

Trong API automation, ưu tiên độc lập giữa test nên `@BeforeMethod` thường an toàn hơn.

## 10) Công cụ chuyển json response thành json schema
https://transform.tools/json-to-json-schema
- Copy json reponse vào và chuyển thành json schema để verify