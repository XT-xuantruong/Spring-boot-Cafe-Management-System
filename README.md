# **Project Name**

**Cafe Management System**

## **Brief Description**

The Cafe Management System is a web application designed to streamline cafe operations, catering to three primary roles: **Admin** (system management), **Staff** (cafe employees), and **Customer** (cafe patrons). It facilitates user management, role-based authorization, table reservations, order processing, payment handling, and menu management, offering a secure and user-friendly experience.

- **Frontend**: Built with React and TypeScript for type-safe development, using Vite for fast builds, TailwindCSS v3 and Shadcn UI for modern and responsive UI, Redux and RTK Query for state management and API calls, Axios for HTTP requests, and React Router DOM for navigation.

- **Backend**: Powered by Spring Boot with REST APIs, integrated with Spring Security and JWT for authentication/authorization, Cloudinary for image management, and Swagger for API documentation.

- **Database**: MySQL with an optimized schema supporting Admin, Staff, and Customer roles.

---

## **Features**

The features are categorized based on the roles of **Admin**, **Staff**, and **Customer**:

1\. **User Management**:

   - **Admin**: Create, update, delete accounts for Staff and Customers; assign roles (Admin, Staff, Customer).

   - **Staff**: View and update personal profile (e.g., change password).

   - **Customer**: Register, log in, and update personal information (email, phone).

2\. **Role-Based Authorization**:

   - **Admin**: Full system control (e.g., manage menu, tables, reports).

   - **Staff**: Manage daily operations (e.g., handle orders, reservations, payments).

   - **Customer**: Access services (e.g., reserve tables, place orders, view personal history).

3\. **Table Management**:

   - **Admin**: Add, edit, delete tables; view status of all tables.

   - **Staff**: View table statuses (Available, Reserved, Occupied), confirm/cancel reservations.

   - **Customer**: Reserve tables in advance via the customer interface.

4\. **Order Management**:

   - **Admin**: View all orders, cancel orders if needed.

   - **Staff**: Create, update, and complete orders for customers.

   - **Customer**: Place orders, view their order details.

5\. **Payment Management**:

   - **Admin**: View payment history across the system.

   - **Staff**: Process payments (cash, card, mobile), update payment status.

   - **Customer**: Pay for their orders, view personal payment history.

6\. **Menu Management**:

   - **Admin**: Add, edit, delete menu items; upload item images via Cloudinary.

   - **Staff**: View menu to assist customers with orders.

   - **Customer**: Browse menu and select items to order.

7\. **Basic Reporting**:

   - **Admin**: View revenue reports, order/payment statistics by day/month.

   - **Staff**: View order reports for their shift (optional extension).

   - **Customer**: No access to reports.

---

## **Table Definitions (Database Schema)**

The database uses **MySQL** with tables optimized for the **Admin**, **Staff**, and **Customer** roles. Below is the schema in tabular format:

| **Table Name** | **Column Name**        | **Data Type** | **Constraints**                     | **Description**                              |

|----------------|------------------------|---------------|-------------------------------------|----------------------------------------------|

| **users**      | user_id               | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for users                  |

|                | username              | VARCHAR(50)   | NOT NULL, UNIQUE                    | Username for login                           |

|                | password              | VARCHAR(255)  | NOT NULL                            | Encrypted password (BCrypt)                  |

|                | full_name             | VARCHAR(100)  | NOT NULL                            | Full name of the user                        |

|                | email                 | VARCHAR(100)  | UNIQUE                              | User's email address                         |

|                | phone                 | VARCHAR(20)   |                                     | User's phone number                          |

|                | created_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP           | Record creation timestamp                    |

|                | updated_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Record update timestamp                      |

| **roles**      | role_id               | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for roles                  |

|                | role_name             | VARCHAR(50)   | NOT NULL, UNIQUE                    | Role name (ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER) |

|                | description           | TEXT          |                                     | Role description                             |

