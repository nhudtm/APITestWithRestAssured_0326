# REST Assured API Framework

Framework automation API dùng **Java + REST Assured + TestNG + Allure** để test các endpoint của [Restful Booker](https://restful-booker.herokuapp.com).

## 1) Tech stack

- Java 17
- Maven
- REST Assured 5.5.1
- TestNG 7.10.2
- Allure (`allure-testng` + `allure-maven`)
- Gson, AssertJ, JavaFaker

## 2) Cấu trúc chính

```text
src/main/java/com/heroku_app
	├─ endpoints/        # API paths
	├─ modules/          # PayloadManager
	├─ pojos/            # Request/Response POJOs
	└─ commons/          # Constants

src/test/java/com/heroku_app
	├─ base/             # BaseTest setup
	├─ asserts/          # AssertActions
	└─ tests/
			├─ crud/         # Health check, token, create booking
			└─ e2e_integration/

src/test/resources
	├─ data.properties
	└─ schemas/
```

## 3) Chạy test

### Chạy suite mặc định (testng.xml)

```bash
mvn clean test
```

### Chạy suite E2E (testng_e2e.xml)

```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng_e2e.xml
```

## 4) Allure report

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

## 5) Ghi chú quan trọng

- File `restassured-api-framework.SKILL.md` trong project ban đầu được tải từ **QAskill.sh** để làm tài liệu tham khảo cho framework.
- Project đang dùng package thực tế `com.heroku_app...` trong test suites (`src/test/resources/testng.xml`, `src/test/resources/testng_e2e.xml`).