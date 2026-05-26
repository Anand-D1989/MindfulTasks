# 🧘 MindfulTasks Android App

Welcome to **MindfulTasks**, your custom native Android application designed to help you organize your daily focus and write daily reflection logs in a serene, distraction-free environment.

This project is built using modern Android standards:
- **Kotlin**: The official, modern programming language for Android development.
- **Jetpack Compose**: Google's modern declarative UI toolkit (Material 3) for crafting premium, beautiful user interfaces.
- **Gradle (Kotlin DSL)**: The industry standard tool for compiling and managing libraries.
- **MVVM Architecture**: Follows the Model-View-ViewModel clean design pattern to separate your data logic from the visual layout.

---

## 📁 Understanding Your Project Structure
Since you are new to coding, here is a quick tour of what is inside this repository:
- `app/src/main/java/com/mindful/tasks/`
  - `MainActivity.kt`: The main entryway of your app. It initializes your app structure and runs our beautiful custom UI.
  - `viewmodel/TaskViewModel.kt`: The "Brain". It manages your tasks, checks items off, adds journal logs, and handles data.
  - `ui/screens/TaskScreen.kt`: The "Face". This is where all the Jetpack Compose visual interfaces (buttons, text inputs, logs, tabs) are coded.
  - `ui/theme/`: Contains `Color.kt`, `Theme.kt`, and `Type.kt` which together define our Obsidian dark mode and premium styling.
- `app/src/main/AndroidManifest.xml`: An configuration file telling the Android system what the name of your app is and how to start it.
- `build.gradle.kts` & `settings.gradle.kts`: Build instructions listing the tools and Compose libraries we need.

---

## 🚀 How to Run the App on Your Mac (Step-by-Step)

To see this app in action, you can open it in **Android Studio** (Google's official free app maker tool).

### Step 1: Download Android Studio
1. Go to [developer.android.com/studio](https://developer.android.com/studio) and download Android Studio for macOS.
2. Select **"Mac with Apple Chip"** (since your Mac is powered by an Apple Silicon M1/M2/M3 processor).
3. Install and launch it.

### Step 2: Open the Project
1. In Android Studio, select **Open** or **Import**.
2. Navigate to your workspace folder: `/Users/developer/Documents/AG2.0` and click **Open**.
3. Android Studio will automatically recognize the files, set up the standard files (like Gradle wrappers), and fetch the necessary files to run it. *(Please wait a minute or two for this "Gradle Sync" to finish!)*

### Step 3: Run on an Android Emulator (Virtual Phone)
1. In the top right corner of Android Studio, click on **Device Manager** (the icon looks like a small phone next to a gear).
2. Click **Create Virtual Device** ➔ Choose **Pixel 8** (or any device you like) ➔ Click **Next**.
3. Select an Android system version (e.g., API 34 or "UpsideDownCake") and download it.
4. Click **Finish**.
5. Once your emulator is ready, look at the top menu bar, select your new virtual phone, and click the green **Run** button (play icon ◀). The emulator will boot up, and **MindfulTasks** will install and run!

---

## 📦 How to Build Your APK (Share it with Friends!)

An **APK** is a shareable application installer file for Android phones. You can easily build it:
1. In the top toolbar of Android Studio, click on **Build** from the menu.
2. Hover over **Build Bundle(s) / APK(s)** and click **Build APK(s)**.
3. Android Studio will compile all the code. Once it finishes, a small pop-up will appear in the bottom-right corner.
4. Click on **locate** inside that pop-up. This will open your Finder and show you a file named `app-debug.apk` (or similar).
5. You can copy this file directly to any Android phone, open it, and tap **Install** to run it natively!

---

## 🐙 How to Upload this Code to Git & GitHub

We have already initialized a local Git repository for you and committed your files! Follow these steps to upload it online:

### Step 1: Create a GitHub Account
If you don't have one, head to [github.com](https://github.com) and sign up for a free account.

### Step 2: Create a New Repository on GitHub
1. Once logged in, click the green **New** button (or the `+` icon in the top right corner and select **New repository**).
2. Name your repository `MindfulTasks`.
3. Leave it **Public** or **Private** (whichever you prefer).
4. **IMPORTANT**: Do *NOT* check the options to "Add a README file", "Add .gitignore", or "Choose a license" (we have already created all of these for you!).
5. Click the green **Create repository** button.

### Step 3: Connect your Mac to GitHub and Push
On your Mac, open the **Terminal** app (or run the following commands in this editor) and type the following commands:

```bash
# 1. Open the directory of your project
cd /Users/developer/Documents/AG2.0

# 2. Add your GitHub repository link (replace with your actual GitHub username and URL!)
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/MindfulTasks.git

# 3. Rename the default branch to 'main'
git branch -M main

# 4. Push your code to GitHub!
git push -u origin main
```
*Note: GitHub may ask you to log in or enter your credentials. If you have "GitHub Desktop" installed, you can also simply import this folder directly into GitHub Desktop to publish it with a visual interface!*