| **user_roles** | user_id               | BIGINT        | PRIMARY KEY, FOREIGN KEY (users)    | References user_id                           |

|                | role_id               | BIGINT        | PRIMARY KEY, FOREIGN KEY (roles)    | References role_id                           |

| **tables**     | table_id              | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for tables                 |

|                | table_number          | VARCHAR(10)   | NOT NULL, UNIQUE                    | Table identifier (e.g., T01, T02)            |

|                | capacity              | INT           | NOT NULL                            | Number of seats                              |

|                | status                | ENUM          | DEFAULT 'AVAILABLE'                 | Table status (AVAILABLE, RESERVED, OCCUPIED) |

|                | created_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP           | Record creation timestamp                    |

|                | updated_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Record update timestamp                      |

| **reservations** | reservation_id      | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for reservations           |

|                | user_id               | BIGINT        | FOREIGN KEY (users), NULLABLE       | Customer who made the reservation            |

|                | table_id              | BIGINT        | FOREIGN KEY (tables)                | Table being reserved                         |

|                | reservation_time      | DATETIME      | NOT NULL                            | Reservation date and time                    |

|                | status                | ENUM          | DEFAULT 'PENDING'                   | Status (PENDING, CONFIRMED, CANCELLED)       |

|                | created_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP           | Record creation timestamp                    |

|                | updated_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Record update timestamp                      |

| **menu_items** | item_id               | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for menu items             |

|                | item_name             | VARCHAR(100)  | NOT NULL                            | Name of the menu item                        |

|                | description           | TEXT          |                                     | Item description                             |

|                | price                 | DECIMAL(10,2) | NOT NULL                            | Item price                                   |

|                | category              | VARCHAR(50)   |                                     | Category (e.g., Coffee, Juice, Food)         |

|                | image_url             | VARCHAR(255)  |                                     | URL of item image (Cloudinary)               |

|                | is_available          | BOOLEAN       | DEFAULT TRUE                        | Availability status                          |

|                | created_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP           | Record creation timestamp                    |

|                | updated_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Record update timestamp                      |

| **orders**     | order_id              | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for orders                 |

|                | user_id               | BIGINT        | FOREIGN KEY (users), NULLABLE       | Staff or Customer who created the order      |

|                | table_id              | BIGINT        | FOREIGN KEY (tables), NULLABLE      | Table associated with the order              |

|                | order_status          | ENUM          | DEFAULT 'PENDING'                   | Status (PENDING, PREPARING, SERVED, COMPLETED, CANCELLED) |

|                | payment_status        | ENUM          | DEFAULT 'UNPAID'                    | Payment status (UNPAID, PARTIALLY_PAID, PAID, CANCELLED) |

|                | total_amount          | DECIMAL(10,2) | DEFAULT 0.00                        | Total order amount                           |

|                | created_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP           | Record creation timestamp                    |

|                | updated_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Record update timestamp                      |

| **order_items**| order_item_id         | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for order items            |

|                | order_id              | BIGINT        | FOREIGN KEY (orders)                | References order_id                          |

|                | item_id               | BIGINT        | FOREIGN KEY (menu_items)            | References item_id                           |

|                | quantity              | INT           | NOT NULL                            | Quantity of the item                         |

|                | unit_price            | DECIMAL(10,2) | NOT NULL                            | Price per item                               |

|                | subtotal              | DECIMAL(10,2) | GENERATED (quantity * unit_price)   | Calculated subtotal                          |

| **payments**   | payment_id            | BIGINT        | PRIMARY KEY, AUTO_INCREMENT         | Unique identifier for payments               |

|                | order_id              | BIGINT        | FOREIGN KEY (orders)                | References order_id                          |

|                | amount                | DECIMAL(10,2) | NOT NULL                            | Payment amount                               |

|                | payment_method        | ENUM          | NOT NULL                            | Method (CASH, CARD, MOBILE, BANK_TRANSFER)   |

