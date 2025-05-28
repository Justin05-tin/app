# nammoadidaphat

**nammoadidaphat** is a modern Android application designed to help users exercise at home, featuring a clean and interactive UI built with Jetpack Compose and robust backend services powered by Firebase. The app offers personalized workout routines, user authentication, and secure data management, making it easy for anyone to stay fit from the comfort of their home.

## Features

- **Jetpack Compose UI**: Enjoy a beautiful, responsive, and modern user interface built entirely with Jetpack Compose.
- **Firebase Integration**:
  - **Authentication**: Secure login, registration, and password reset using Firebase Auth.
  - **Firestore Database**: Store and retrieve user and workout data in real time.
  - **Firebase Storage**: Manage and display workout images and media.
- **Personalized Workouts**:
  - Workouts categorized by body parts: Abs, Arms, Chest, and Legs.
  - Multiple difficulty levels (Beginner, Intermediate, Advanced) for each category.
  - Workout details include duration, number of exercises, and difficulty rating.
- **User Management**:
  - Register with email, password, and a security key for password recovery.
  - Forgot password flow with security key verification and email reset.
- **Motivational Tags**: Filter and discover workouts by tags like "Keep Fit", "Build Muscle", "Stretch", and more.
- **Image Support**: Uses Coil for efficient image and GIF loading.
- **Navigation**: Smooth in-app navigation using Navigation Compose.

## Screenshots

*(Add screenshots of Home, Login, Register, and Workout screens here)*

## Getting Started

### Prerequisites

- Android Studio Flamingo or newer
- Android device or emulator (API 24+)
- Firebase project with Authentication, Firestore, and Storage enabled

### Installation

1. **Clone the repository:**
   ```bash
   git clone <your-repo-url>
   cd nammoadidaphat
   ```

2. **Open in Android Studio** and let Gradle sync.

3. **Firebase Setup:**
   - Download your `google-services.json` from the Firebase Console and place it in `app/`.

4. **Run the app** on your device or emulator.

## Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Firebase Auth, Firestore, Storage**
- **Coil (for image loading)**
- **Navigation Compose**
- **Material Design 3**

## Project Structure

```
app/
  └── src/
      └── main/
          ├── java/com/example/nammoadidaphat/
          │   ├── HomeScreen.kt
          │   ├── LoginScreen.kt
          │   ├── RegisterScreen.kt
          │   ├── ForgotPasswordScreen.kt
          │   └── ... (other UI and logic files)
          └── res/
              └── values/
                  └── strings.xml
```

## Customization

- **Add new workouts**: Update the workout lists in `HomeScreen.kt`.
- **Change tags**: Edit the tags in the `HomeScreen.kt` for more motivational categories.
- **UI Themes**: Modify Compose theme files for a custom look and feel.

## Contributing

Contributions are welcome! Please open issues or submit pull requests for improvements and new features.

## License

This project is licensed under the MIT License.
