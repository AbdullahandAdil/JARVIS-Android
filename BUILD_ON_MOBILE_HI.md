# मोबाइल से Cloud Build (Laptop के बिना)

यह प्रोजेक्ट GitHub Actions के जरिए APK बना सकता है। अभी APK इस ZIP में शामिल नहीं है।

1. GitHub पर नया खाली repository बनाएं (Public या Private)।
2. इस ZIP को Extract करें और सभी फाइलें repository में upload करें। मोबाइल Chrome में GitHub का Desktop site उपयोग करना पड़ सकता है।
3. `main` branch में commit करें।
4. Repository के Actions टैब में `Build JARVIS Android APK` workflow खोलें।
5. `Run workflow` दबाएं (या push के बाद workflow अपने आप चलेगा)।
6. सफल run के Artifacts में `JARVIS-debug-apk` डाउनलोड करें।
7. ZIP artifact को Extract करके `app-debug.apk` फोन में install करें।

ध्यान दें:
- GitHub account और repository की जरूरत है; इसे तुम्हारे खाते में मैं स्वयं upload नहीं कर सकता जब तक account connection/permission उपलब्ध न हो।
- यह Stage 1 starter app है, पूर्ण NetHyTech JARVIS port नहीं।
- Cloud AI, memory, web search, camera vision, WhatsApp automation और background wake word अभी नहीं जुड़े हैं।
- APK build को GitHub Actions पर verify करना बाकी है।
