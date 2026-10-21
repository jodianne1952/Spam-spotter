# Spam Spotter Android (experimental)

Native Android call-screening foundation, separate from the existing web prototype.

## Build
Open the android/ directory in Android Studio (JDK 17), sync Gradle and run the app on an Android 10+ phone. To build from a configured environment: `gradle :app:assembleDebug`.

## Current behavior
- Requests the Android call-screening role through the official system dialog.
- Receives eligible incoming screening callbacks when selected as the screening app.
- **Does not block any calls**. It is monitor-only until rules, consent, and tests are complete.
- Does not read contacts, SMS, or upload phone numbers.
- Android and carrier behavior may vary. Emergency and restricted calls are not suitable test cases.

## Next milestones
Implement opt-in on-device number rules, trusted allowlist, quiet hours, repeat-call handling, privacy-safe local event history, device tests, and a signed test APK. Review Play Store policies before adding SMS access or subscriptions.
