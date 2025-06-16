# 📚 Doubt Solving Chat App

An intelligent and interactive platform that connects students with mentors and AI to get instant doubt resolution through chat, voice, or video formats.

---

## 🚀 Features

- 🔐 **Authentication**
  - Google Sign-in / Email & Password via Firebase
  - Role-based access: Student | Mentor

- 📩 **Doubt Submission**
  - Students can post textual doubts with optional image/video
  - Tagging system for subject/topic categorization

- 💬 **Answer Mechanisms**
  - Manual mentor answers (chat-based)
  - Voice/video reply support
  - AI-generated answers using Gemini API

- 🎯 **Doubt Tracking**
  - View status: Unanswered | Answered | In Review
  - Timeline history of conversations

- 🌐 **Tech Stack**
  - **Backend**: Spring Boot + MySQL + Gemini AI API

---

## 🛠️ Installation & Setup

### Backend (Spring Boot)

```bash
cd backend/
mvn clean install
npm start
