# Sequence Diagram

## Authentication & JWT Flow

```mermaid
sequenceDiagram
    actor Client

    participant AuthController as AuthController
    participant AuthService as AuthServiceImpl
    participant UserRepo as UsersRepository
    participant PasswordEncoder as PasswordEncoder
    participant JwtUtil as JwtUtil
    participant DB as PostgreSQL

    Client->>AuthController: POST /api/auth/login
    AuthController->>AuthService: login(LoginRequest)

    AuthService->>AuthService: Log attempt login
    AuthService->>UserRepo: findByUsername(username)
    UserRepo->>DB: SELECT users WHERE username = ?
    DB-->>UserRepo: user data
    UserRepo-->>AuthService: Users

    AuthService->>PasswordEncoder: matches(rawPassword, hashedPassword)

    alt Invalid username or password
        AuthService-->>AuthController: BadCredentialsException
        AuthController-->>Client: 401 Unauthorized / 403 Forbidden
    else Login success
        AuthService->>JwtUtil: generateToken(username, role)
        JwtUtil-->>AuthService: JWT Token
        AuthService-->>AuthController: LoginResponse
        AuthController-->>Client: 200 OK + JWT Token
    end
```

---

## Protected Endpoint Flow

```mermaid
sequenceDiagram
    actor Client

    participant JwtFilter as JwtAuthenticationFilter
    participant JwtUtil as JwtUtil
    participant SecurityContext as SecurityContextHolder
    participant Controller as Protected Controller

    Client->>JwtFilter: Request Protected Endpoint<br/>Authorization: Bearer Token

    JwtFilter->>JwtFilter: Read Authorization Header
    JwtFilter->>JwtUtil: validateToken(token)

    alt Token invalid or missing
        JwtFilter-->>Client: 403 Forbidden
    else Token valid
        JwtFilter->>JwtUtil: extractUsername(token)
        JwtFilter->>JwtUtil: extractRole(token)
        JwtFilter->>SecurityContext: setAuthentication(username, role)
        JwtFilter->>Controller: Continue request
    end
```

---

## Product Management Flow

```mermaid
sequenceDiagram
    actor Client

    participant JwtFilter as JwtAuthenticationFilter
    participant Controller as ProductController
    participant Service as ProductServiceImpl
    participant ProductRepo as ProductsRepository
    participant StockRepo as StocksRepository
    participant ExceptionHandler as GlobalExceptionHandler
    participant DB as PostgreSQL

    Client->>JwtFilter: Request /api/products<br/>Bearer JWT
    JwtFilter->>Controller: Authorized request

    alt GET /api/products
        Controller->>Service: findAll()
        Service->>ProductRepo: findAll()
        ProductRepo->>DB: SELECT products
        DB-->>ProductRepo: product list
        ProductRepo-->>Service: List Products
        Service-->>Controller: List ProductResponse
        Controller-->>Client: 200 OK

    else GET /api/products/{id}
        Controller->>Service: findById(id)
        Service->>ProductRepo: findById(id)
        ProductRepo->>DB: SELECT product WHERE id = ?
        DB-->>ProductRepo: product / empty

        alt Product not found
            Service-->>ExceptionHandler: ResourceNotFoundException
            ExceptionHandler-->>Client: 404 Not Found
        else Product found
            ProductRepo-->>Service: Product
            Service-->>Controller: ProductResponse
            Controller-->>Client: 200 OK
        end

    else POST /api/products
        Controller->>Service: create(ProductRequest)
        Service->>ProductRepo: existsBySku(sku)
        ProductRepo->>DB: SELECT product WHERE sku = ?
        DB-->>ProductRepo: true / false

        alt SKU already exists
            Service-->>ExceptionHandler: RuntimeException
            ExceptionHandler-->>Client: 400 Bad Request
        else New product
            Service->>ProductRepo: save(product)
            ProductRepo->>DB: INSERT products
            DB-->>ProductRepo: saved product

            Service->>StockRepo: save(initial stock)
            StockRepo->>DB: INSERT stocks quantity = 0
            DB-->>StockRepo: saved stock

            Service-->>Controller: ProductResponse
            Controller-->>Client: 200 OK
        end

    else PUT /api/products/{id}
        Controller->>Service: update(id, ProductRequest)
        Service->>ProductRepo: findById(id)
        ProductRepo->>DB: SELECT product WHERE id = ?
        DB-->>ProductRepo: product / empty

        alt Product not found
            Service-->>ExceptionHandler: ResourceNotFoundException
            ExceptionHandler-->>Client: 404 Not Found
        else Product found
            Service->>ProductRepo: findBySku(sku)
            ProductRepo->>DB: SELECT product WHERE sku = ?
            DB-->>ProductRepo: product / empty

            alt Duplicate SKU from another product
                Service-->>ExceptionHandler: RuntimeException
                ExceptionHandler-->>Client: 400 Bad Request
            else Valid update
                Service->>ProductRepo: save(updated product)
                ProductRepo->>DB: UPDATE products
                DB-->>ProductRepo: updated product
                Service-->>Controller: ProductResponse
                Controller-->>Client: 200 OK
            end
        end

    else DELETE /api/products/{id}
        Controller->>Service: delete(id)
        Service->>ProductRepo: findById(id)
        ProductRepo->>DB: SELECT product WHERE id = ?
        DB-->>ProductRepo: product / empty

        alt Product not found
            Service-->>ExceptionHandler: ResourceNotFoundException
            ExceptionHandler-->>Client: 404 Not Found
        else Product found
            Service->>ProductRepo: delete(product)
            ProductRepo->>DB: DELETE products
            DB-->>ProductRepo: success
            Controller-->>Client: 200 OK<br/>Product deleted successfully
        end
    end
```

