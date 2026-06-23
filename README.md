# 3pm-api-toko

**Version:** `2.0.0`
**Project Type:** Backend Developer Technical Test Submission
**Java:** `OpenJDK 17`
**Build Tool:** `Apache Maven`
**Database:** `PostgreSQL`

## Tech Stack

* Java 17
* Spring Boot 3.5.x
* Spring Data JPA
* Hibernate
* PostgreSQL
* Flyway
* Spring Security
* JWT Authentication
* Swagger / OpenAPI
* Lombok
* JUnit 5
* Mockito

## Overview

API Toko adalah aplikasi backend sederhana yang dibuat untuk memenuhi kebutuhan Technical Test Backend Developer.

Aplikasi ini menyediakan fitur:

* Authentication menggunakan JWT
* CRUD Master Barang
* Manajemen Stok
* Transaksi Penjualan
* Laporan 5 Barang Terlaris
* Laporan 5 Barang Paling Menguntungkan
* Laporan Penjualan yang Mengandung 5 Barang Paling Menguntungkan

Sistem dibangun menggunakan arsitektur REST API dengan Spring Boot dan PostgreSQL sebagai database utama.

Nomor transaksi penjualan dibuat otomatis dengan format:

```text
AAyyyyMMdd0001
```

Contoh:

```text
AA202606230001
```

## Visuals ERD

Berikut adalah ERD (Entity Relationship Diagram) untuk sistem **3pm-api-toko**:

![ERD Event Booking](docs/ERD_Toko.png)



## Database Setup

Bagian ini menjelaskan setup PostgreSQL yang dibutuhkan agar **3pm-api-toko** dapat berjalan dengan baik pada local environment.

Masuk ke PostgreSQL terlebih dahulu:

```bash
sudo -u postgres psql
```

Setelah masuk ke PostgreSQL Console, jalankan perintah berikut:

```sql
CREATE DATABASE db_toko;

CREATE USER toko_user WITH PASSWORD 'toko_password';

GRANT ALL PRIVILEGES ON DATABASE db_toko TO toko_user;
```

Masuk ke database:

```sql
\c db_toko
```

Berikan privilege pada schema:

```sql
GRANT ALL ON SCHEMA public TO toko_user;

GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO toko_user;

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO toko_user;
```

### Keterangan

* `db_toko` merupakan database utama aplikasi.
* `toko_user` merupakan database user yang digunakan oleh aplikasi Spring Boot.
* Konfigurasi database harus sesuai dengan nilai yang terdapat pada `application.yml`.

Project ini menggunakan **Flyway Migration**, sehingga seluruh tabel akan dibuat secara otomatis saat aplikasi dijalankan.

---

### Flyway Migration

Lokasi migration:

```text
src/main/resources/db/migration
```

Migration yang tersedia:

```text
V1__create_initial_schema.sql
V2__insert_initial_data.sql
```

---

### Database Schema

#### users

Menyimpan data pengguna yang dapat mengakses aplikasi.

| Column     | Description              |
| ---------- | ------------------------ |
| id         | Primary Key              |
| username   | Username Login           |
| password   | Password BCrypt          |
| role       | Role User (ADMIN / USER) |
| created_at | Waktu pembuatan data     |
| updated_at | Waktu update data        |

#### products

Menyimpan data master barang.

| Column         | Description      |
| -------------- | ---------------- |
| id             | Primary Key      |
| sku            | Kode Barang      |
| product_name   | Nama Barang      |
| purchase_price | Harga Modal      |
| selling_price  | Harga Jual       |
| description    | Deskripsi Barang |

#### stocks

Menyimpan stok barang.

| Column     | Description      |
| ---------- | ---------------- |
| id         | Primary Key      |
| product_id | Referensi Barang |
| quantity   | Jumlah Stok      |

#### sales

Menyimpan header transaksi penjualan.

| Column         | Description            |
| -------------- | ---------------------- |
| id             | Primary Key            |
| transaction_no | Nomor Transaksi        |
| sale_date      | Tanggal Penjualan      |
| total_amount   | Total Penjualan        |
| created_by     | User Pembuat Transaksi |

#### sale_details

Menyimpan detail item transaksi.

| Column         | Description         |
| -------------- | ------------------- |
| id             | Primary Key         |
| sale_id        | Referensi Penjualan |
| product_id     | Referensi Barang    |
| quantity       | Jumlah Barang       |
| purchase_price | Harga Modal         |
| selling_price  | Harga Jual          |
| subtotal       | Total Harga         |
| profit         | Keuntungan          |

---

### Initial Data

Migration `V2__insert_initial_data.sql` akan membuat data awal berupa:

#### Default User

| Username | Password | Role  |
| -------- | -------- | ----- |
| admin    | admin123 | ADMIN |

#### Master Barang

Sebanyak 10 data barang contoh:

