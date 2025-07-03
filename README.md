# 🚀 Doubt Solver App

An intelligent, modern platform connecting students and mentors for instant doubt resolution. Built with Spring Boot, Thymeleaf, and MySQL, it offers a beautiful UI, role-based access, and AI-powered answers.

---

## Deployment Link: https://doubtsolverapp-production.up.railway.app/

## ✨ Features

- **Modern Home & About Pages**  
  Beautiful, responsive landing and about pages with mission, features, and team info.

- **Authentication & Roles**  
  - Secure login and registration
  - Role-based dashboards for Students and Mentors

- **Doubt Management**  
  - Students can post, edit, and track doubts
  - Mentors can answer, edit, and manage doubts

- **AI Integration**  
  - Get instant answers powered by Gemini AI API

- **Profile Management**  
  - Update personal info and view activity

- **Responsive UI**  
  - Custom dashboards for students and mentors
  - Styled with modern CSS and SVG illustrations

---


## 🛠️ Tech Stack

- **Backend:** Spring Boot 3, Java 17, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf templates, HTML5, CSS3
- **Database:** MySQL
- **AI:** Gemini API
- **Build:** Maven
- **Containerization:** Docker

---

## ⚡ Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/yourusername/doubt-solver-app.git
cd doubt-solver-app
```

### 2. Configure the Database

Edit `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/doubt_app?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=yourpassword
server.port=8081
```

### 3. Build & Run

#### Using Maven

```bash
./mvnw spring-boot:run
```
or on Windows:
```bash
mvnw.cmd spring-boot:run
```

#### Using Docker

```bash
docker build -t doubt-solver-app .
docker run -p 8081:8081 doubt-solver-app
```

### 4. Access the App

- Home: [http://localhost:8081/](http://localhost:8081/)
- About: [http://localhost:8081/about](http://localhost:8081/about)
- Login/Register: via navigation links

---

## 🧑‍💻 Project Structure

```
src/
  main/
    java/com/doubtapp/backend/
      controller/    # Spring MVC controllers
      model/         # JPA entities
      repository/    # Spring Data repositories
      service/       # Business logic & AI integration
    resources/
      templates/     # Thymeleaf HTML templates
      static/        # Static files (images, CSS, JS)
      application.properties
```

---

## 🙋‍♂️ Meet the Developer

Made with love by [Guru Singh Pal](https://github.com/GuruSinghPal) ❤️

---

## 📄 License

This project is for educational purposes.
