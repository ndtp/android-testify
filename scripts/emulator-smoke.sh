#!/usr/bin/env bash
#
# Emulator smoke test. Deliberately dependency-free: no Gradle, no test
# framework, nothing from this repo. It answers one question -- is this
# emulator configuration usable at all? -- so that a failure here can only
# mean the emulator, never our tooling.
#
# Runs inside reactivecircus/android-emulator-runner, which has already
# booted the device by the time this starts.

set -uo pipefail

fail=0

section() { printf '\n=== %s ===\n' "$*"; }
check() {
  if [ "$1" -eq 0 ]; then
    echo "PASS: $2"
  else
    echo "FAIL: $2"
    fail=1
  fi
}

section "Device"
adb devices
for prop in ro.build.version.sdk ro.build.version.release \
            ro.product.cpu.abi ro.build.fingerprint; do
  printf '%-28s %s\n' "$prop" "$(adb shell getprop "$prop" | tr -d '\r')"
done

section "Core services"
for svc in package activity window; do
  adb shell service check "$svc" | grep -q ": found"
  check $? "service $svc is registered"
done

section "SurfaceFlinger stability"
# The compositor restarting underneath us is what breaks everything
# downstream: when it dies, init restarts the runtime with it, and any
# binder call in flight fails with "Broken pipe".
before=$(adb shell pidof surfaceflinger | tr -d '\r')
echo "surfaceflinger pid: ${before:-<none>}"
echo "observing for 60s ..."
sleep 60
after=$(adb shell pidof surfaceflinger | tr -d '\r')
echo "surfaceflinger pid: ${after:-<none>}"
[ -n "$before" ] && [ "$before" = "$after" ]
check $? "surfaceflinger held the same pid for 60s"

section "Crash buffer"
crashes=$(adb logcat -b crash -d)
if [ -z "$crashes" ]; then
  echo "PASS: crash buffer is empty"
else
  echo "FAIL: crash buffer is not empty"
  fail=1
  echo "$crashes" | head -60
fi

section "Screencap"
# Goes through the same host->guest colour buffer readback that screenshot
# testing depends on.
adb exec-out screencap -p > screen-idle.png
[ -s screen-idle.png ]
check $? "screencap wrote $(wc -c < screen-idle.png) bytes"

section "Install"
adb install -r ./SmokeApp/app/build/outputs/apk/debug/app-debug.apk
check $? "adb install"
adb shell pm list packages | grep -q dev.testify.smoke
check $? "dev.testify.smoke is installed"

section "Launch"
adb shell am start -W -n dev.testify.smoke/.MainActivity
check $? "MainActivity started"
sleep 3
adb exec-out screencap -p > screen-app.png
[ -s screen-app.png ]
check $? "screencap after launch wrote $(wc -c < screen-app.png) bytes"

section "Result"
if [ "$fail" -eq 0 ]; then
  echo "ALL CHECKS PASSED"
else
  echo "ONE OR MORE CHECKS FAILED"
fi
exit "$fail"