* Indomie Goreng
* Aqua 600ml
* Teh Botol Sosro
* Kopi Kapal Api
* Biskuit Roma
* Susu Ultra 250ml
* Chitato 68g
* Roti Tawar
* Sabun Lifebuoy
* Shampoo Sunsilk

#### Initial Stock

Seluruh barang akan mendapatkan stok awal:

```text
100 pcs
```

---

### Database Flow

Secara umum alur database pada aplikasi ini adalah:

1. Membuat database PostgreSQL.
2. Menjalankan aplikasi Spring Boot.
3. Flyway mengeksekusi migration secara otomatis.
4. Tabel dan data awal dibuat otomatis.
5. User melakukan login menggunakan akun admin.
6. User dapat mengelola master barang.
7. User dapat melakukan transaksi penjualan.
8. Sistem akan mengurangi stok secara otomatis.
9. Data transaksi digunakan untuk menghasilkan laporan penjualan dan profit.

---

### Transaction Number Format

Nomor transaksi penjualan dibuat secara otomatis dengan format:

```text
AAyyyyMMdd0001
```

Contoh:

```text
AA202606230001
AA202606230002
AA202606230003
```

Format tersebut terdiri dari:

| Bagian   | Keterangan            |
| -------- | --------------------- |
| AA       | Prefix Transaksi      |
| yyyyMMdd | Tanggal Transaksi     |
| 0001     | Running Number Harian |
|          |                       |



## Architecture

```text
     ┌────────────────────┐
     │  Client Layer      │
     │                    │
     │ - Swagger UI       │
     │ - Postman          │
     │ - REST Client      │
     └─────────┬──────────┘
               │ HTTP Request
               ▼
   ┌─────────────────────────┐
   │    Controller Layer     │
   │                         │
   │ - AuthController        │
   │ - ProductController     │
   │ - StockController       │
   │ - SaleController        │
   │ - ReportController      │
   └───────────┬─────────────┘
               │
               ▼
   ┌─────────────────────────┐
   │    Security Layer       │
   │                         │
   │ - Spring Security       │
   │ - JWT Authentication    │
   │ - Authorization Filter  │
   └───────────┬─────────────┘
               │
               ▼
     ┌────────────────────┐
     │   Service Layer    │
     │                    │
     │ - AuthService      │
     │ - ProductService   │
     │ - StockService     │
     │ - SaleService      │
     │ - ReportService    │
     └─────────┬──────────┘
               │
               ▼
   ┌──────────────────────────┐
   │   Repository Layer       │
   │                          │
   │ - UsersRepository        │
   │ - ProductsRepository     │
   │ - StocksRepository       │
   │ - SalesRepository        │
   │ - SaleDetailsRepository  │
   └───────────┬──────────────┘
               │
               ▼
      ┌──────────────────┐
      │  Database Layer  │
      │                  │
      │ - users          │
      │ - products       │
      │ - stocks         │
      │ - sales          │
      │ - sale_details   │
      └──────────────────┘
```

### Architecture Summary

| Step | Component        | Description                                                      |
| ---- | ---------------- | ---------------------------------------------------------------- |
| 1    | Client Layer     | User mengakses API melalui Swagger UI, Postman, atau REST Client |
| 2    | Controller Layer | Menerima HTTP Request dan melakukan validasi request             |
| 3    | Security Layer   | Memverifikasi JWT Token dan melakukan authorization              |
| 4    | Service Layer    | Menjalankan business logic aplikasi                              |
| 5    | Repository Layer | Mengakses database menggunakan Spring Data JPA                   |
| 6    | Database Layer   | Menyimpan data master barang, stok, penjualan, dan laporan       |


## Architecture Diagram

See:

- [docs/sequence-diagram.md](docs/sequence-diagram.md)


## Folder Structure

