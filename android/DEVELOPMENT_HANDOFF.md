# Android development handoff

## Current scope
Native Android 10+ test application using the Android CallScreeningService role. Exact manually blocked numbers are rejected; trusted numbers override the blocklist; 10 PM–7 AM quiet hours silence untrusted calls without rejecting them. Numbers and preferences remain in on-device SharedPreferences.

## Build verification (required)
1. Open GitHub Actions > Android debug APK; confirm the workflow completes successfully.
2. Download the `spam-spotter-debug-apk` artifact; install on an Android test device.
3. Open the app and grant the call-screening role in the system dialog.
4. Place a call from an explicitly blocked test number; verify rejection.
5. Place a call from a trusted number that is also on the blocklist; verify it rings.
6. Place a call from an unlisted number during quiet hours; verify silence rather than rejection.
7. Disable quiet hours and repeat; verify ordinary ringing.
8. Remove a blocked number and repeat; verify it rings.
9. Test incoming caller-ID withheld calls, airplane-mode recovery, and restarting the phone.
10. Test accessibility, large font, and landscape on real hardware.

## Known limits
- No spam intelligence, automatic business classification, SMS interception, or cloud sync.
- Exact-number matching only. International formatting and country-code variants require more work.
- No repeat-caller bypass yet.
- Call screening availability and behavior vary by Android device and carrier.
- Quiet hours use the phone's local time; the current fixed interval is 10 PM–7 AM.
- This is not a security-certified or Play Store release build.
- No successful compilation, device testing, or APK download is asserted by this document.

## Release gate
Do not publish until CI builds, device tests, privacy review, support contact, signed release artifact, and Play policy checks are complete.
