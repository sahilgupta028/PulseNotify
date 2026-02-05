# 🚀 PulseNotify – Event‑Driven Notification Microservice

PulseNotify is a **production‑grade, event‑driven notification microservice** built to demonstrate **real‑world backend engineering practices** using **Spring Boot, RabbitMQ, PostgreSQL, and a Node.js email gateway**.

This project is intentionally designed as an **interview‑ready system** that showcases asynchronous processing, message queues, retries, dead‑letter queues (DLQ), idempotency, clean architecture, and external service integration.

---

## 🧠 Why PulseNotify?

In real systems, sending notifications **synchronously** is a bad idea:

* Email/SMS providers can fail
* Notifications are slow and blocking
* Retries can break APIs

PulseNotify solves this by:

* Decoupling notification creation from delivery
* Processing messages asynchronously via RabbitMQ
* Retrying failures safely
* Moving poison messages to a Dead Letter Queue

---

## 🏗️ High‑Level Architecture

```
 Client / Other Services
        |
        v
  REST API (Spring Boot)
        |
        v
   PostgreSQL (PENDING)
        |
        v
    RabbitMQ Exchange
        |
        v
 Notification Consumer
        |
        v
 Node.js Email Service (Nodemailer)
```

---

## 🧩 Components

### 1️⃣ Spring Boot Messaging Service

Responsible for:

* Accepting notification requests
* Persisting notification state
* Publishing events to RabbitMQ
* Consuming messages
* Retry & DLQ handling

### 2️⃣ RabbitMQ

* Main Queue → Notification processing
* Retry mechanism → controlled retries
* Dead Letter Queue → failed messages

### 3️⃣ PostgreSQL

Stores:

* Notification metadata
* Status tracking
* Retry count

### 4️⃣ Node.js Email Gateway

* Sends emails using Nodemailer
* Isolated from core business logic
* Can be replaced by any provider (SES, SendGrid, etc.)

---

## 📦 Tech Stack

| Layer      | Technology               |
| ---------- | ------------------------ |
| Backend    | Spring Boot 3            |
| Messaging  | RabbitMQ (Cloud / Local) |
| Database   | PostgreSQL               |
| Email      | Node.js + Nodemailer     |
| Templates  | Thymeleaf / HTML         |
| Security   | Spring Security          |
| Build Tool | Maven                    |

---

## 📁 Project Structure

```
pulsenotify-messaging/
├── .env
├── pom.xml
├── src/main/java/com/pulsenotify/
│   ├── config/
│   ├── controller/
│   ├── service/
│   ├── entity/
│   ├── repository/
│   ├── dto/
│   └── constant/
└── src/main/resources/
    ├── application.properties
    └── templates/email-template.html
```

---

## 🔐 Environment Variables (.env)

```
DB_URL=jdbc:postgresql://localhost:5432/pulsenotify
DB_USER=pulsenotify_user
DB_PASSWORD=secret

RABBIT_HOST=cloudamqp.com
RABBIT_USER=guest
RABBIT_PASS=guest

EMAIL_SERVICE_URL=http://localhost:3001
```

✔ `.env` is **never committed**
✔ Secrets are injected at runtime

---

## 📡 REST APIs

### ➤ Create Notification

`POST /api/notifications`

```json
{
  "type": "EMAIL",
  "recipient": "user@gmail.com",
  "subject": "Welcome",
  "payload": {
    "message": "Welcome to PulseNotify"
  }
}
```

✔ Response: `202 ACCEPTED`

---

### ➤ Get Notification Status

`GET /api/notifications/{id}`

Response:

```json
{
  "id": 5,
  "status": "SENT",
  "retryCount": 0
}
```

---

## 🔁 Retry & DLQ Strategy

* Max retries: **3**
* Delay between retries: **10 seconds**
* After max retries → message sent to **DLQ**

Statuses:

* `PENDING`
* `RETRY`
* `SENT`
* `FAILED`

---

## ✉️ Email Flow

1. Consumer receives message
2. Fetches notification from DB
3. Calls Node.js email service
4. Updates status
5. Retries on failure

---

## 🧪 Testing via Postman

1. Start RabbitMQ
2. Start PostgreSQL
3. Start Node.js email service
4. Start Spring Boot app
5. Call `POST /api/notifications`
6. Observe logs & DB updates

---

## 🔒 Security

* CSRF disabled (API‑only)
* Stateless APIs
* Ready for JWT integration

---

## 🧠 Interview Topics Covered

* Why async notifications?
* At‑least‑once delivery
* Idempotency
* Retry vs DLQ
* Event‑driven design
* External service isolation

---

## 📈 Future Enhancements

* SMS & Push notifications
* Rate limiting
* Circuit breaker (Resilience4j)
* Metrics & monitoring
* Kubernetes deployment

---

## 👨‍💻 Author

**Sahil Gupta**
Backend Developer | Spring Boot | Microservices

---

⭐ If you’re reviewing this project as an interviewer: this system is intentionally designed to mirror real production architecture.
