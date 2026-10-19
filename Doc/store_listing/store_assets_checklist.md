# Google Play Store Assets Checklist & Specifications

## 1. App Icon
- **Format:** 32-bit PNG (with alpha)
- **Dimensions:** 512 px by 512 px
- **Max file size:** 1024 KB
- **Design:** LexiLadder clean white ladder vector on deep blue `#1976D2` rounded square background.
- **Source asset:** `app/src/main/res/drawable/ic_launcher_foreground.xml` + `ic_launcher_background.xml`

---

## 2. Feature Graphic
- **Format:** JPEG or 24-bit PNG (no alpha)
- **Dimensions:** 1024 px by 500 px
- **Max file size:** 15 MB
- **Text & Tagline:**
  - Title: *LexiLadder*
  - Subtitle: *English Vocabulary, One Level at a Time*
  - Badge: *100% Offline | SM-2 Spaced Repetition*

---

## 3. Phone Screenshots (Minimum 4, Recommended 6)
- **Aspect Ratio:** 16:9 or 9:16 (Standard phone portrait: 1080 x 2340 or 1080 x 1920)
- **Format:** JPEG or 24-bit PNG
- **Specifications & Captions:**

| No | Screen View | Marketing Title / Caption | Target File |
|---|---|---|---|
| 1 | **Home Screen** | *Daily 5-Minute Vocabulary Routine* | `app/build/outputs/screen_p6.png` |
| 2 | **Flashcard / Learn** | *Rich Definitions, IPA & Contextual Sentences* | `screen_learn.png` |
| 3 | **Adaptive Practice** | *Smart Spaced Repetition (SM-2) for Long-Term Memory* | `screen_practice.png` |
| 4 | **Mastery Test** | *Pass Level Tests to Unlock New Vocabulary Stages* | `screen_mastery_test.png` |
| 5 | **Progress & Review** | *Track Mastered Words, Retention & Due Reviews* | `screen_progress.png` |
| 6 | **Settings & Offline** | *100% Offline Access & Multiple Translation Support* | `screen_settings.png` |

---

## 4. Google Play Console Declarations
- **Application Category:** Education
- **Content Rating:** PEGI 3 / Everyone (No violent, sexual, or sensitive content)
- **Target Audience:** Everyone (Age 13+)
- **Ads Declaration:** "Yes, my app contains ads" (Google AdMob integrated, non-intrusive banners and post-session interstitials only)
- **Data Safety:**
  - Data collected: None collected to remote servers. All learning progress stored locally on device.
  - Third-party SDKs: Google Mobile Ads SDK (AdMob), Google Play Billing.
  - Data encrypted in transit: Yes (HTTPS used for optional content updates).
  - Account creation required: No.