|                | payment_status        | ENUM          | DEFAULT 'PENDING'                   | Status (PENDING, COMPLETED, FAILED, REFUNDED) |

|                | payment_time          | DATETIME      |                                     | Timestamp of payment                         |

|                | transaction_id        | VARCHAR(100)  | UNIQUE                              | Transaction ID from payment gateway          |

|                | created_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP           | Record creation timestamp                    |

|                | updated_at            | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Record update timestamp                      |

**Relationships**:

- **users** ↔ **user_roles** ↔ **roles**: Many-to-many for role assignment (Admin, Staff, Customer).

- **users** → **reservations**: One Customer can have multiple reservations (1:n).

- **tables** → **reservations**: One table can have multiple reservations (1:n).

- **users** → **orders**: One Staff or Customer can create multiple orders (1:n).

- **tables** → **orders**: One table can be associated with multiple orders (1:n).

- **menu_items** → **order_items**: One menu item can appear in multiple orders (1:n).

- **orders** → **order_items**: One order contains multiple items (1:n).

- **orders** → **payments**: One order can have multiple payment transactions (1:n).

**Notes**:

- Roles are stored in the `roles` table with values: `ROLE_ADMIN`, `ROLE_STAFF`, `ROLE_CUSTOMER`.

- The `user_roles` table links users to their roles, supporting multiple roles per user if needed.

---

## **API List**

The **REST APIs** are designed to support **Admin**, **Staff**, and **Customer** roles, with clear role-based authorization using **JWT**. All APIs return JSON and are grouped by functionality.

### **1. Authentication APIs**

| Method | Endpoint            | Role         | Description                              | Request Body                              | Response                         |

|--------|---------------------|--------------|------------------------------------------|-------------------------------------------|----------------------------------|

| POST   | `/api/auth/login`   | None         | Log in, returns JWT token (Admin, Staff, Customer) | `{ "username": string, "password": string }` | `{ "token": string, "username": string, "roles": string[] }` |

| POST   | `/api/auth/register`| None         | Register a new Customer                 | `{ "username": string, "password": string, "fullName": string, "email": string, "phone": string }` | `{ "userId": number, ... }` |

### **2. User Management APIs**

| Method | Endpoint               | Role                  | Description                              | Request Body                              | Response                         |

|--------|------------------------|-----------------------|------------------------------------------|-------------------------------------------|----------------------------------|

| GET    | `/api/users`           | ROLE_ADMIN            | Get list of all users                   | -                                         | `[{ "userId": number, "username": string, "roles": string[], ... }]` |

| GET    | `/api/users/{id}`      | ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER | Get user details by ID (Customers only view own info) | -                                         | `{ "userId": number, ... }`     |

| POST   | `/api/users`           | ROLE_ADMIN            | Create new user (Admin, Staff)          | `{ "username": string, "password": string, "fullName": string, "email": string, "phone": string, "roles": string[] }` | `{ "userId": number, ... }`     |

| PUT    | `/api/users/{id}`      | ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER | Update user info (Customers only update own info) | `{ "fullName": string, "email": string, "phone": string }` | `{ "userId": number, ... }`     |

| DELETE | `/api/users/{id}`      | ROLE_ADMIN            | Delete a user                           | -                                         | `{ "message": "User deleted" }` |

### **3. Table Management APIs**

| Method | Endpoint               | Role                  | Description                              | Request Body                              | Response                         |

|--------|------------------------|-----------------------|------------------------------------------|-------------------------------------------|----------------------------------|

| GET    | `/api/tables`          | ROLE_ADMIN, ROLE_STAFF | Get list of tables and statuses        | -                                         | `[{ "tableId": number, "tableNumber": string, "status": string, ... }]` |

| GET    | `/api/tables/{id}`     | ROLE_ADMIN, ROLE_STAFF | Get table details by ID                | -                                         | `{ "tableId": number, ... }`     |

