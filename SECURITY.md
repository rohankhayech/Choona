# ChromaticTuner Security Policy and Information

## Supported Versions
Only the latest version is supported for bug fixes and security patches.
Please keep the app updated to the latest version.

## Verification
If downloading the app from other sources than [Google Play](https://play.google.com/store/apps/details?id=com.rohankhayech.ChromaticTuner) or [this repository](https://github.com/ArmaRizki/ChromaticTuner/releases/latest), it is recommended to verify the APK before installation.
This ensures the downloaded app is an official copy of ChromaticTuner.

To verify the downloaded app before installing it, you can use the [`apksigner`](https://developer.android.com/tools/apksigner#examples-verify) tool in the Android SDK Build Tools.

Use the following command to check the SHA-256 signing certificate fingerprint of the downloaded APK:

```bash
apksigner verify --print-certs [path to ChromaticTuner APK]
```

The SHA-256 signing certificate fingerprint should match the following:
```

```
This is listed as `Signer #1 certificate SHA-256 digest` in the command's output.

## Reporting a Vulnerability
You can confidentially report security vulnerabilities [here](https://github.com/ArmaRizki/ChromaticTuner/security/advisories/new).
