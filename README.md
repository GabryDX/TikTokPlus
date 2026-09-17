# TikTok Plus 📱

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)

An Open-Source, privacy-focused WebApp wrapper for accessing TikTok.

## 📖 About
The main reason this project exists is due to the trust and privacy issues associated with the official TikTok app. By acting as a sandboxed, browser-like wrapper, TikTok Plus mitigates many of the native app's privacy concerns while still allowing you to enjoy the content. 

The goal is to improve the general security and privacy of accessing TikTok as much as possible, giving control back to the user.

## ✨ Features
* **Privacy-First:** Operates as a web wrapper, significantly restricting access to your device's native data (contacts, clipboard, hardware identifiers) that the official app typically collects.
* **Lightweight:** Tiny app size and low resource consumption compared to the official app.
* **Native Feel:** Includes "Swipe to Refresh" functionality to seamlessly refresh your feed.
* **Modern UI:** Features a modern Android splash screen.
* **Open Source:** Fully transparent, allowing anyone to verify the code and contribute.

## 🚀 Installation
You can download the latest APK from the **[Releases](../../releases)** page (if available) or build it from source.

1. Download the `.apk` file.
2. Enable "Install from Unknown Sources" in your device settings.
3. Install and enjoy a more private TikTok experience!

### 🔐 App Verification
To ensure the authenticity of the APK and verify that it has not been tampered with, you can check its signing certificate using [AppVerifier](https://github.com/soupslurpr/AppVerifier):

* **Package Name:** `com.heronikostudios.tiktokplus`
* **Signing Certificate SHA-256:**
  ```text
  36:D6:9B:D7:8C:8A:44:90:C2:BC:3F:53:29:6A:BD:68:88:7E:2A:50:AD:9B:9D:A1:C3:6C:CC:D6:4E:96:AF:01
  ```

## 🛠️ Building from Source
To build the project yourself, you'll need [Android Studio](https://developer.android.com/studio).

1. Clone the repository:
   ```bash
   git clone https://github.com/Gabrydx/TikTokPlus.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle.
4. Run the app on an emulator or physical device.

## 🤝 Contributing
This is an open project, and contributions are very welcome! If you have ideas for improving security, adding features (like ad-blocking or video downloading), or fixing bugs:

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## ⚠️ Disclaimer
I do not own any rights over the TikTok app, brand, trademark, or content. This is strictly a custom browser wrapper built for educational and privacy purposes. Use at your own discretion.

## 📄 License
This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
