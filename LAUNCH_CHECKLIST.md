# Spam Spotter launch checklist

Do not announce or advertise the app until the items marked as pending are completed.

## Current build

- Static web app, no backend or live threat-intelligence feed.
- Text and URL checks run in the browser; scam-report notes and text-size preference remain in local browser storage.
- The app does not block calls or messages and does not submit reports to carriers.
- A low-warning result does not mean a link or message is safe.

## Required before public announcement

- [ ] Select a hosting provider and final domain.
- [ ] Publish over HTTPS and test the chosen URL on Android and iPhone.
- [ ] Confirm the provider's access logs, privacy policy, and retention terms; update the on-page privacy notice if needed.
- [ ] Add a support contact address or form.
- [ ] Run the manual checks below on a real phone at standard, large, and extra-large text.
- [ ] Confirm the app installs and opens offline after first load; confirm a new release replaces the offline cache.
- [ ] Confirm a user can clear local scam reports and understand that browser site data removal also clears them.

## Manual checks

- [ ] Check a blank message and a harmless ordinary message.
- [ ] Check a test message containing urgent payment language and a fake link; confirm the result names the detected warning signs.
- [ ] Check a normal HTTPS address and a look-alike address such as `https://amazon.com.example.org`; confirm the app never opens the link.
- [ ] Save a report, reload the page, verify the count remains, then clear it and reload again.
- [ ] Change each text-size option, reload the page, and verify the setting persists.
- [ ] Use keyboard navigation to reach every control and verify visible focus.
- [ ] At extra-large text, verify content reflows without horizontal page scrolling or clipped controls.
- [ ] Test with Android TalkBack or iOS VoiceOver and confirm each control/result is announced.

## Not part of this release

No live database, automatic blocking, carrier reporting, user accounts, analytics, or server-side report collection. Those features require separate product, privacy, and security work.
