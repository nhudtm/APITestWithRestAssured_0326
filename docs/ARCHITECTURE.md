# Kiến trúc dự án

Tài liệu này mô tả kiến trúc và luồng thực thi của REST Assured API Test Framework.

## Kiến trúc tổng thể

```mermaid
flowchart TB
    CMD[Maven / Command Line]
    POM[pom.xml]
    SUREFIRE[Maven Surefire]

    subgraph TESTNG["TestNG Test Suites"]
        CRUD[testng.xml]
        E2E[testng_e2e.xml]
    end

    subgraph TESTS["Test Layer"]
        AUTH[TestCreateToken]
        BOOKING[TestCreateBooking]
        HEADER[TestCreateBookingHeaderPayload]
        FLOW[TestIntegrationFlow1]
        DP[BookingDataProviders]
    end

    subgraph FRAMEWORK["Framework Support"]
        BASE[BaseTest]
        AUTHAPI[AuthAPI]
        BOOKINGAPI[BookingAPI]
        REST[RestClient]
        SPEC[RequestSpecFactory]
        ASSERT[AssertActions]
        FACTORY[PayloadFactory]
        POJO[Request / Response POJO]
        JSON[BuildBodyJSON]
    end

    API[Restful Booker API]
    ALLURE[Allure Results]
    REPORT[Allure Report]

    CMD --> POM
    POM --> SUREFIRE
    SUREFIRE --> CRUD
    SUREFIRE --> E2E

    CRUD --> AUTH
    CRUD --> BOOKING
    CRUD --> HEADER
    E2E --> FLOW

    DP --> BOOKING
    AUTH --> BASE
    BOOKING --> BASE
    HEADER --> BASE
    FLOW --> BASE

    BASE --> AUTHAPI
    BASE --> BOOKINGAPI
    BASE --> FACTORY
    BASE --> ASSERT
    BASE --> SPEC

    AUTHAPI --> REST
    BOOKINGAPI --> REST
    FACTORY --> POJO
    FACTORY --> JSON
    REST --> JSON
    SPEC --> REST

    REST -->|HTTP GET / POST / PUT / DELETE| API
    API -->|Response| REST
    REST --> POJO
    POJO --> ASSERT

    TESTNG --> ALLURE
    TESTS --> ALLURE
    ALLURE --> REPORT
```

## Luồng thực thi một testcase

```mermaid
sequenceDiagram
    participant Maven as Maven Surefire
    participant Suite as TestNG Suite
    participant Test as Testcase
    participant Base as BaseTest
    participant API as BookingAPI/AuthAPI
    participant Client as RestClient
    participant Server as Restful Booker
    participant Allure as Allure

    Maven->>Suite: Chọn testng.xml hoặc testng_e2e.xml
    Suite->>Test: Chạy testcase theo group, priority hoặc dependency
    Suite->>Base: Kích hoạt lifecycle TestNG
    Base->>Base: @BeforeSuite cleanup report
    Base->>Base: @BeforeMethod tạo RestClient, API và PayloadFactory
    Test->>API: Gọi create/get/update/delete/auth
    API->>Client: Chuyển request qua REST client
    Client->>Server: Gửi HTTP request
    Server-->>Client: HTTP status và response body
    Client-->>API: Response
    API-->>Test: RestAssured Response
    Test->>Test: Assert status, header, body và JSON schema
    Test-->>Allure: Ghi testcase và kết quả
    Base->>Base: @AfterMethod tearDown
    Base->>Base: @AfterClass reset RestAssured
```

## Luồng E2E

```mermaid
flowchart LR
    C[Create Booking] -->|bookingId| V[Verify Booking]
    V -->|bookingId| U[Update Booking]
    U -->|token + bookingId| D[Delete Booking]
    D --> X[Verify Booking 404]
```

Các bước E2E được liên kết bằng `dependsOnMethods` và chia sẻ dữ liệu qua `ITestContext`.