```text
threepm-api-toko
├── docs
│   └── sequence-diagram.md
├── logs
│   ├── error.log
│   └── trail.log
├── src
│   ├── main
│   │   ├── java/com/threepm/api/toko
│   │   │     ├── Config
│   │   │     │   ├── OpenApiConfig.java
│   │   │     │   └── SecurityConfig.java
│   │   │     │
│   │   │     ├── Controller
│   │   │     │   ├── AuthController.java
│   │   │     │   ├── ProductController.java
│   │   │     │   ├── ReportController.java
│   │   │     │   ├── SaleController.java
│   │   │     │   └── StockController.java
│   │   │     │
│   │   │     ├── Model
│   │   │     │   ├── Entity
│   │   │     │   │   ├── Users.java
│   │   │     │   │   ├── Products.java
│   │   │     │   │   ├── Stocks.java
│   │   │     │   │   ├── Sales.java
│   │   │     │   │   └── SaleDetails.java
│   │   │     │   │
│   │   │     │   ├── Request
│   │   │     │   │   ├── LoginRequest.java
│   │   │     │   │   ├── ProductRequest.java
│   │   │     │   │   ├── SaleRequest.java
│   │   │     │   │   └── SaleDetailRequest.java
│   │   │     │   │
│   │   │     │   └── Response
│   │   │     │       ├── BaseResponse.java
│   │   │     │       ├── LoginResponse.java
│   │   │     │       ├── ProductResponse.java
│   │   │     │       ├── StockResponse.java
│   │   │     │       ├── SaleResponse.java
│   │   │     │       ├── SaleDetailResponse.java
│   │   │     │       ├── TopSellingProductResponse.java
│   │   │     │       ├── TopProfitProductResponse.java
│   │   │     │       └── ProfitSaleResponse.java
│   │   │     │
│   │   │     ├── Repository
│   │   │     │   ├── UsersRepository.java
│   │   │     │   ├── ProductsRepository.java
│   │   │     │   ├── StocksRepository.java
│   │   │     │   ├── SalesRepository.java
│   │   │     │   └── SaleDetailsRepository.java
│   │   │     │
│   │   │     ├── Service
│   │   │     │   ├── AuthService.java
│   │   │     │   ├── ProductService.java
│   │   │     │   ├── StockService.java
│   │   │     │   ├── SaleService.java
│   │   │     │   ├── ReportService.java
│   │   │     │   │
│   │   │     │   └── impl
│   │   │     │       ├── AuthServiceImpl.java
│   │   │     │       ├── ProductServiceImpl.java
│   │   │     │       ├── StockServiceImpl.java
│   │   │     │       ├── SaleServiceImpl.java
│   │   │     │       └── ReportServiceImpl.java
│   │   │     │
│   │   │     ├── Util
│   │   │     │   └── JwtUtil.java
│   │   │     │
│   │   │     └── ApiTokoApplication.java
│   │   │
│   │   └── resources
│   │       ├── application.yml
│   │       ├── banner.txt
│   │       └── db
│   │           └── migration
│   │               ├── V1__create_initial_schema.sql
│   │               └── V2__insert_initial_data.sql
│   │
│   └── test/java/com/threepm/api/toko 
│       └── ApiTokoApplicationTests.java
│
├── pom.xml
├── run.sh
├── README.md
├── HELP.md
├── Test for Backend.pdf
├── mvnw
├── mvnw.cmd
└── .gitignore
```

## Architecture Diagram

See:

- [docs/sequence-diagram.md](docs/sequence-diagram.md)



## Configuration

Bagian ini menjelaskan konfigurasi utama yang digunakan oleh aplikasi **3pm-api-toko**.

### Main Configuration (`application.yml`)

File ini merupakan konfigurasi utama aplikasi yang digunakan saat menjalankan project secara lokal.

```yaml
spring:
  application:
    name: api-toko

  banner:
    location: classpath:banner.txt

  datasource:
    url: jdbc:postgresql://localhost:5432/db_toko
    username: toko_user
    password: toko_password
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

server:
  port: 8080

springdoc:
  swagger-ui:
    path: /swagger-ui.html

  api-docs:
    path: /v3/api-docs

logging:
  level:
    com.threepm.api.toko: DEBUG
    org.springframework.security: INFO

app:
  name: 3PM Backend Test API Toko
  version: 1.0.0

  security:
    jwt-secret: ${JWT_SECRET:3pm-backend-test-secret-key-minimum-32-characters}
    jwt-expiration-ms: ${JWT_EXPIRATION_MS:86400000}
    permit-all: ${APP_SECURITY_PERMIT_ALL:false}
```

### Environment Variables

Berikut adalah environment variable yang dapat digunakan untuk melakukan override konfigurasi aplikasi.

| Variable                | Default Value                                     | Description                                                    |
| ----------------------- | ------------------------------------------------- | -------------------------------------------------------------- |
| JWT_SECRET              | 3pm-backend-test-secret-key-minimum-32-characters | Secret key yang digunakan untuk signing JWT token              |
| JWT_EXPIRATION_MS       | 86400000                                          | Masa berlaku JWT token dalam millisecond (24 jam)              |
| APP_SECURITY_PERMIT_ALL | false                                             | Mengaktifkan atau menonaktifkan security pada seluruh endpoint |

## Application Startup

Bagian ini menjelaskan beberapa cara menjalankan aplikasi, baik untuk local development, mode JWT, maupun container-based run menggunakan Docker.

## Build & Run

Bagian ini menjelaskan cara melakukan build dan menjalankan aplikasi **3pm-api-toko** pada local environment.

### Prerequisites

Pastikan software berikut sudah terinstall:

* Java 17
* Maven 3.8+
* PostgreSQL 17+
* Git

Verifikasi instalasi:

```bash
java -version
mvn -version
psql --version
```

---

### Build Project

Sebelum aplikasi dijalankan, lakukan proses build untuk mengunduh dependency dan menghasilkan executable JAR.

