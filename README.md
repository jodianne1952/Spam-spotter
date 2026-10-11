# Spam Spotter

Spam Spotter is a phone-friendly, installable web app prototype for reviewing suspicious messages and website addresses. The current build is suitable for a limited beta after hosting and device checks; it is not a verified security product.

## Run and host

Serve the repository root from any static host over HTTPS. GitHub Pages can serve this repository from the `main` branch and root folder. The service worker and install/offline behavior require HTTPS (localhost is allowed for local testing).

## Included

- Local, rule-based message checks and URL structure checks. Entered messages and links are not sent by the app.
- Large and extra-large text controls with a saved, device-local preference.
- Scam report notes saved only in the browser's local storage; reports are not uploaded.
- Responsive layout, web app manifest, and offline cache.

## Important limits

This build does not query a live scam database, block calls or texts, report scams to carriers, or guarantee that a message or site is genuine. It may miss scams or flag legitimate content. A result showing few warning signs is not proof of safety. Users should verify through a trusted channel and should not enter sensitive personal information.

## Before public release

- Choose a hosting provider and domain, enable HTTPS, and verify the deployment URL.
- Complete the mobile and accessibility checks in `LAUNCH_CHECKLIST.md`, including the largest text size.
- Decide on a support contact and publish it with a privacy policy that matches the selected host's logging and retention practices.
- If adding analytics, uploads, accounts, live threat data, or other server features, revise privacy disclosures and security review first.

No API keys or backend service are needed for this static prototype.
