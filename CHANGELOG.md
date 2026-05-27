# 🧘 MindfulTasks - My Project Diary (Ch Changelog)

Welcome to your project diary! This file keeps track of every single change we make in plain, simple English. As you learn to code, you can read this file to easily understand what each file is, what it does, and why we created it—without any confusing tech jargon!

---

## 📅 May 26, 2026: Creating the App from Scratch 🚀

Today, we built the complete foundation for your Android app, **MindfulTasks**. Think of this step as building the frame, walls, wiring, and design for a brand new house. 

Here is exactly what we created, explained in everyday terms:

### 🏠 The App's Visual Features (What You See)
We created a beautiful **Obsidian Dark Mode** theme. It uses deep charcoal backgrounds, smooth borders, and glowing green accents to look clean and premium.
The app has two main screens you can switch between:
1. **The Tasks Planner**: A clean checklist where you can add tasks, choose their priority (Low, Medium, High), categorize them, and check them off.
2. **The Zen Journal**: A peaceful reflection diary where you can write down your daily thoughts and pick a mood emoji (like 🧘, 🚀, or 😊) to save with it.

---

### 📁 The Files We Created & What They Do

To build this app, we had to create several files. Here is an easy guide to what each one does:

#### 1. The Design Team (Styling & Colors)
* **`Color.kt` (The Paint Palette)**: 
  * *What it is*: A file that holds our official colors.
  * *Layman explanation*: Instead of manually coloring buttons, we define colors like "ObsidianBlack" and "EmeraldZen" here so we can reuse them across the whole app.
* **`Type.kt` (The Font Book)**: 
  * *What it is*: A file that controls the text fonts and sizes.
  * *Layman explanation*: It ensures that headers are big and bold, while task descriptions are clean and easy to read.
* **`Theme.kt` (The Dark Mode Switch)**: 
  * *What it is*: The overarching style settings.
  * *Layman explanation*: It glues our Paint Palette and Font Book together and tells your phone's status bar to look dark and seamless.

#### 2. The Core App Engine (The Logic & Screens)
* **`MainActivity.kt` (The Main Door Key)**: 
  * *What it is*: The entry point of your Android app.
  * *Layman explanation*: When you tap the app icon on your phone screen, this file acts as the key that unlocks the app and opens the front door.
* **`TaskViewModel.kt` (The Manager Brain)**: 
  * *What it is*: The data manager.
  * *Layman explanation*: This is the smartest file in the app. It acts like a manager sitting at a desk. It holds the lists of tasks and journal entries in memory. When you click "Add Task" or "Check Box," the Screen tells the Manager, and the Manager updates the memory.
* **`TaskScreen.kt` (The Interactive Pages)**: 
  * *What it is*: The actual user interface code.
  * *Layman explanation*: This is the visual layout of the app. It creates the tabs, the text input boxes, the submit buttons, and the checklist items that you see and tap on your screen.

#### 3. Behind-the-Scenes Settings (The Building Foundation)
* **`AndroidManifest.xml` (The Identity Card)**: 
  * *What it is*: A configuration file for the Android system.
  * *Layman explanation*: It tells the Android operating system, *"Hello, my name is MindfulTasks, my icon looks like this, and this is how you launch me."*
* **`.gitignore` (The Trash Filter)**: 
  * *What it is*: A filter file for Git version control.
  * *Layman explanation*: When saving your work to GitHub, you don't need to upload temporary files that your computer generates. This file tells Git, *"Only save my actual code files, and ignore the temporary computer trash."*
* **`build.gradle.kts` & `settings.gradle.kts` & `libs.versions.toml` (The Supply Lists)**: 
  * *What it is*: Lists of dependencies and plugins.
  * *Layman explanation*: Like a recipe that lists the ingredients you need from the grocery store, these files tell Android Studio exactly which Google libraries and toolkits to download to compile and build your app.

#### 4. The Guides (Helpful Manuals)
* **`README.md` (The Instruction Manual)**: 
  * *What it is*: An easy-to-read markdown guide.
  * *Layman explanation*: A manual designed just for you! It explains step-by-step how to download Android Studio, run the app on a virtual phone, make a shareable installation file (APK), and upload your code to GitHub.
* **`CHANGELOG.md` (This Diary)**: 
  * *What it is*: A timeline diary.
  * *Layman explanation*: The exact file you are reading right now, tracking every step of our journey.

---

### 🔧 Update: Fixed Gradle Sync Failure (May 26, 2026)
* **What we modified**: Simplified the **`settings.gradle.kts`** file.
* **Why we did it**: The original file had a strict filter list that blocked Google from downloading some essential tools. We removed the filter, giving Google full access to download what it needs. Now, Android Studio will be able to synchronize and compile the app correctly!

---

### 🔧 Update: Upgraded Gradle Wrapper Version (May 26, 2026)
* **What we modified**: Created the **`gradle-wrapper.properties`** configuration file to force Gradle to version `8.7`.
* **Why we did it**: The Android compile tools we are using require Gradle version `8.6` or higher, but your system was defaults to version `8.5`. By adding this file, we instructed Android Studio to automatically download and use version `8.7`, which solves the version mismatch crash.

---

### 🔧 Update: Fixed Syntax Typos in Build File (May 26, 2026)
* **What we modified**: Replaced dashes (`-`) with dots (`.`) in the **`app/build.gradle.kts`** file for three library names (like changing `tooling-preview` to `tooling.preview`).
* **Why we did it**: Gradle's Kotlin coding language treats dashes as minus signs (subtraction). It was trying to mathematically subtract `preview` from `tooling`, causing a build error! Changing them to dots fixes the grammar so Android Studio understands the library references correctly.

