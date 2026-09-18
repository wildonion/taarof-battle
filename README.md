# Taarof Battle - src/taarof

Beautiful, friendly, professional 2-player tap battle game in Kotlin + Jetpack Compose Material3.

Package: `com.wildonion.taarofbattle`
MinSdk 24, Target 35, Compose BOM 2024.10.00

## Structure
```
src/taarof/
  settings.gradle.kts
  build.gradle.kts
  gradle.properties
  local.properties (points to your Android SDK)
  gradlew.bat
  app/
    build.gradle.kts
    proguard-rules.pro
    src/main/
      AndroidManifest.xml
      java/com/wildonion/taarofbattle/
        MainActivity.kt
        game/GameViewModel.kt (countdown, ticker, bot, powerups, score)
        ui/theme/Theme.kt (cream + deep teal + warm orange, friendly pro look)
        ui/screens/MenuScreen.kt (names, 2P vs Bot, 10/15/20s)
        ui/screens/BattleScreen.kt (split tap zones, tug-of-war bar, powerups)
        ui/screens/ResultScreen.kt (winner, bill, funny line, share)
        util/ShareUtil.kt (share to Telegram / Instagram)
```

## How to build APK (2 min)

Option A - Android Studio (recommended):
1. Open Android Studio -> Open -> select `src/taarof`
2. Let Gradle sync (needs internet first time)
3. Run > Run 'app' for emulator, or Build > Build App Bundle/APK > Build APK
4. APK output: `app/build/outputs/apk/debug/app-debug.apk`

Option B - command line:
```
cd src/taarof
gradle assembleDebug
# APK at app/build/outputs/apk/debug/app-debug.apk
```

## Game design
- Menu: enter 2 names, pick 2-players (same phone multi-touch) or vs Bot, pick duration.
- Battle: top = Player2 (teal), bottom = Player1 (orange). Spam-tap. Powerups: Ghasam x2 (2s), Waiter freeze opponent (1.2s), No Wallet +5.
- Result: winner crown, loser pays random funny bill (e.g. 2,400,000 Toman), share button adds viral loop for Bazaar.

## Next for Bazaar release
- Add app icon + 5 Persian screenshots + 30s video
- Add Tapsell interstitial every 2 battles
- Add Bazaar IAB: remove ads / unlock phrases
- Add Persian strings.xml (`values-fa`) for ASO
