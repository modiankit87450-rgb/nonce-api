# Nonce API - Spring Boot + MySQL

## Setup karo

### 1. MySQL mein database banao
```sql
CREATE DATABASE nonce_db;
```

### 2. application.properties update karo
```
spring.datasource.url=jdbc:mysql://localhost:3306/nonce_db
spring.datasource.username=root
spring.datasource.password=yourpassword
```

### 3. Run karo
```bash
mvn spring-boot:run
```
Table automatically ban jayegi (`nonce_store`).

---

## API Usage

### Generate Nonce
**POST** `http://localhost:8080/api/v2/generateNonce`

**Headers:**
```
Content-Type: application/json
x-request-id: any-unique-id   (optional)
```

**Request Body:**
```json
{
  "deviceId":    "device-abc-123",
  "appId":       "com.example.myapp",
  "deviceMake":  "Samsung",
  "deviceModel": "Galaxy S21",
  "osVersion":   "13",
  "appVersion":  "1.0.0"
}
```

**Response 200:**
```json
{
  "status":     "SUCCESS",
  "nonce":      "base64encodedNonceValue",
  "deviceId":   "device-abc-123",
  "appId":      "com.example.myapp",
  "expiresAt":  "2024-01-01T10:10:00",
  "xRequestId": "auto-generated-uuid"
}
```

**Error Response:**
```json
{
  "status":    "FAILURE",
  "errorCode": "400",
  "message":   "deviceId: deviceId is required"
}
```

### Health Check
**GET** `http://localhost:8080/api/v2/health`

---

## Notes
- Nonce **10 minutes** mein expire hota hai (configurable)
- Nonce ek baar use hone ke baad mark as `used=true`
- Expired nonces har 5 minute mein auto-delete hote hain
- DB mein plaintext nonce store nahi hota — sirf SHA-256 hash store hota hai
