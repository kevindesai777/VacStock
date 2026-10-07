# VacStock

A simple Android app for keeping track of vaccine stock at a clinic.

- One screen: every vaccine has a − button, its dose count, and a + button
- Add any vaccine from the + in the top bar, so the list fits any country's schedule
- Tap a vaccine's name to rename it or type an exact count when a new box arrives
- Swipe a row left to reveal Delete
- Works offline; the list is stored on the device and included in Android backup

Built with Kotlin, Jetpack Compose and Material 3.

## Build

Requires JDK 17 and the Android SDK (compileSdk 37).

```bash
./gradlew :app:assembleDebug        # debug APK
./gradlew :app:testDebugUnitTest    # unit tests
```

## Release

Create an upload key and a `keystore.properties` file (see `keystore.properties.example`), then:

```bash
./gradlew :app:bundleRelease        # app/build/outputs/bundle/release/app-release.aab
```

## Privacy

VacStock has no internet permission, accounts, ads or analytics. See [PRIVACY.md](PRIVACY.md).

## Store listing

Play Store text and graphics live in `fastlane/metadata/android/en-US/` (the layout fastlane
`supply` and F-Droid read): title, short and full description, release notes per versionCode,
a 512×512 icon, a 1024×500 feature graphic and phone screenshots.
