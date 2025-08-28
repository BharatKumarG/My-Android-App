
# MyAndroidAppEnhanced

This is an enhanced ToDo app (sample) with AI-like features: quick natural language add, smart suggestions, WorkManager-based reminders (stub), export/import, and a polished Compose UI.
Import into Android Studio and run. CI workflow `.github/workflows/android-ci.yml` builds a debug APK on push to `main`.

Notes:
- gradle-wrapper.jar placeholder included; if you want a proper wrapper, run `gradle wrapper` locally to regenerate the jar.
- The NLP and AI features are local heuristics; integrate an LLM or ML model for richer AI.
