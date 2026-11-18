# Survey Platform

The Survey Management System is a full-stack application designed to create and manage surveys, collect responses, and analyze submitted data.

The backend is developed using Java and Spring Boot. It provides REST APIs that allow the frontend application to communicate with the database and perform survey-related operations.

The backend handles survey creation, updating, publishing, closing, response submission, answer validation, response retrieval, analytics, and CSV export.

The application uses SQLite as its database during development.

## Technology Stack

| Technology         | Purpose                                   |
| ------------------ | ----------------------------------------- |
| Java 21            | Backend programming language              |
| Spring Boot        | Backend application framework             |
| Spring Web         | REST API development                      |
| Spring Data JPA    | Database access and repository management |
| Hibernate          | Object-relational mapping                 |
| SQLite             | Database management                       |
| Maven              | Dependency management and project build   |
| Jackson            | JSON processing and serialization         |
| Jakarta Validation | Request validation                        |

# How to Run the Project

There are three ways to run the Survey Management System:

1. Using Dockerfiles (individual containers).
2. Using Docker Compose.
3. Running locally without Docker.

## 1. Run Using Dockerfiles

This approach builds and runs the frontend and backend containers separately.

### Prerequisites

- Docker installed and running.
- Backend and frontend Dockerfiles configured.
- Frontend `nginx.conf` configured.

### Step 1: Navigate to the Project Root

```bash
cd survey-management-system
```

### Step 2: Create a Docker Network and Database Volume

```bash
docker network create survey-network
docker volume create survey-db
```

The network allows the frontend Nginx container to communicate with the backend container. The volume preserves SQLite database data.

### Step 3: Build the Backend Image

```bash
docker build -t survey-backend ./backend
```

### Step 4: Run the Backend Container

```bash
docker run -d \
  --name backend \
  --network survey-network \
  -p 5454:5454 \
  -v survey-db:/data \
  -e SERVER_PORT=5454 \
  -e SPRING_DATASOURCE_URL=jdbc:sqlite:/data/mydb.db \
  survey-backend
```

The backend will be available at:

```text
http://localhost:5454
```

### Step 5: Build the Frontend Image

```bash
docker build \
  --build-arg VITE_API_BASE_URL=/api \
  -t survey-frontend ./frontend
```

### Step 6: Run the Frontend Container

```bash
docker run -d \
  --name frontend \
  --network survey-network \
  -p 5173:80 \
  survey-frontend
```

The frontend will be available at:

```text
http://localhost:5173
```

Nginx serves the React application and forwards `/api` requests to the backend container.

### Step 7: Verify Running Containers

```bash
docker ps
```

You should see both containers running:

- `backend`
- `frontend`

### Stop the Containers

```bash
docker stop frontend backend
```

Remove the containers:

```bash
docker rm frontend backend
```

The Docker network and database volume can remain for future use.

---

## 2. Run Using Docker Compose (Recommended)

Docker Compose provides an easier way to build and run both applications together.

It automatically creates the required network and database volume.

### Prerequisites

- Docker installed and running.
- Docker Compose v2.
- `docker-compose.yml` present in the project root.

### Step 1: Navigate to the Project Root

```bash
cd survey-management-system
```

### Step 2: Build and Start Both Applications

```bash
docker compose up --build -d
```

This command:

- Builds the backend image.
- Builds the frontend image.
- Creates the frontend and backend containers.
- Creates the required Docker network.
- Creates the SQLite database volume.
- Starts both applications.

### Step 3: Verify Container Status

```bash
docker compose ps
```

### Step 4: Access the Application

**Frontend:**

```text
http://localhost:5173
```

**Backend:**

```text
http://localhost:5454
```

### Step 5: View Application Logs

View all logs:

```bash
docker compose logs -f
```

View backend logs:

```bash
docker compose logs -f backend
```

View frontend logs:

```bash
docker compose logs -f frontend
```

### Stop the Application

```bash
docker compose down
```

This stops and removes the containers and network but preserves the named database volume.

To rebuild and restart after making changes:

```bash
docker compose up --build -d
```

**Important:** Avoid `docker compose down -v` unless you intentionally want to delete the persistent SQLite database volume and its data.

---

## 3. Run Locally Without Docker

This approach runs the Spring Boot backend and React frontend directly on your machine.

### Prerequisites

Install the following:

- Java 21
- Maven
- Node.js
- npm
- Git

### Step 1: Clone the Repository

```bash
git clone <repository-url>
```

Navigate to the project:

```bash
cd survey-management-system
```

### Step 2: Start the Backend

Navigate to the backend directory:

```bash
cd backend
```

Check the `src/main/resources/application.properties` configuration.

Example:

```properties
spring.application.name=survey-backend

server.port=5454

spring.datasource.url=jdbc:sqlite:mydb.db

spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect

spring.jpa.hibernate.ddl-auto=update
```

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

Alternatively, use the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The backend will be available at:

```text
http://localhost:5454
```

### Step 3: Start the Frontend

Open another terminal and navigate to the frontend directory:

```bash
cd survey-management-system/frontend
```

Install the dependencies:

```bash
npm install
```

Create a `.env` file in the frontend directory:

```env
VITE_API_BASE_URL=http://localhost:5454/api
```

Start the React development server:

```bash
npm run dev
```

The frontend will be available at:

```text
http://localhost:5173
```

The actual port may differ if Vite selects another available port.

### Step 4: Verify the Application

Open the frontend in your browser:

```text
http://localhost:5173
```

Make sure both applications are running:

| Application | URL |
|---|---|
| React Frontend | `http://localhost:5173` |
| Spring Boot Backend | `http://localhost:5454` |

Ensure the backend CORS configuration allows the frontend development origin (`http://localhost:5173`).

---

## Quick Reference

| Execution Method | Command |
|---|---|
| Build backend image | `docker build -t survey-backend ./backend` |
| Build frontend image | `docker build --build-arg VITE_API_BASE_URL=/api -t survey-frontend ./frontend` |
| Run with Docker Compose | `docker compose up --build -d` |
| Stop Docker Compose | `docker compose down` |
| Run backend locally | `mvn spring-boot:run` |
| Run frontend locally | `npm run dev` |

**Application URLs**

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:5454`

**Note:** When using the individual Dockerfiles, the frontend and backend must share the same Docker network because Nginx forwards API requests to the backend container using its Docker network name. The Docker Compose setup configures this automatically.