---

## Stock Flow

```mermaid
sequenceDiagram
    actor Client

    participant JwtFilter as JwtAuthenticationFilter
    participant Controller as StockController
    participant Service as StockServiceImpl
    participant StockRepo as StocksRepository
    participant ExceptionHandler as GlobalExceptionHandler
    participant DB as PostgreSQL

    Client->>JwtFilter: Request /api/stocks<br/>Bearer JWT
    JwtFilter->>Controller: Authorized request

    alt GET /api/stocks
        Controller->>Service: getAllStocks()
        Service->>StockRepo: findAll()
        StockRepo->>DB: SELECT stocks JOIN products
        DB-->>StockRepo: stock list
        StockRepo-->>Service: List Stocks
        Service-->>Controller: List StockResponse
        Controller-->>Client: 200 OK

    else GET /api/stocks/{productId}
        Controller->>Service: getStock(productId)
        Service->>StockRepo: findByProduct_Id(productId)
        StockRepo->>DB: SELECT stock WHERE product_id = ?
        DB-->>StockRepo: stock / empty

        alt Stock not found
            Service-->>ExceptionHandler: ResourceNotFoundException
            ExceptionHandler-->>Client: 404 Not Found
        else Stock found
            StockRepo-->>Service: Stocks
            Service-->>Controller: StockResponse
            Controller-->>Client: 200 OK
        end
    end
```

---

## Sales Transaction Flow

