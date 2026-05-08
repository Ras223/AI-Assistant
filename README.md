# Nexus AI Assistant (Android)

Nexus is a futuristic, AI-powered personal assistant application for Android. It leverages NVIDIA's advanced NIM APIs to provide intelligent text conversations and generate stunning images right from your chat interface.

## 🚀 Features

- **Conversational AI**: Chat naturally with Nexus, powered by NVIDIA's text generation models.
- **Image Generation**: Use the `/draw` command to instantly generate images based on your prompts (e.g., `/draw futuristic city`).
- **Session Management**: Automatically organizes your conversations into manageable chat sessions.
- **Modern UI**: Clean and intuitive chat interface with markdown support for structured AI responses.

## 🛠️ Technology Stack

- **Language**: Kotlin
- **Platform**: Android SDK
- **Architecture**: Coroutines for asynchronous network requests
- **UI Components**: Material Design (DrawerLayout, MaterialToolbar, RecyclerView)
- **AI Integration**: NVIDIA NIM APIs (Text & Image models)

## 📋 Prerequisites

To run this project, you will need:
- Android Studio (Electric Eel or newer recommended)
- Minimum SDK Version: 24 (or as specified in your `build.gradle`)
- An active **NVIDIA API Key**

## ⚙️ Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/nexus-ai-android.git
   ```

2. **Open the project:**
   Open Android Studio, select "Open", and navigate to the cloned project directory.

3. **Configure your API Key:**
   Open `MainActivity.kt` and replace the placeholder API key with your actual NVIDIA API key:
   ```kotlin
   private val apiKey = "YOUR API KEY NVIDIA"
   ```
   *(Note: For production apps, it is recommended to store API keys securely in `local.properties` or environment variables instead of hardcoding them.)*

4. **Run the App:**
   Connect your Android device or start an emulator, and click the **Run** button in Android Studio.

## 💡 How to Use

- **Chat**: Simply type your message in the input field and hit send. Nexus will reply based on the context of the conversation.
- **Create Image**: Tap the "Draw" pill or type `/draw <your prompt>` in the chat. Nexus will process your request and return a generated image directly in the chat window.
- **New Session**: Tap the "New Chat" button in the side drawer to start a fresh conversation.

## 📸 Screenshots
*(Add screenshots of your application here)*

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