```bash
mvn clean package
```

Jika proses build berhasil, Maven akan menghasilkan file JAR pada folder:

```text
target/api-toko-0.0.1-SNAPSHOT.jar
```

---

### Run Application

Project menyediakan script `run.sh` untuk mempermudah proses build dan startup aplikasi.

Berikan permission terlebih dahulu:

```bash
chmod +x run.sh
```

Jalankan aplikasi:

```bash
./run.sh
```

Script akan melakukan beberapa langkah otomatis:

* Memeriksa apakah port `8080` sedang digunakan
* Menghentikan proses yang menggunakan port tersebut
* Melakukan build Maven
* Mencari executable JAR
* Menjalankan aplikasi Spring Boot

---

### Manual Run

Selain menggunakan script, aplikasi juga dapat dijalankan secara manual:

```bash
mvn clean package
```

```bash
java -jar target/api-toko-0.0.1-SNAPSHOT.jar
```

---

### Application URLs

Setelah aplikasi berhasil berjalan, endpoint berikut dapat diakses:

| Service      | URL                                   |
| ------------ | ------------------------------------- |
| Application  | http://localhost:8080                 |
| Swagger UI   | http://localhost:8080/swagger-ui.html |
| OpenAPI Docs | http://localhost:8080/v3/api-docs     |

---

### Startup Verification

Pastikan aplikasi berhasil berjalan dengan melihat log berikut:

```text
Started ApiTokoApplication
Tomcat started on port 8080
```

Jika log tersebut muncul, maka aplikasi siap digunakan dan seluruh endpoint dapat diakses melalui Swagger UI.


## JWT Authentication

Project ini menggunakan **Spring Security** dan **JWT (JSON Web Token)** untuk mengamankan endpoint API.

Setelah user berhasil login, sistem akan menghasilkan JWT Token yang harus digunakan pada setiap request ke endpoint yang membutuhkan autentikasi.

### Security Rules

#### Public Endpoints

Endpoint berikut dapat diakses tanpa JWT Token:

* `/swagger-ui/**`
* `/swagger-ui.html`
* `/v3/api-docs/**`
* `POST /api/auth/login`

#### Protected Endpoints

Endpoint berikut memerlukan JWT Token:

##### Master Barang

* `GET /api/products`
* `GET /api/products/{id}`
* `POST /api/products`
* `PUT /api/products/{id}`
* `DELETE /api/products/{id}`

##### Stocks

* `GET /api/stocks`
* `GET /api/stocks/{productId}`

##### Sales

* `POST /api/sales`
* `GET /api/sales`
* `GET /api/sales/{id}`

##### Reports

* `GET /api/reports/top-selling-products`
* `GET /api/reports/top-profitable-products`
* `GET /api/reports/sales-containing-top-profitable-products`

---

### Security Configuration

Konfigurasi JWT terdapat pada file:

`src/main/resources/application.yml`

```yaml
app:
  name: 3PM Backend Test API Toko
  version: 1.0.0

  security:
    jwt-secret: ${JWT_SECRET:3pm-backend-test-secret-key-minimum-32-characters}
    jwt-expiration-ms: ${JWT_EXPIRATION_MS:86400000}
    permit-all: ${APP_SECURITY_PERMIT_ALL:false}
```

### Configuration Description

| Property          | Description                                                          |
| ----------------- | -------------------------------------------------------------------- |
| jwt-secret        | Secret key yang digunakan untuk signing dan verifikasi JWT Token     |
| jwt-expiration-ms | Masa berlaku JWT Token dalam millisecond                             |
| permit-all        | Flag untuk mengaktifkan atau menonaktifkan security saat development |

---

### Authentication Flow

1. User melakukan login melalui endpoint:

```http
POST /api/auth/login
```

2. Sistem memverifikasi username dan password.

3. Jika valid, sistem mengembalikan JWT Token.

4. JWT Token digunakan pada request berikutnya melalui header Authorization.

---

### Authorization Header

Gunakan format berikut pada setiap request yang membutuhkan autentikasi:

```text
Authorization: Bearer <your-jwt-token>
```

Contoh:

```text
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9...
```

---

### Swagger Authorization

Swagger UI telah terintegrasi dengan JWT Authentication.

Langkah penggunaan:

1. Login melalui endpoint `POST /api/auth/login`
2. Salin token yang diterima dari response
3. Klik tombol **Authorize** pada Swagger UI
4. Masukkan token dengan format:

```text
Bearer <your-jwt-token>
```

5. Klik **Authorize**
6. Seluruh endpoint yang membutuhkan autentikasi dapat digunakan melalui Swagger UI



## API Documentation

Dokumentasi API tersedia melalui **Swagger UI** setelah aplikasi berhasil dijalankan.

Swagger memudahkan reviewer atau interviewer untuk:

