# 📖 Choose Your Own Adventure – Interactive Storytelling App
# 📌 Overview

This Android mobile application allows users to immerse themselves in interactive storytelling. Unlike traditional reading apps, readers can shape the story’s direction through their choices, while authors can create and publish branching narratives.

The app supports two user roles:
1. Readers: Explore stories, make choices, bookmark progress, and follow authors.
2. Authors: Write stories with branching paths, publish them, and view reader statistics.

Developed in Kotlin with Android Studio, and powered by Firebase Authentication, Realtime Database, and Storage, the app ensures a seamless, real-time experience.

# 🚀 Features
👥 User Management
1. Email/password sign-up & login with role selection (Author or Reader)
2. Profile customisation (bio, display name, profile picture)
3. Firebase authentication & secure session handling

# ✍️ Author Features
1. Create and publish interactive stories with multiple pages & choices
2. Manage content (add/edit/delete pages, upload cover images)
3. Embed multimedia (PDF, audio, video) into stories
4. Track story statistics (readers, path choices, completion rates)

# 📚 Reader Features
1. Browse and filter stories by genre, title, or popularity
2. Add stories to personal library with progress tracking
3. Navigate stories via interactive choices
4. View story path history and outcomes
5. Follow favourite authors and receive updates

# 📊 Advanced Features
1. Choice conditions (e.g., based on past decisions or time of day)
2. Real-time sync of choices and progress across devices
3. Email notifications when authors publish new stories

🛠️ Tech Stack
1. Language & Frameworks: Kotlin, Android Jetpack Compose, Material Design
2. Backend Services: Firebase Authentication, Firebase Realtime Database, Firebase Storage
3. Libraries: Glide (image loading), RecyclerView, Google Material Components
4. Testing: Unit, integration, and performance testing with Android Profiler and JUnit

🏗️ Architecture
1. Frontend (Android + Kotlin): Dynamic UI, navigation drawer, bottom navigation bar
2. Backend (Firebase): Authentication, realtime database, storage for media
3. Data Flow: JSON-like key-value structure (Users, Stories, Pages, Libraries, Favourites)

📂 Database Design
1. Users: email, name, password, role, favourites, library
2. Stories: authorId, title, description, category, cover image
3. Pages: text, choices → linked to next page IDs
4. Reader Libraries: personalised progress and bookmarks
5. Interaction Tracking: real-time path and choice data

✅ Testing & Validation
1. Unit Testing: Components like authentication and story navigation
2. Integration Testing: Frontend ↔ Firebase services
3. Performance Testing: Load times, memory usage, response times
4. User Acceptance Testing: Improved navigation & visual story path features based on feedback
5. Security Testing: Verified Firebase authentication & secure data handling

🔒 Security & Compliance
1. Firebase Authentication & secure token management
2. SSL encryption for all data transfers
3. Password hashing and role-based access
4. GDPR-compliant data handling

📈 Achievements
1. Successfully implemented a platform for interactive storytelling
2. Positive user feedback on engagement and usability
3. Advanced Firebase integration for real-time updates and scalability
4. Built with modern Android development practices (Jetpack Compose)

📌 Future Enhancements
1. Multi-platform support (iOS & web)
2. Larger story library & author collaborations
3. Community features (comments, ratings, forums)
4. Push notifications & reminders

👩‍💻 Author

Tanvi Patil
MSc Computer Science, University of Southampton (2024)
