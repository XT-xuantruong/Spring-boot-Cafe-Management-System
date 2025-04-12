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

### 1. `users`
Stores information about registered users (admins, staff, customers).

| Column       | Type           | Constraints                                      | Description                        |
|--------------|----------------|--------------------------------------------------|------------------------------------|
| user_id      | BIGINT         | PRIMARY KEY, AUTO_INCREMENT                      | Unique identifier for users        |
| username     | VARCHAR(50)    | NOT NULL, UNIQUE                                 | Username for login                 |
| password     | VARCHAR(255)   | NOT NULL                                         | Encrypted password (BCrypt)        |
| full_name    | VARCHAR(100)   | NOT NULL                                         | Full name                          |
| email        | VARCHAR(100)   | UNIQUE                                           | Email address                      |
| phone        | VARCHAR(20)    |                                                  | Phone number                       |
| created_at   | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP                        | Record creation time               |
| updated_at   | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP ON UPDATE              | Last update time                   |

---

### 2. `roles`
Defines roles assigned to users (admin, staff, customer).

| Column       | Type         | Constraints                  | Description                            |
|--------------|--------------|------------------------------|----------------------------------------|
| role_id      | BIGINT       | PRIMARY KEY, AUTO_INCREMENT  | Unique role ID                         |
| role_name    | VARCHAR(50)  | NOT NULL, UNIQUE             | Role name (e.g., ROLE_ADMIN)           |
| description  | TEXT         |                              | Role description                       |

---

### 3. `user_roles`
Mapping table for users and their roles (many-to-many).

| Column   | Type    | Constraints                       | Description              |
|----------|---------|-----------------------------------|--------------------------|
| user_id  | BIGINT  | PRIMARY KEY, FOREIGN KEY (users)  | References `users`       |
| role_id  | BIGINT  | PRIMARY KEY, FOREIGN KEY (roles)  | References `roles`       |

---

### 4. `tables`
Represents tables available in the coffee shop.

| Column       | Type          | Constraints                              | Description                      |
|--------------|---------------|------------------------------------------|----------------------------------|
| table_id     | BIGINT        | PRIMARY KEY, AUTO_INCREMENT              | Unique table ID                  |
| table_number | VARCHAR(10)   | NOT NULL, UNIQUE                         | Table code (e.g., T01, T02)      |
| capacity     | INT           | NOT NULL                                 | Number of seats                  |
| status       | ENUM          | DEFAULT 'AVAILABLE'                      | Status (AVAILABLE, RESERVED...)  |
| created_at   | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP                | Creation time                    |
| updated_at   | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP ON UPDATE      | Last update                      |

---

### 5. `reservations`
Stores customer table reservations.

| Column           | Type      | Constraints                              | Description                             |
|------------------|-----------|------------------------------------------|-----------------------------------------|
| reservation_id   | BIGINT    | PRIMARY KEY, AUTO_INCREMENT              | Unique reservation ID                   |
| user_id          | BIGINT    | FOREIGN KEY (users), NULLABLE            | Customer who reserved                    |
| table_id         | BIGINT    | FOREIGN KEY (tables)                     | Reserved table                          |
| reservation_time | DATETIME  | NOT NULL                                 | Time and date of reservation            |
| status           | ENUM      | DEFAULT 'PENDING'                        | PENDING, CONFIRMED, CANCELLED           |
| created_at       | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP                | Record creation                         |
| updated_at       | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP ON UPDATE      | Record update                           |

---

### 6. `menu_items`
Contains menu items available for order.

| Column       | Type           | Constraints                  | Description                         |
|--------------|----------------|------------------------------|-------------------------------------|
| item_id      | BIGINT         | PRIMARY KEY, AUTO_INCREMENT  | Unique menu item ID                 |
| item_name    | VARCHAR(100)   | NOT NULL                     | Name of the item                    |
| description  | TEXT           |                              | Description                         |
| price        | DECIMAL(10,2)  | NOT NULL                     | Item price                          |
| category     | VARCHAR(50)    |                              | Coffee, Juice, Food...              |
| image_url    | VARCHAR(255)   |                              | Image URL (e.g., Cloudinary)        |
| is_available | BOOLEAN        | DEFAULT TRUE                 | Availability                        |
| created_at   | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP    | Record creation                     |
| updated_at   | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update              |

---

### 7. `orders`
Tracks orders placed by staff or customers.

| Column         | Type          | Constraints                      | Description                                 |
|----------------|---------------|----------------------------------|---------------------------------------------|
| order_id       | BIGINT        | PRIMARY KEY, AUTO_INCREMENT      | Unique order ID                             |
| user_id        | BIGINT        | FOREIGN KEY (users), NULLABLE    | Person who placed the order                 |
| table_id       | BIGINT        | FOREIGN KEY (tables), NULLABLE   | Table associated with the order             |
| order_status   | ENUM          | DEFAULT 'PENDING'                | PENDING, SERVED, COMPLETED...               |
| payment_status | ENUM          | DEFAULT 'UNPAID'                 | UNPAID, PAID, PARTIALLY_PAID, CANCELLED     |
| total_amount   | DECIMAL(10,2) | DEFAULT 0.00                     | Total amount of the order                   |
| created_at     | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP        | Record creation                             |
| updated_at     | TIMESTAMP     | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update                             |

