# JARVIS Android — Stage 1

यह शुरुआती Android-native foundation है, final AI assistant नहीं।

## अभी क्या काम करता है
- Android app का शुरुआती UI
- Microphone permission
- Android SpeechRecognizer से Hindi speech input
- Android TextToSpeech से Hindi में जवाब
- कुछ basic local replies (समय, अभिवादन, परिचय)
- App foreground में होने पर volume-key press detection का शुरुआती प्रयास

## अभी क्या नहीं है
- NetHyTech desktop Python modules का पूर्ण Android port
- Cloud AI / ChatGPT-जैसा Brain
- Background में हमेशा सुनने वाला “Hey JARVIS”
- Background volume-button shortcut (Android restrictions के कारण)
- WhatsApp automation, camera vision, image generation, alarms, web search

## Build
Android Studio में इस folder को खोलें और Gradle Sync करें। Android SDK 35 और JDK 17 आवश्यक हैं।
फिर Build > Build APK(s) चुनें।

## सुरक्षा
API keys को APK में hardcode न करें। AI API जोड़ने पर सुरक्षित backend/proxy या user-provided key flow की जरूरत होगी।


## मोबाइल से APK बनाना
GitHub Actions workflow `.github/workflows/android-build.yml` شامل है। मोबाइल से Cloud Build के निर्देश `BUILD_ON_MOBILE_HI.md` में हैं।
