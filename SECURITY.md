# Security & Privacy Policy

The security of **AlexQr** and the privacy of our users are foundational principles of this project.

---

## 🛡️ Supported Versions

We provide security updates and patches for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

---

## 🔒 Privacy & Zero-Network Guarantee

AlexQr is engineered from the ground up to be **strictly 100% offline**:

1. **No Internet Permission**:
   Reviewing `app/src/main/AndroidManifest.xml` confirms that `android.permission.INTERNET` is not requested or declared. The Android operating system enforces that this app cannot make outbound network connections or receive inbound socket traffic.
2. **Local Storage Only**:
   All scan records, generated barcodes, user preferences, and theme choices are stored exclusively on device inside the app's sandboxed private data directory, using an Android Room SQLite database and private `SharedPreferences`. No data is transmitted off the device.
3. **No Third-Party Trackers or Analytics**:
   The app contains zero telemetry SDKs, zero crash-reporting services that send data off-device, and zero advertising libraries.
4. **Camera Safety**:
   Camera hardware permissions are only used while the viewfinder is visibly active on screen. Images and preview frames are processed in-memory using on-device Google ML Kit Vision algorithms and are never written to unencrypted storage or transmitted anywhere.

---

## 🚨 Reporting a Vulnerability

If you discover a security vulnerability or potential privacy loophole within AlexQr, please do not disclose it via a public GitHub issue.

Instead, please send a responsible disclosure email directly to the developer:
- **Email**: [alexismupole@gmail.com](mailto:alexismupole@gmail.com)
- **Subject**: `[SECURITY VULNERABILITY] AlexQr - Brief Description`

### Please Include:
- A description of the vulnerability.
- Steps to reproduce or proof-of-concept code.
- Impact assessment (e.g., potential unauthorized local file access or denial of service).
- Your name / handle if you wish to be credited in our release notes.

We will acknowledge receipt within 48 hours and work with you on a timeline for a verified patch release.