---

### 8. `order_items`
Items included in each order.

| Column       | Type          | Constraints                    | Description                         |
|--------------|---------------|--------------------------------|-------------------------------------|
| order_item_id| BIGINT        | PRIMARY KEY, AUTO_INCREMENT    | Unique order item ID                |
| order_id     | BIGINT        | FOREIGN KEY (orders)           | Related order                       |
| item_id      | BIGINT        | FOREIGN KEY (menu_items)       | Ordered item                        |
| quantity     | INT           | NOT NULL                       | Quantity of the item                |
| unit_price   | DECIMAL(10,2) | NOT NULL                       | Price per unit                      |
| subtotal     | DECIMAL(10,2) | GENERATED (quantity * unit_price) | Subtotal of the item            |

---

### 9. `payments`
Details about payments for orders.

| Column         | Type           | Constraints                     | Description                                |
|----------------|----------------|----------------------------------|--------------------------------------------|
| payment_id     | BIGINT         | PRIMARY KEY, AUTO_INCREMENT     | Unique payment ID                          |
| order_id       | BIGINT         | FOREIGN KEY (orders)            | Related order                              |
| amount         | DECIMAL(10,2)  | NOT NULL                        | Amount paid                                |
| payment_method | ENUM           | NOT NULL                        | CASH, CARD, MOBILE, BANK_TRANSFER          |
| payment_status | ENUM           | DEFAULT 'PENDING'               | PENDING, COMPLETED, FAILED, REFUNDED       |
| payment_time   | DATETIME       |                                  | Time of payment                            |
| transaction_id | VARCHAR(100)   | UNIQUE                          | ID from payment gateway                    |
| created_at     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP       | Record creation                            |
| updated_at     | TIMESTAMP      | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update                           |

---

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

### 1. 🔐 Authentication API
Handles user authentication and session management.

| Method | Endpoint             | Description                  |
|--------|----------------------|------------------------------|
| POST   | `/api/auth/register` | Register a new user          |
| POST   | `/api/auth/login`    | Authenticate user and get access token  |
| POST   | `/api/auth/refresh`  | Refresh the access token     |
| GET    | `/api/auth/me`       | Get authenticated user's information |
| POST   | `/api/auth/logout`   | Log out the user             |

---

### 2. 👤 User API
Manages user accounts and roles.

| Method | Endpoint           | Description                  |
|--------|--------------------|------------------------------|
| GET    | `/api/users`       | Retrieve all users           |
| GET    | `/api/users/{id}`  | Get details of a specific user |
| PUT    | `/api/users/{id}`  | Update user information      |
| DELETE | `/api/users/{id}`  | Delete a user                |

---

### 3. 🍽️ Table API
Handles table creation and management.

| Method | Endpoint             | Description                  |
|--------|----------------------|------------------------------|
| GET    | `/api/tables`        | Get all tables               |
| GET    | `/api/tables/{id}`   | Get specific table details   |
| POST   | `/api/tables`        | Create a new table           |
| PUT    | `/api/tables/{id}`   | Update table information     |
| DELETE | `/api/tables/{id}`   | Delete a table               |

---

### 4. 📅 Reservation API
Manages table reservations.

| Method | Endpoint                 | Description                    |
|--------|--------------------------|--------------------------------|
| GET    | `/api/reservations`      | Get all reservations           |
| POST   | `/api/reservations`      | Create a new reservation       |
| PUT    | `/api/reservations/{id}` | Update reservation information |
| DELETE | `/api/reservations/{id}` | Cancel/delete a reservation    |

---

### 5. 📋 Menu API
Handles the restaurant's menu items.

| Method | Endpoint            | Description                    |
|--------|---------------------|--------------------------------|
| GET    | `/api/menus`        | List all menu items            |
| GET    | `/api/menus/{id}`   | Get details of a menu item     |
| POST   | `/api/menus`        | Add a new menu item            |
| PUT    | `/api/menus/{id}`   | Update a menu item             |
| DELETE | `/api/menus/{id}`   | Delete a menu item             |

---

### 6. 🧾 Order API
Manages customer orders.

| Method | Endpoint           | Description                   |
|--------|--------------------|-------------------------------|
| GET    | `/api/orders`      | List all orders               |
| GET    | `/api/orders/{id}` | Get details of a specific order |
| POST   | `/api/orders`      | Create a new order            |
| PUT    | `/api/orders/{id}` | Update an order               |
| DELETE | `/api/orders/{id}` | Cancel/delete an order        |

---

### 7. 💳 Payment API
Handles order payments.

| Method | Endpoint             | Description                      |
|--------|----------------------|----------------------------------|
| GET    | `/api/payments`      | List all payments                |
| GET    | `/api/payments/{id}` | Get details of a specific payment |
| POST   | `/api/payments`      | Create a new payment             |
| PUT    | `/api/payments/{id}` | Update payment status/details    |
| DELETE | `/api/payments/{id}` | Delete a payment record          |

---


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
