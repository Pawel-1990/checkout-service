# 🛒 Checkout Component API

## Overview

This service implements a **market checkout component** — a backend system for scanning items, calculating totals, applying multi-item and bundle discounts, and generating receipts.  

The system is designed using **Spring Boot** and exposes a **RESTful API** documented via **OpenAPI 3 (Swagger UI)**.

---

## 🧩 Business Context

- Each item has a **standard unit price**.
- Some items offer **multi-pricing** (e.g., “Buy 3 for 30¢”).
- Certain items have **bundle discounts**, where buying specific items together applies a price reduction.
- The **checkout session** maintains a list of scanned items, calculates the total dynamically, and generates a **receipt** after payment.

---

## 🧠 Technical Summary

| Component | Description |
|------------|-------------|
| `ItemController` | Manages store items (CRUD operations) |
| `BundleDiscountController` | Manages rules for discounts between items |
| `CheckoutController` | Handles the checkout process — adding/removing items, paying, and generating receipts |

Access to the built-in H2 database is available at:
http://localhost:8080/h2-console

Before logging in, fill in the fields as follows:

JDBC URL: jdbc:h2:mem  
Username: user  
Password: password

---

## 🚀 Running the Application

### 1️⃣ Build
```bash
./mvnw clean package
```

2️⃣ Run
```bash
java -jar target/checkout-app.jar
The service starts on port 8080 by default.
```

## 📘 API Documentation (Swagger UI)

Once the application is running, open:

👉 http://localhost:8080/swagger-ui/index.html

The OpenAPI 3 documentation is automatically generated from annotations such as:

@Operation, @ApiResponse, @Parameter, @Tag

These annotations describe every endpoint, expected request/response structure, and possible status codes.

## ⚙️ Example Objects
🧾 ItemRequest
{
"name": "A",
"normalPrice": 40.0,
"requiredQuantity": 3,
"specialPrice": 30.0
}

🧮 BundleDiscountRequest
{
"firstItemId": 1,
"secondItemId": 2,
"discountAmount": 5.0
}

💰 CheckoutItem
{
"itemName": "Apple,
"quantity": 2
}

## 🔐 Security & Authorization

This service uses Spring Security for authentication and authorization.

Two users are predefined:

| Username   | Password   | Role     | Access Rights                                                                                                                                                                                                                  |
| ---------- | ---------- | -------- |--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `admin`    | `admin`    | ADMIN    | Full access to all API endpoints                                                                                                                                                                                               |
| `customer` | `customer` | CUSTOMER | Access to all **GET** endpoints, two **POST** endpoints:<br>• `/api/checkouts/create`<br>• `/api/checkouts/{id}/pay` and two **PATCH** endpoints:<br>• `/api/checkouts/{id}/add-items`<br>• `/api/checkouts/{id}/delete-items` |


Authentication is handled via HTTP Basic Auth, and access control is enforced based on user roles.

## 🧑‍💻 Developer Notes

Built with Spring Boot 3, Spring Validation, and Springdoc OpenAPI 3.

Fully testable via Postman or Swagger UI.

Uses logging for all incoming API requests.

Follows REST and HTTP semantics (proper use of 201, 204, 400, 404 codes).




## EXAMPLE SERVICE FLOW:

POST /api/checkouts – create a new checkout

GET /api/items/names – return the list of available store items

GET /api/bundle-discounts – return the list of available store promotions

PATCH /api/checkouts/1/add-items – add specific products to the cart; discounts are applied based on quantities.

Example request body:
```json
[
{
"itemName": "Banana",
"quantity": 6
},
{
"itemName": "Apple",
"quantity": 10
}
]
```
GET /api/checkouts/1 – return checkout details including applied discounts

PATCH /api/checkouts/1/add-items – add the item “Pear” to get a bundle discount.

Example request body:
```json

[
{
"itemName": "Pear",
"quantity": 1
}
]
```

POST /api/checkouts/1/pay – pay for the order and receive a receipt with all purchase details

GET /api/checkouts/1/receipt – view the receipt


### Author

Paweł Dyjak