| POST   | `/api/tables`          | ROLE_ADMIN            | Create a new table                      | `{ "tableNumber": string, "capacity": number }` | `{ "tableId": number, ... }`     |

| PUT    | `/api/tables/{id}`     | ROLE_ADMIN            | Update table details                    | `{ "tableNumber": string, "capacity": number, "status": string }` | `{ "tableId": number, ... }`     |

| DELETE | `/api/tables/{id}`     | ROLE_ADMIN            | Delete a table                          | -                                         | `{ "message": "Table deleted" }` |

### **4. Reservation Management APIs**

| Method | Endpoint                   | Role                  | Description                              | Request Body                              | Response                         |

|--------|----------------------------|-----------------------|------------------------------------------|-------------------------------------------|----------------------------------|

| GET    | `/api/reservations`        | ROLE_ADMIN, ROLE_STAFF | Get list of reservations                | -                                         | `[{ "reservationId": number, ... }]` |

| GET    | `/api/reservations/{id}`   | ROLE_ADMIN, ROLE_STAFF | Get reservation details by ID           | -                                         | `{ "reservationId": number, ... }` |

| GET    | `/api/reservations/customer` | ROLE_CUSTOMER       | Get list of Customer's reservations     | -                                         | `[{ "reservationId": number, ... }]` |

| POST   | `/api/reservations`        | ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER | Create a new reservation        | `{ "userId": number, "tableId": number, "reservationTime": string }` | `{ "reservationId": number, ... }` |

| PUT    | `/api/reservations/{id}`   | ROLE_ADMIN, ROLE_STAFF | Update reservation status (e.g., CONFIRMED) | `{ "status": string }`                    | `{ "reservationId": number, ... }` |

| DELETE | `/api/reservations/{id}`   | ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER | Cancel reservation (Customers only cancel own) | -                                         | `{ "message": "Reservation cancelled" }` |

### **5. Menu Management APIs**

| Method | Endpoint                   | Role                  | Description                              | Request Body                              | Response                         |

|--------|----------------------------|-----------------------|------------------------------------------|-------------------------------------------|----------------------------------|

| GET    | `/api/menu-items`          | All                   | Get list of menu items                  | -                                         | `[{ "itemId": number, "itemName": string, "imageUrl": string, ... }]` |

| GET    | `/api/menu-items/{id}`     | All                   | Get menu item details by ID             | -                                         | `{ "itemId": number, ... }`     |

| POST   | `/api/menu-items`          | ROLE_ADMIN            | Create a new menu item (with Cloudinary image) | `multipart/form-data: { "itemName": string, "price": number, "category": string, "image": file }` | `{ "itemId": number, ... }`     |

| PUT    | `/api/menu-items/{id}`     | ROLE_ADMIN            | Update a menu item                      | `multipart/form-data: { "itemName": string, "price": number, "category": string, "image": file }` | `{ "itemId": number, ... }`     |

| DELETE | `/api/menu-items/{id}`     | ROLE_ADMIN            | Delete a menu item                      | -                                         | `{ "message": "Item deleted" }` |

### **6. Order Management APIs**

| Method | Endpoint                   | Role                  | Description                              | Request Body                              | Response                         |

|--------|----------------------------|-----------------------|------------------------------------------|-------------------------------------------|----------------------------------|

| GET    | `/api/orders`              | ROLE_ADMIN, ROLE_STAFF | Get list of all orders                 | -                                         | `[{ "orderId": number, "tableId": number, ... }]` |

| GET    | `/api/orders/{id}`         | ROLE_ADMIN, ROLE_STAFF | Get order details by ID                | -                                         | `{ "orderId": number, "items": [{...}], ... }` |

| GET    | `/api/orders/customer`     | ROLE_CUSTOMER         | Get list of Customer's orders           | -                                         | `[{ "orderId": number, ... }]` |

