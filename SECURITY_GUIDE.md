# LM-APIService Security Guide

## Overview

The LM-APIService is now secured with JWT (JSON Web Token) authentication. All API endpoints require a valid access token obtained from the LM-AuthService.

## Authentication Flow

1. **Register/Login** - Get access token from `http://localhost:8082/api/auth/login`
2. **Use Access Token** - Include token in `Authorization` header for all API requests
3. **Refresh Token** - When access token expires, use refresh token to get a new one

## Secured Endpoints

All endpoints under `/api/*` (except `/api/health`) require authentication:

- `GET /api/authors` - Get all authors
- `GET /api/authors/{id}` - Get author by ID
- `POST /api/authors` - Create author
- `DELETE /api/authors/{id}` - Delete author
- `GET /api/authors/search?q=name` - Search authors
- `GET /api/authors/{id}/books` - Get books by author
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create book
- `PUT /api/books/{id}` - Update book
- `DELETE /api/books/{id}` - Delete book
- `GET /api/books/genre/{genre}` - Get books by genre
- `GET /api/books/author/{authorId}` - Get books by author ID

## Public Endpoints

- `GET /api/health` - Health check (no authentication required)

## How to Use

### Step 1: Login to Get Access Token

```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePassword123!"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "username": "john_doe",
  "email": "john@example.com",
  "role": "USER"
}
```

### Step 2: Use Access Token in API Requests

Include the access token in the `Authorization` header:

```bash
curl -X GET http://localhost:8081/api/authors \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json"
```

### Step 3: Create Author (Example)

```bash
curl -X POST http://localhost:8081/api/authors \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "name": "J.K. Rowling",
    "email": "jkrowling@example.com"
  }'
```

### Step 4: Create Book (Example)

```bash
curl -X POST http://localhost:8081/api/books \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Harry Potter",
    "genre": "Fantasy",
    "authorId": 1
  }'
```

## Error Responses

### 401 Unauthorized - Missing or Invalid Token

```json
{
  "error": "Unauthorized"
}
```

**Causes:**
- Missing `Authorization` header
- Invalid or expired access token
- Malformed token

### 403 Forbidden - Access Denied

```json
{
  "error": "Access denied: ..."
}
```

**Causes:**
- Token is valid but user doesn't have required permissions

## JavaScript/Fetch Examples

### Making Authenticated Requests

```javascript
// Get access token from login
const login = async () => {
  const response = await fetch('http://localhost:8082/api/auth/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      username: 'john_doe',
      password: 'SecurePassword123!'
    })
  });
  
  const data = await response.json();
  localStorage.setItem('accessToken', data.accessToken);
  return data.accessToken;
};

// Use token in API requests
const getAuthors = async () => {
  const accessToken = localStorage.getItem('accessToken');
  
  const response = await fetch('http://localhost:8081/api/authors', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    }
  });
  
  if (response.status === 401) {
    // Token expired, need to refresh or login again
    console.error('Token expired');
    return;
  }
  
  const authors = await response.json();
  return authors;
};

// Create author
const createAuthor = async (name, email) => {
  const accessToken = localStorage.getItem('accessToken');
  
  const response = await fetch('http://localhost:8081/api/authors', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${accessToken}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      name: name,
      email: email
    })
  });
  
  if (response.status === 401) {
    console.error('Token expired');
    return;
  }
  
  const author = await response.json();
  return author;
};
```

## Postman Configuration

### Setting Up Authorization

1. Create a new request
2. Go to the **Authorization** tab
3. Select **Type: Bearer Token**
4. Paste your access token in the **Token** field
5. The token will be automatically added to the `Authorization` header

### Using Environment Variables

1. Create an environment variable `accessToken`
2. Set it after login: `{{accessToken}} = <your-token>`
3. Use it in Authorization: `Bearer {{accessToken}}`

## Token Expiration

- **Access Token**: Valid for 24 hours
- **Refresh Token**: Valid for 7 days

When the access token expires:
1. Use the refresh token to get a new access token
2. Or login again to get new tokens

## Testing Without Authentication

If you try to access a secured endpoint without a token:

```bash
curl -X GET http://localhost:8081/api/authors
```

**Response:**
```json
{
  "error": "Unauthorized"
}
```

## Configuration

The JWT secret must match between:
- **LM-AuthService** (`application.properties`)
- **LM-APIService** (`application.properties`)

Both services use the same secret to validate tokens:
```properties
jwt.secret=your-secret-key-change-this-in-production-to-a-strong-random-string
```

## Security Notes

1. **Never expose JWT secret** in production
2. **Use HTTPS** in production
3. **Store tokens securely** (httpOnly cookies, secure storage)
4. **Rotate secrets** regularly
5. **Monitor token expiration** and handle refresh gracefully