* Melihat daftar endpoint yang tersedia
* Melihat schema request dan response
* Melakukan testing API secara langsung dari browser
* Menggunakan JWT Authentication melalui fitur **Authorize**

### Swagger UI

```text
http://localhost:8080/swagger-ui.html
```

atau

```text
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON

```text
http://localhost:8080/v3/api-docs
```

### Available Server

```text
http://localhost:8080
```

Description:

```text
Local Development
```

### Authentication Flow

1. Login melalui endpoint `/api/auth/login`
2. Copy JWT token dari response
3. Klik tombol **Authorize** pada Swagger UI
4. Masukkan token dengan format:

```text
Bearer <your-jwt-token>
```

5. Seluruh endpoint yang memerlukan autentikasi dapat diakses setelah proses authorize berhasil.

---

## API Endpoints

Berikut endpoint utama yang tersedia pada project **3pm-api-toko**.

### Authentication

| Endpoint          | Method | Description                     |
| ----------------- | ------ | ------------------------------- |
| `/api/auth/login` | POST   | Login dan mendapatkan JWT Token |

---

### Master Barang

| Endpoint             | Method | Description                              |
| -------------------- | ------ | ---------------------------------------- |
| `/api/products`      | GET    | Menampilkan seluruh data barang          |
| `/api/products/{id}` | GET    | Menampilkan detail barang berdasarkan ID |
| `/api/products`      | POST   | Menambahkan data barang baru             |
| `/api/products/{id}` | PUT    | Mengubah data barang                     |
| `/api/products/{id}` | DELETE | Menghapus data barang                    |

---

### Stocks

| Endpoint                  | Method | Description                             |
| ------------------------- | ------ | --------------------------------------- |
| `/api/stocks`             | GET    | Menampilkan seluruh stok barang         |
| `/api/stocks/{productId}` | GET    | Menampilkan stok berdasarkan product ID |

---

### Sales

| Endpoint          | Method | Description                             |
| ----------------- | ------ | --------------------------------------- |
| `/api/sales`      | POST   | Membuat transaksi penjualan             |
| `/api/sales`      | GET    | Menampilkan seluruh transaksi penjualan |
| `/api/sales/{id}` | GET    | Menampilkan detail transaksi penjualan  |

---

### Reports

| Endpoint                                                | Method | Description                                                         |
| ------------------------------------------------------- | ------ | ------------------------------------------------------------------- |
| `/api/reports/top-selling-products`                     | GET    | Menampilkan 5 barang terlaris                                       |
| `/api/reports/top-profitable-products`                  | GET    | Menampilkan 5 barang paling menguntungkan                           |
| `/api/reports/sales-containing-top-profitable-products` | GET    | Menampilkan transaksi yang mengandung 5 barang paling menguntungkan |

---

### Security Notes

* Endpoint login dapat diakses tanpa token.
* Seluruh endpoint selain login memerlukan JWT Authentication.
* Authorization menggunakan Bearer Token.
* Swagger UI telah terintegrasi dengan JWT Authorization melalui tombol **Authorize**.

### HTTP Response

Aplikasi menggunakan format response JSON melalui DTO Response yang telah disediakan pada masing-masing endpoint.

Contoh response login:

```json
{
  "username": "admin",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

## API Usage

### Authentication

### POST /api/auth/login

Endpoint ini digunakan untuk melakukan login dan mendapatkan JWT Token yang digunakan untuk mengakses endpoint yang dilindungi.

#### Request Format

```json
 {
  "username": "admin",
  "password": "admin123"
}
```

#### Response

```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTc4MjE4NzIyOCwiZXhwIjoxNzgyMjczNjI4fQ.Zm8PAyVZEiQRKWNMJAoF0cfJxgogR__QuJMxzXFVphwgHQidiluVXAvB2XrOX-rZ",
  "tokenType": "Bearer",
  "username": "admin",
  "role": "ADMIN"
}
```

---

## Master Barang

### GET /api/products

Endpoint ini digunakan untuk menampilkan seluruh data barang.

#### Request Format

Tidak memerlukan request body.

#### Response

```json
[
  {
    "id": 1,
    "sku": "BRG-001",
    "productName": "Indomie Goreng",
    "purchasePrice": 2500,
    "sellingPrice": 3500,
    "description": "Mie instan goreng",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 2,
    "sku": "BRG-002",
    "productName": "Aqua 600ml",
    "purchasePrice": 3000,
    "sellingPrice": 4500,
    "description": "Air mineral botol 600ml",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 3,
    "sku": "BRG-003",
    "productName": "Teh Botol Sosro",
    "purchasePrice": 3500,
    "sellingPrice": 5000,
    "description": "Minuman teh botol",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 4,
    "sku": "BRG-004",
    "productName": "Kopi Kapal Api",
    "purchasePrice": 1200,
    "sellingPrice": 2000,
    "description": "Kopi sachet",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 5,
    "sku": "BRG-005",
    "productName": "Biskuit Roma",
    "purchasePrice": 6000,
    "sellingPrice": 8500,
    "description": "Biskuit kemasan",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 6,
    "sku": "BRG-006",
    "productName": "Susu Ultra 250ml",
    "purchasePrice": 5000,
    "sellingPrice": 7500,
    "description": "Susu UHT kemasan kecil",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 7,
    "sku": "BRG-007",
    "productName": "Chitato 68g",
    "purchasePrice": 8000,
    "sellingPrice": 12000,
    "description": "Snack kentang kemasan",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 8,
    "sku": "BRG-008",
    "productName": "Roti Tawar",
    "purchasePrice": 11000,
    "sellingPrice": 15000,
    "description": "Roti tawar kemasan",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 9,
    "sku": "BRG-009",
    "productName": "Sabun Lifebuoy",
    "purchasePrice": 3500,
    "sellingPrice": 6000,
    "description": "Sabun mandi batang",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  },
  {
    "id": 10,
    "sku": "BRG-010",
    "productName": "Shampoo Sunsilk",
    "purchasePrice": 9000,
    "sellingPrice": 13500,
    "description": "Shampoo botol kecil",
    "createdAt": "2026-06-23T11:22:22.516665",
    "updatedAt": "2026-06-23T11:22:22.516665"
  }
]
```

### GET /api/products/{id}

Endpoint ini digunakan untuk menampilkan detail barang berdasarkan ID.

#### Request Format

```text
GET /api/products/1
```

#### Response

```json
{
  "id": 1,
  "sku": "BRG-001",
  "productName": "Indomie Goreng",
  "purchasePrice": 2500,
  "sellingPrice": 3500,
  "description": "Mie instan goreng",
  "createdAt": "2026-06-23T11:22:22.516665",
  "updatedAt": "2026-06-23T11:22:22.516665"
}
```

### POST /api/products

Endpoint ini digunakan untuk menambahkan barang baru.

#### Request Format

```json
{
  "sku": "BRG-011",
  "productName": "Pocari Sweat",
  "purchasePrice": 5000,
  "sellingPrice": 7000,
  "description": "Minuman isotonik"
}
```

#### Response

```json
{
  "id": 11,
  "sku": "BRG-011",
  "productName": "Pocari Sweat",
  "purchasePrice": 5000,
  "sellingPrice": 7000,
  "description": "Minuman isotonik",
  "createdAt": "2026-06-23T11:34:00.837158",
  "updatedAt": "2026-06-23T11:34:00.83719"
}
```

### PUT /api/products/{id}

Endpoint ini digunakan untuk memperbarui data barang.

#### Request Format

```json
{
  "sku": "BRG-011",
  "productName": "Pocari Sweat 500ml",
  "purchasePrice": 5500,
  "sellingPrice": 7500,
  "description": "Updated Product"
}
```

#### Response

```json
{
  "id": 11,
  "sku": "BRG-011",
  "productName": "Pocari Sweat 500ml",
  "purchasePrice": 5500,
  "sellingPrice": 7500,
  "description": "Updated Product",
  "createdAt": "2026-06-23T11:34:00.837158",
  "updatedAt": "2026-06-23T11:34:00.83719"
}
```

### DELETE /api/products/{id}

Endpoint ini digunakan untuk menghapus data barang.

#### Request Format

```text
DELETE /api/products/11
```

#### Response

```json
{
  "message": "Product deleted successfully"
}
```

---

## Stocks

### GET /api/stocks

Endpoint ini digunakan untuk menampilkan seluruh data stok barang.

#### Request Format

Tidak memerlukan request body.

#### Response

```json
[
  {
    "productId": 2,
    "sku": "BRG-002",
    "productName": "Aqua 600ml",
    "quantity": 100
  },
  {
    "productId": 3,
    "sku": "BRG-003",
    "productName": "Teh Botol Sosro",
    "quantity": 100
  },
  {
    "productId": 4,
    "sku": "BRG-004",
    "productName": "Kopi Kapal Api",
    "quantity": 100
  },
  {
    "productId": 5,
    "sku": "BRG-005",
    "productName": "Biskuit Roma",
    "quantity": 100
  },
  {
    "productId": 6,
    "sku": "BRG-006",
    "productName": "Susu Ultra 250ml",
    "quantity": 100
  },
  {
    "productId": 7,
    "sku": "BRG-007",
    "productName": "Chitato 68g",
    "quantity": 100
  },
  {
    "productId": 8,
    "sku": "BRG-008",
    "productName": "Roti Tawar",
    "quantity": 100
  },
  {
    "productId": 9,
    "sku": "BRG-009",
    "productName": "Sabun Lifebuoy",
    "quantity": 100
  },
  {
    "productId": 10,
    "sku": "BRG-010",
    "productName": "Shampoo Sunsilk",
    "quantity": 100
  }
]
```

### GET /api/stocks/{productId}

Endpoint ini digunakan untuk menampilkan stok berdasarkan product ID.

#### Request Format

```text
GET /api/stocks/2
```

#### Response

```json
{
  "productId": 2,
  "sku": "BRG-002",
  "productName": "Aqua 600ml",
  "quantity": 100
}
```

---

## Sales

### POST /api/sales

Endpoint ini digunakan untuk membuat transaksi penjualan dan mengurangi stok secara otomatis.

#### Request Format

```json
{
  "items": [
    {
      "productId": 3,
      "quantity": 2
    },
    {
      "productId": 4,
      "quantity": 1
    }
  ]
}
```

#### Response

```json
{
  "id": 3,
  "transactionNo": "AA202606230001",
  "saleDate": "2026-06-23T12:04:12.504663461",
  "totalAmount": 12000,
  "details": [
    {
      "productId": 3,
      "sku": "BRG-003",
      "productName": "Teh Botol Sosro",
      "quantity": 2,
      "purchasePrice": 3500,
      "sellingPrice": 5000,
      "subtotal": 10000,
      "profit": 3000
    },
    {
      "productId": 4,
      "sku": "BRG-004",
      "productName": "Kopi Kapal Api",
      "quantity": 1,
      "purchasePrice": 1200,
      "sellingPrice": 2000,
      "subtotal": 2000,
      "profit": 800
    }
  ]
}
```

### GET /api/sales

Endpoint ini digunakan untuk menampilkan seluruh transaksi penjualan.

#### Request Format

Tidak memerlukan request body.

#### Response

```json
[
  {
    "id": 3,
    "transactionNo": "AA202606230001",
    "saleDate": "2026-06-23T12:04:12.504663",
    "totalAmount": 12000,
    "details": [
      {
        "productId": 3,
        "sku": "BRG-003",
        "productName": "Teh Botol Sosro",
        "quantity": 2,
        "purchasePrice": 3500,
        "sellingPrice": 5000,
        "subtotal": 10000,
        "profit": 3000
      },
      {
        "productId": 4,
        "sku": "BRG-004",
        "productName": "Kopi Kapal Api",
        "quantity": 1,
        "purchasePrice": 1200,
        "sellingPrice": 2000,
        "subtotal": 2000,
        "profit": 800
      }
    ]
  }
]
```

### GET /api/sales/{id}

Endpoint ini digunakan untuk menampilkan detail transaksi penjualan berdasarkan ID.

#### Request Format

```text
GET /api/sales/3
```

#### Response

```json
{
  "id": 3,
  "transactionNo": "AA202606230001",
  "saleDate": "2026-06-23T12:04:12.504663",
  "totalAmount": 12000,
  "details": [
    {
      "productId": 3,
      "sku": "BRG-003",
      "productName": "Teh Botol Sosro",
      "quantity": 2,
      "purchasePrice": 3500,
      "sellingPrice": 5000,
      "subtotal": 10000,
      "profit": 3000
    },
    {
      "productId": 4,
      "sku": "BRG-004",
      "productName": "Kopi Kapal Api",
      "quantity": 1,
      "purchasePrice": 1200,
      "sellingPrice": 2000,
      "subtotal": 2000,
      "profit": 800
    }
  ]
}
```

---

## Reports

### GET /api/reports/top-selling-products

Endpoint ini digunakan untuk menampilkan 5 barang terlaris berdasarkan total quantity yang terjual.

#### Response

```json
[
  {
    "productId": 9,
    "sku": "BRG-009",
    "productName": "Sabun Lifebuoy",
    "totalQty": 10
  },
  {
    "productId": 5,
    "sku": "BRG-005",
    "productName": "Biskuit Roma",
    "totalQty": 7
  },
  {
    "productId": 6,
    "sku": "BRG-006",
    "productName": "Susu Ultra 250ml",
    "totalQty": 5
  },
  {
    "productId": 8,
    "sku": "BRG-008",
    "productName": "Roti Tawar",
    "totalQty": 4
  },
  {
    "productId": 7,
    "sku": "BRG-007",
    "productName": "Chitato 68g",
    "totalQty": 3
  }
]
```

### GET /api/reports/top-profitable-products

Endpoint ini digunakan untuk menampilkan 5 barang dengan keuntungan tertinggi.

#### Response

```json
[
  {
    "productId": 9,
    "sku": "BRG-009",
    "productName": "Sabun Lifebuoy",
    "qty": 10,
    "total": 60000,
    "modal": 35000,
    "keuntungan": 25000
  },
  {
    "productId": 5,
    "sku": "BRG-005",
    "productName": "Biskuit Roma",
    "qty": 7,
    "total": 59500,
    "modal": 42000,
    "keuntungan": 17500
  },
  {
    "productId": 8,
    "sku": "BRG-008",
    "productName": "Roti Tawar",
    "qty": 4,
    "total": 60000,
    "modal": 44000,
    "keuntungan": 16000
  },
  {
    "productId": 6,
    "sku": "BRG-006",
    "productName": "Susu Ultra 250ml",
    "qty": 5,
    "total": 37500,
    "modal": 25000,
    "keuntungan": 12500
  },
  {
    "productId": 7,
    "sku": "BRG-007",
    "productName": "Chitato 68g",
    "qty": 3,
    "total": 36000,
    "modal": 24000,
    "keuntungan": 12000
  }
]
```

### GET /api/reports/sales-containing-top-profitable-products

Endpoint ini digunakan untuk menampilkan transaksi yang mengandung 5 barang paling menguntungkan.

#### Response

```json
[
  {
    "idPenjualan": 6,
    "transactionNo": "AA202606230004",
    "tanggal": "2026-06-23T12:08:41.681316",
    "detailBarang": [
      {
        "productId": 9,
        "sku": "BRG-009",
        "productName": "Sabun Lifebuoy",
        "quantity": 10,
        "purchasePrice": 3500,
        "sellingPrice": 6000,
        "subtotal": 60000,
        "profit": 25000
      },
      {
        "productId": 10,
        "sku": "BRG-010",
        "productName": "Shampoo Sunsilk",
        "quantity": 2,
        "purchasePrice": 9000,
        "sellingPrice": 13500,
        "subtotal": 27000,
        "profit": 9000
      }
    ]
  },
  {
    "idPenjualan": 5,
    "transactionNo": "AA202606230003",
    "tanggal": "2026-06-23T12:08:17.278357",
    "detailBarang": [
      {
        "productId": 7,
        "sku": "BRG-007",
        "productName": "Chitato 68g",
        "quantity": 3,
        "purchasePrice": 8000,
        "sellingPrice": 12000,
        "subtotal": 36000,
        "profit": 12000
      },
      {
        "productId": 8,
        "sku": "BRG-008",
        "productName": "Roti Tawar",
        "quantity": 4,
        "purchasePrice": 11000,
        "sellingPrice": 15000,
        "subtotal": 60000,
        "profit": 16000
      }
    ]
  },
  {
    "idPenjualan": 4,
    "transactionNo": "AA202606230002",
    "tanggal": "2026-06-23T12:08:00.500986",
    "detailBarang": [
      {
        "productId": 5,
        "sku": "BRG-005",
        "productName": "Biskuit Roma",
        "quantity": 7,
        "purchasePrice": 6000,
        "sellingPrice": 8500,
        "subtotal": 59500,
        "profit": 17500
      },
      {
        "productId": 6,
        "sku": "BRG-006",
        "productName": "Susu Ultra 250ml",
        "quantity": 5,
        "purchasePrice": 5000,
        "sellingPrice": 7500,
        "subtotal": 37500,
        "profit": 12500
      }
    ]
  }
]
```

## Prerequisites

- Java 17
- Maven 3.8+
- PostgreSQL 14+ atau versi compatible
- Optional: Keycloak atau JWT issuer lain untuk strict JWT validation mode
- Optional: Kafka jika ingin menguji event publishing dan Kafka UI
- Docker dan Docker Compose untuk containerized setup

### Testing Recommendation

Disarankan menggunakan:

```
✅Apache JMeter
✅K6
✅lotcost
✅Gatling
```

```
Target Test Scenario
TPS 150 (normal load)
TPS 240 (peak load)
```

## Changelog

### v2.0.0
- Added JWT Authentication Filter
- Fixed protected endpoint authorization using Bearer Token
- Fixed login authentication flow
- Added detailed service logging for easier tracking
- Added product service tracking logs
- Added stock service tracking logs
- Added sales transaction tracking logs
- Added not found response handling
- Added custom `ResourceNotFoundException`
- Added global exception handler
- Improved product not found response
- Improved stock not found response
- Fixed DELETE product response message
- Fixed Flyway migration issue during local development
- Improved API response documentation
- Improved README documentation
- Updated API usage examples
- Updated architecture and sequence diagram documentation

### v1.0.0

Initial release for Backend Developer Technical Test.

Features included:

- JWT Authentication
- Login API
- CRUD Master Barang
- Stock Management
- Sales Transaction Management
- Automatic Transaction Number Generation
- Top 5 Best Selling Products Report
- Top 5 Most Profitable Products Report
- Sales Report Containing Top 5 Most Profitable Products
- PostgreSQL Integration
- Flyway Database Migration
- Swagger/OpenAPI Documentation
- Spring Security Integration

---

## License

This project was developed as part of a **Backend Developer Technical Test Submission**.

The source code is intended solely for technical assessment and evaluation purposes.

Copyright © 2026 Dino Darmayanto

All rights reserved.

Project Submission For:

**3PM Solution - Backend Developer Technical Test**
