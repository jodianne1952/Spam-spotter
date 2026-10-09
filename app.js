const $ = (id) => document.getElementById(id);
const patterns = [
  { re: /\b(act now|immediately|urgent|within 24 hours|final notice|account (?:will be|has been) (?:closed|suspended|locked))\b/i, points: 2, why: "Pressures you to act quickly." },
  { re: /\b(verify|confirm|unlock|restore|validate)\b.{0,45}\b(account|identity|password|payment|card|bank|ssn|social security)\b/i, points: 3, why: "Asks you to verify sensitive information." },
  { re: /\b(password|one[- ]time code|verification code|pin|social security number|ssn|full card number)\b/i, points: 3, why: "Mentions sensitive credentials or identity information." },
  { re: /\b(gift card|crypto(?:currency)?|bitcoin|wire transfer|cash app|zelle|western union)\b/i, points: 2, why: "Requests a payment method commonly used in scams." },
  { re: /\b(you (?:have )?won|claim your prize|guaranteed (?:prize|return)|selected as a winner|free (?:gift|iphone|prize))\b/i, points: 2, why: "Promises a prize or unusually good offer." },
  { re: /\b(click|tap|open)\b.{0,35}\b(link|here|below|url|button)\b/i, points: 1, why: "Encourages you to follow a link." },
  { re: /\b(package (?:is )?(?:held|on hold|delayed)|delivery (?:failed|attempt)|unpaid (?:toll|fee|postage))\b/i, points: 2, why: "Uses a common delivery or fee lure." },
  { re: /\b(irs|social security administration|police|sheriff|court)\b.{0,60}\b(arrest|warrant|fine|pay now|immediate payment)\b/i, points: 3, why: "Combines an authority claim with a demand or threat." },
  { re: /\b(secret|don't tell|do not tell|keep this between us)\b/i, points: 2, why: "Uses secrecy to isolate or pressure you." },
  { re: /(?:https?:\/\/|www\.)\S+/i, points: 1, why: "Contains a web link. Inspect links carefully before opening." },
  { re: /\b(call|text|reply)\s+(?:this|the following)\s+number\b/i, points: 1, why: "Directs you to an unverified contact number." },
];
const knownShorteners = new Set(["bit.ly", "tinyurl.com", "t.co", "goo.gl", "ow.ly", "is.gd", "cutt.ly", "rb.gy"]);
const knownBrands = ["paypal.com", "amazon.com", "apple.com", "microsoft.com", "google.com", "irs.gov", "usps.com", "fedex.com", "ups.com", "chase.com", "bankofamerica.com"];
function urlsIn(text) { return text.match(/(?:https?:\/\/|www\.)[^\s<>"']+/gi) || []; }
function suspiciousUrl(raw) {
  const notes = [];
  let url;
  try { url = new URL(raw.startsWith("www.") ? `http://${raw}` : raw); }
  catch { return ["The address could not be parsed. Check for typos and do not open it until verified."]; }
  const host = url.hostname.toLowerCase().replace(/^www\./, "");
  if (url.protocol !== "https:") notes.push("It does not use HTTPS encryption.");
  if (host.startsWith("xn--") || host.split(".").some(x => x.startsWith("xn--"))) notes.push("The domain uses encoded characters that can disguise look-alike names.");
  if (knownShorteners.has(host)) notes.push("It uses a link-shortening service, which hides the destination.");
  if (/^\d{1,3}(?:\.\d{1,3}){3}$/.test(host)) notes.push("It uses a numeric IP address instead of a recognizable domain.");
  if (host.includes("@") || url.username || url.password) notes.push("It contains unusual sign-in information in the address.");
  if (host.split(".").length > 4) notes.push("It has an unusually deep subdomain structure.");
  if (/(login|verify|secure|account|update|support|claim|payment)/i.test(host) && !knownBrands.includes(host)) notes.push("The domain uses words often added to impersonation links; check the exact registered domain.");
  if (knownBrands.some(brand => host !== brand && host.endsWith(`.${brand}`))) notes.push("A familiar brand appears only as a subdomain; the registered domain may belong to someone else.");
  if (!notes.length) notes.push("No obvious address warning signs were found. This does not prove the site is genuine.");
  return notes;
}
function resultMarkup(level, title, summary, findings, next) {
  return `<span class="risk ${level}">${level.toUpperCase()} RISK</span><h3>${title}</h3><p>${summary}</p>${findings.length ? `<ul>${findings.map(x => `<li>${escapeHtml(x)}</li>`).join("")}</ul>` : ""}<p><strong>Safer next step:</strong> ${escapeHtml(next)}</p>`;
}
function escapeHtml(value) { return String(value).replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c])); }
function analyzeMessage(text) {
  if (!text.trim()) return null;
  const matched = patterns.filter(x => x.re.test(text));
  const linkNotes = urlsIn(text).flatMap(suspiciousUrl);
  const score = matched.reduce((sum, x) => sum + x.points, 0) + (linkNotes.some(x => !x.startsWith("No obvious")) ? 2 : 0);
  const level = score >= 6 ? "high" : score >= 3 ? "medium" : "low";
  return { level, findings: [...new Set([...matched.map(x => x.why), ...linkNotes])], summary: level === "high" ? "Several warning signs are present. Treat this as suspicious." : level === "medium" ? "Some warning signs are present. Verify through a trusted channel." : "Few common warning signs were detected. The message could still be unsafe." };
}
document.querySelectorAll(".tab").forEach(button => button.addEventListener("click", () => {
  document.querySelectorAll(".tab").forEach(tab => { tab.classList.remove("active"); tab.setAttribute("aria-selected", "false"); });
  button.classList.add("active"); button.setAttribute("aria-selected", "true");
  ["message", "link", "report"].forEach(name => { $(`${name}-panel`).hidden = name !== button.dataset.tab; });
}));
$("message-input").addEventListener("input", e => { $("message-count").textContent = `${e.target.value.length.toLocaleString()} / 10,000`; });
$("analyze-message").addEventListener("click", () => {
  const result = analyzeMessage($("message-input").value); const box = $("message-result"); box.hidden = false;
  if (!result) { box.innerHTML = "<p>Paste a message first. Remove personal details before checking.</p>"; return; }
  const title = result.level === "high" ? "This message looks suspicious" : result.level === "medium" ? "Pause and verify" : "Few common warning signs found";
  const next = result.level === "high" ? "Do not click, reply, or pay. Contact the organization using a trusted number or website." : "Do not use contact details in the message. Look up the organization independently.";
  box.innerHTML = resultMarkup(result.level, title, result.summary, result.findings, next);
});
$("analyze-url").addEventListener("click", () => {
  const input = $("url-input").value.trim(); const box = $("url-result"); box.hidden = false;
  if (!input) { box.innerHTML = "<p>Enter a link first. This check will not open it.</p>"; return; }
  const notes = suspiciousUrl(input); const level = notes.some(x => !x.startsWith("No obvious")) ? "medium" : "low";
  box.innerHTML = resultMarkup(level, level === "medium" ? "Review this address carefully" : "No obvious warning signs found", "This check looks at the address only; it does not visit or verify the website.", notes, "Open a trusted app or type the known official address yourself.");
});
function readReports() { try { return JSON.parse(localStorage.getItem("spamSpotterReports") || "[]"); } catch { return []; } }
function updateReportCount() { $("report-total").textContent = `${readReports().length} report${readReports().length === 1 ? "" : "s"} saved on this device.`; }
$("save-report").addEventListener("click", () => {
  const rows = readReports(); rows.push({ kind: $("report-kind").value, note: $("report-note").value.trim(), date: new Date().toISOString() });
  try { localStorage.setItem("spamSpotterReports", JSON.stringify(rows.slice(-100))); $("report-feedback").textContent = "Saved on this device. It has not been sent anywhere."; $("report-note").value = ""; updateReportCount(); }
  catch { $("report-feedback").textContent = "Could not save in this browser. Check device storage settings."; }
});
$("clear-data").addEventListener("click", () => { localStorage.removeItem("spamSpotterReports"); $("report-feedback").textContent = "Reports cleared from this device."; updateReportCount(); });
updateReportCount();
if ("serviceWorker" in navigator && location.protocol.startsWith("http")) navigator.serviceWorker.register("./sw.js").catch(() => {});
