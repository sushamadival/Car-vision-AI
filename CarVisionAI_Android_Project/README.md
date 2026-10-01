# CarVision AI - Android Application

CarVision AI is an AI-powered Android mobile application built with **Android Studio, Kotlin, and Jetpack Compose** to automatically identify vehicle make, model, colour, and confidence percentages from images.

## Features
- Real-time Camera capture using Android CameraX
- Gallery selection using Android Activity Result APIs
- Gemini Multimodal image recognition using Google Generative AI Android SDK
- Automotive Dark Theme with Material 3 styling
- Low-confidence model handling ("Model could not be identified with high confidence.")

## Quick Setup Guide
1. Open Android Studio (Ladybug, Meerkat, or newer).
2. Select **Open** and select this extracted directory.
3. Open `local.properties` and add your Gemini API key:
   ```properties
   GEMINI_API_KEY=YOUR_GEMINI_API_KEY_HERE
   ```
4. Sync Gradle and run the app on an Android device or emulator (Android 7.0+ / API 24+).