| POST   | `/api/orders`              | ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER | Create a new order             | `{ "userId": number, "tableId": number, "items": [{ "itemId": number, "quantity": number }], ... }` | `{ "orderId": number, ... }`     |

| PUT    | `/api/orders/{id}`         | ROLE_ADMIN, ROLE_STAFF | Update order status                    | `{ "orderStatus": string }`               | `{ "orderId": number, ... }`     |

| DELETE | `/api/orders/{id}`         | ROLE_ADMIN, ROLE_STAFF | Cancel an order                        | -                                         | `{ "message": "Order cancelled" }` |

### **7. Payment Management APIs**

| Method | Endpoint                   | Role                  | Description                              | Request Body                              | Response                         |

|--------|----------------------------|-----------------------|------------------------------------------|-------------------------------------------|----------------------------------|

| GET    | `/api/payments`            | ROLE_ADMIN, ROLE_STAFF | Get list of all payments               | -                                         | `[{ "paymentId": number, ... }]` |

| GET    | `/api/payments/order/{orderId}` | ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER | Get payments for an order (Customers only view own) | -                                         | `[{ "paymentId": number, ... }]` |

| GET    | `/api/payments/customer`   | ROLE_CUSTOMER         | Get list of Customer's payments         | -                                         | `[{ "paymentId": number, ... }]` |

| POST   | `/api/payments`            | ROLE_ADMIN, ROLE_STAFF | Process a new payment                  | `{ "orderId": number, "amount": number, "paymentMethod": string, "transactionId": string }` | `{ "paymentId": number, ... }`     |

| PUT    | `/api/payments/{id}`       | ROLE_ADMIN, ROLE_STAFF | Update payment status                  | `{ "paymentStatus": string }`             | `{ "paymentId": number, ... }`     |

---

## **Additional Information**

### **Technologies Used**

- **Frontend**:

  - **React, TypeScript**: Type-safe UI development.

  - **Vite**: Fast build tool with hot module replacement.

  - **TailwindCSS v3, Shadcn UI**: Modern, responsive design.

  - **Redux, RTK Query**: Efficient state management and API handling.

  - **Axios**: HTTP requests to backend.

  - **React Router DOM**: Client-side navigation.

- **Backend**:

  - **Spring Boot**: Core framework for REST APIs.

  - **Spring Security, JWT**: Role-based authentication/authorization (ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER).

  - **Cloudinary**: Image upload for menu items.

  - **Swagger**: API documentation.

  - **JPA Repository**: Interaction with MySQL database.

- **Database**: MySQL with the schema defined above.

### **Role-Based Authorization Details**

- **ROLE_ADMIN**:

  - Full system access: manage users, tables, menu items; view system-wide reports.

  - Examples: Create Staff accounts, add new menu items.

- **ROLE_STAFF**:

  - Daily operations: process orders, handle reservations, manage payments.

  - Examples: Confirm table reservations, update order status.

- **ROLE_CUSTOMER**:

  - Service interaction: reserve tables, place orders, make payments, view personal history.

  - Examples: Book a table, order from the menu, pay via card.

### **Implementation Notes**

- **Security**:

  - Passwords encrypted with BCrypt.

  - JWT used for authentication, stored securely (e.g., HttpOnly cookies or secure localStorage).

  - Role-based access enforced with `@PreAuthorize` in backend and role checks in frontend.

- **Performance**:

  - Optimize JPA queries with `fetch = FetchType.LAZY` for relationships.

  - Use RTK Query caching to minimize API requests.

- **UI/UX**:

  - Leverage TailwindCSS and Shadcn UI for responsive, role-specific interfaces (e.g., Admin dashboard, Customer ordering page).

  - Differentiate UI based on roles (e.g., Admin sees management tools, Customer sees menu).

- **Documentation**:

  - APIs documented with Swagger, accessible at `/swagger-ui.html`.

  - Provide user guides for Admin, Staff, and Customer roles.

---
