# Choona Security Policy and Information

## Supported Versions
Only the latest version is supported for bug fixes and security patches.
Please keep the app updated to the latest version.

## Verification
If downloading the app from other sources than [Google Play](https://play.google.com/store/apps/details?id=com.rohankhayech.choona) or [this repository](https://github.com/rohankhayech/Choona/releases/latest), it is recommended to verify the APK before installation.
This ensures the downloaded app is an official copy of Choona.

To verify the downloaded app before installing it, you can use the [`apksigner`](https://developer.android.com/tools/apksigner#examples-verify) tool in the Android SDK Build Tools.

Use the following command to check the SHA-256 signing certificate fingerprint of the downloaded APK:

```bash
apksigner verify --print-certs [path to Choona APK]
```

The SHA-256 signing certificate fingerprint should match the following:
```
C8:AA:2E:57:D2:17:7B:0F:72:15:63:EC:DC:C6:80:51:DA:AE:4C:01:77:39:0E:F8:27:94:3C:3D:7E:22:A3:7B
```
This is listed as `Signer #1 certificate SHA-256 digest` in the command's output.

## Reporting a Vulnerability
You can confidentially report security vulnerabilities [here](https://github.com/rohankhayech/Choona/security/advisories/new).
