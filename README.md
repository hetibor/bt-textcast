# BT Textcast

A small Android app for advertising text typed on the phone as now-playing metadata to a connected Bluetooth device, such as a car stereo.

## First version

- Edit a single title field.
- The on-screen preview follows the field immediately; there is no send/confirm button.
- Turn **Casting** on to publish the current title through an Android media session. Changes are published as you type.
- Turn **Casting** off to stop the session and clear its metadata.

## Build and run

Open this repository in Android Studio and let it sync with Gradle, then run the `app` configuration on an Android device. The project uses Kotlin, Android Gradle Plugin 8.7.3, Java 17, and Android SDK 35 (minimum Android 6.0 / API 23).

## Bluetooth/display behavior

This app exposes the title through Android's media-session metadata; it does not directly write an MP3 tag or send arbitrary Bluetooth packets. A connected device must support AVRCP/media metadata, and the device, Android version, and Bluetooth connection determine whether and how the title appears. Displays may truncate or ignore text. The prototype advertises a playing media session without producing audio, so some devices may not show it consistently.
