# Spam Spotter

Spam Spotter is a phone-friendly, installable web app prototype for checking suspicious messages and website addresses.

## Run it

Serve this folder from a local web server or static host. A service worker requires HTTPS (or localhost) to work. Open `index.html` in a browser for a basic check; the install and offline features need a server.

## Included in this test build

- Local rule-based message checks for common urgency, credential, payment, prize, and delivery lures.
- URL structure checks that do not open the URL.
- A local scam-report form. Reports stay in this browser's local storage and are not uploaded.
- Responsive layout, web app manifest, and offline cache.

## Limits

This is an early test prototype, not a live spam database or a verified security product. It does not contact threat-intelligence services, report messages to carriers, or guarantee that a message is safe. The rule list is small and will miss many scams and can flag legitimate messages. Do not enter passwords, one-time codes, financial details, or other private information. No API keys are needed for this local prototype.