---

### 🔧 Update: Fixed Text Field Color Crash in TaskScreen.kt (May 26, 2026)
* **What we modified**: Replaced the invalid parameter `containerColor` with `focusedContainerColor` and `unfocusedContainerColor` in the Zen Journal text box settings.
* **Why we did it**: In this version of Android's styling system, there is no generic "background container color" setting for text input boxes. Instead, Android requires us to specify the background color for both when the box is selected (focused) and when it is unselected (unfocused). Fixing this parameter name stops the compile crash.


---

## 📅 May 26, 2026: Version 2.0 Release - User Auth, REST API Server, & Zen AI Chat 🚀

Today, we rolled out a major architecture upgrade, transforming our standalone app into a fully connected, database-backed platform with built-in AI help.

Here is a summary of what we added and how it works:

### 🏠 App Features (What You See)
1. **User Sign In Screen**: A new secure entry screen. You can register a unique username and password, log in, and log out with a click.
2. **"Zen AI" Chat Tab**: A third tab on your main screen. You can pick between **Gemini (Google)** and **ChatGPT (OpenAI)** and type questions. A list of chat bubbles displays the conversation.
3. **Automatic Login**: The app now remembers who you are so you don't have to sign in every time you open it.

---

### 📁 The Files We Created & Modified

#### 1. The Local Server (The Backend Microservice)
* **`backend-service/package.json` (Server Dependencies)**: List of packages (like Express) required to run the local server.
* **`backend-service/server.js` (The Server Router)**: The "Chef" that runs our local database. It handles registration, logs in users, reads/writes tasks, and delegates AI chat messages.
* **`backend-service/.env.example` (API Key Template)**: A template showing where you can paste your Gemini or OpenAI API keys to connect to real servers.

#### 2. The Android App Upgrades (The Client)
* **`NetworkClient.kt` (The Postal Worker)**: A brand new file that handles all internet requests. It packages data into JSON and ships it over to our local server.
* **`AuthScreen.kt` (The Login Gate)**: The screen with username and password input boxes.
* **`MainActivity.kt`**: Changed the logic to route the user: if no one is logged in, show the login gate; otherwise, show the dashboard.
* **`TaskViewModel.kt`**: Linked all task checklist changes and journal saves to send sync updates to the server. Added chat history logs and AI network routers.
* **`TaskScreen.kt`**: Added the "Zen AI" layout with chat logs, model selection chips, and a logout button.


---

### 🔧 Update: Fixed Coroutine Launch Crash in AuthScreen.kt (May 26, 2026)
* **What we modified**: Imported `kotlinx.coroutines.launch` and changed the syntax from a package-scoped call to `scope.launch` inside the sign-in form.
* **Why we did it**: In Kotlin, `launch` is an extension function that must be executed directly on a `CoroutineScope` object (like our Compose `scope`). Scoping it correctly resolves the compile error and allows background network checks to run smoothly.

---

### 🏁 What to Do Next:
Your files are safe and ready in your workspace! To save them on your Mac's Git system:
1. Open the **Terminal** app on your Mac.
2. Type `cd /Users/developer/Documents/AG2.0` and press Enter.
3. Type `git add .` and press Enter.
4. Type `git commit -m "Version 2.0: Auth, Node REST API, and Zen AI integration"` and press Enter.
5. Push origin in GitHub Desktop!

---

## 📅 May 27, 2026: Phase 2 - Dynamic LLM Workspaces & Separate Conversation Flows 🎨🤖

Today, we upgraded the **Zen AI** tab so that selecting a different AI engine (Gemini or ChatGPT) changes the entire screen layout, giving each model its own independent conversation history and custom quick-start prompt suggestions.

### 🏠 App Features (What You See)
1. **Isolated Conversations**: Toggling between Gemini and ChatGPT now switches you to completely separate conversation histories! The questions and answers you swap with ChatGPT are saved separately from your conversations with Gemini, so they do not get mixed up.
2. **Dynamic UI Styling**:
   * **✨ Gemini Creative Workspace**: If Gemini is selected, the chat log is styled with deep, cosmic purple-indigo gradients and rounded glassmorphic bubbles. The text input field is pill-shaped with a glowing purple outline, and you have access to creative prompts like *"Mindful haiku"* and *"Guided visualization"*.
   * **● ChatGPT Analytical Suite**: If ChatGPT is selected, the interface immediately switches to a tech-minimalist gray theme with clean, boxy, pine-green bubbles. The text input field has sharp corners, and you have access to focus-planning prompts like *"Optimize my tasks"* and *"Plan a focus routine"*.
3. **Clickable Quick Prompts**: Quick prompt buttons sit above the text input bar. Tapping any prompt will automatically load it and send it to the active AI engine for immediate response.

### 📁 Files Modified & Why
* **`TaskViewModel.kt` (The Manager Brain)**: 
  * *What we changed*: Split the single chat message list into two separate lists (`geminiMessages` and `chatGptMessages`).
  * *Why we changed it*: To ensure that switching models displays different chat logs and isolates the conversation flow.
* **`TaskScreen.kt` (The Interactive Pages)**:
  * *What we changed*: Rewrote the entire "Zen AI" tab layout code.
  * *Why we changed it*: To dynamically change colors, headers, bubble styles, and input shapes depending on whether Gemini or ChatGPT is selected, and to render the interactive quick prompt buttons.
