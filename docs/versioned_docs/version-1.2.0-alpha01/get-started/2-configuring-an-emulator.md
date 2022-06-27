# Configure your emulator to run Testify tests

The Sample application includes a baseline for an emulator that's compatible with GitHub Actions. To configure an AVD locally, create a new virtual device with the following settings in the Android Virtual Device (AVD) configuration:

- Phone: Pixel 3a (1080x2220 440dpi)
- Q API level 29, x86, Android 10.0 (Google APIs)
- RAM: 1536 MB
- VM heap: 256 MB
- Internal Storage: 2048 MB
- SD card, Studio-managed: 512 MB
- Enable Device Frame with pixel_3a skin
- Enable keyboard input

Once the emulator is booted:
- Set the Language to English (United States) (`en_US`)
- In the developer settings, set Window animation scale, Transition animation scale, and Animator duration scale to `off`