```mermaid
sequenceDiagram
    actor Client

    participant JwtFilter as JwtAuthenticationFilter
    participant Controller as SaleController
    participant Service as SaleServiceImpl
    participant ProductRepo as ProductsRepository
    participant StockRepo as StocksRepository
    participant SalesRepo as SalesRepository
    participant DetailRepo as SaleDetailsRepository
    participant ExceptionHandler as GlobalExceptionHandler
    participant DB as PostgreSQL

    Client->>JwtFilter: POST /api/sales<br/>Bearer JWT
    JwtFilter->>Controller: Authorized Request
    Controller->>Service: createSale(SaleRequest)

    Service->>SalesRepo: countBySaleDateToday(today)
    SalesRepo->>DB: COUNT sales by sale_date
    DB-->>SalesRepo: countToday
    SalesRepo-->>Service: countToday

    Service->>Service: generateTransactionNo()
    Service->>SalesRepo: save(sale header)
    SalesRepo->>DB: INSERT sales
    DB-->>SalesRepo: saved sales
    SalesRepo-->>Service: Sales

    loop For Each Item
        Service->>ProductRepo: findById(productId)
        ProductRepo->>DB: SELECT product WHERE id = ?
        DB-->>ProductRepo: product / empty

        alt Product not found
            Service-->>ExceptionHandler: ResourceNotFoundException
            ExceptionHandler-->>Client: 404 Not Found
        else Product found
            ProductRepo-->>Service: Product
        end

        Service->>StockRepo: findByProduct_Id(productId)
        StockRepo->>DB: SELECT stock WHERE product_id = ?
        DB-->>StockRepo: stock / empty

        alt Stock not found
            Service-->>ExceptionHandler: ResourceNotFoundException
            ExceptionHandler-->>Client: 404 Not Found
        else Stock found
            StockRepo-->>Service: Stock
        end

        alt Stock not enough
            Service-->>ExceptionHandler: RuntimeException
            ExceptionHandler-->>Client: 400 Bad Request
        else Stock available
            Service->>StockRepo: save(updated stock)
            StockRepo->>DB: UPDATE stocks quantity
            DB-->>StockRepo: success

            Service->>Service: calculate subtotal, modal, profit
            Service->>DetailRepo: save(sale detail)
            DetailRepo->>DB: INSERT sale_details
            DB-->>DetailRepo: success
        end
    end

    Service->>SalesRepo: save(totalAmount)
    SalesRepo->>DB: UPDATE sales total_amount
    DB-->>SalesRepo: success

    Service->>DetailRepo: findBySaleId(saleId)
    DetailRepo->>DB: SELECT sale_details WHERE sale_id = ?
    DB-->>DetailRepo: detail list

    Service-->>Controller: SaleResponse
    Controller-->>Client: 200 OK
```

---

## Report Generation Flow

```mermaid
sequenceDiagram
    actor Client

    participant JwtFilter as JwtAuthenticationFilter
    participant Controller as ReportController
    participant Service as ReportServiceImpl
    participant SalesRepo as SalesRepository
    participant DetailRepo as SaleDetailsRepository
    participant DB as PostgreSQL

    Client->>JwtFilter: GET /api/reports/*<br/>Bearer JWT
    JwtFilter->>Controller: Authorized Request

    alt GET /api/reports/top-selling-products
        Controller->>Service: getTop5SellingProducts()
        Service->>DetailRepo: findTopSellingProducts()
        DetailRepo->>DB: SUM(quantity) GROUP BY product
        DB-->>DetailRepo: top 5 result
        DetailRepo-->>Service: List Object[]
        Service-->>Controller: List TopSellingProductResponse
        Controller-->>Client: 200 OK

    else GET /api/reports/top-profitable-products
        Controller->>Service: getTop5ProfitableProducts()
        Service->>DetailRepo: findTopProfitableProducts()
        DetailRepo->>DB: SUM(profit) GROUP BY product
        DB-->>DetailRepo: top 5 result
        DetailRepo-->>Service: List Object[]
        Service-->>Controller: List TopProfitProductResponse
        Controller-->>Client: 200 OK

    else GET /api/reports/sales-containing-top-profitable-products
        Controller->>Service: getSalesContainingTop5ProfitableProducts()
        Service->>DetailRepo: findTopProfitableProductIds()
        DetailRepo->>DB: SELECT top 5 product ids by profit
        DB-->>DetailRepo: product ids

        Service->>SalesRepo: findSalesContainingProducts(productIds)
        SalesRepo->>DB: SELECT DISTINCT sales containing product ids
        DB-->>SalesRepo: sales list

        Service->>DetailRepo: findBySaleId(saleId)
        DetailRepo->>DB: SELECT sale details
        DB-->>DetailRepo: detail list

        Service-->>Controller: List ProfitSaleResponse
        Controller-->>Client: 200 OK
    end
```
