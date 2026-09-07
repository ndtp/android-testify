# Configure your emulator to run Testify tests

The Sample application includes a baseline for an emulator that's compatible with GitHub Actions. To configure an AVD locally, create a new virtual device with the following settings in the Android Virtual Device (AVD) configuration:

## Intel based hardware

- Phone: Pixel 3a (Obsolete) (1080x2220 440dpi)
- 16 KB Page Size Google APIs **x86_64** v8a System Image, API 37.0 (CinnamonBun; Android 17.0)
- CPU cores: 4
- Graphics acceleration: Automatic
- RAM: 2 GB
- VM heap: 256 MB
- Internal Storage: 10 GB
- Expanded storage, Custom: 512 MB
- Enable Device Frame with pixel_3a skin
- Enable keyboard input

## Apple M1 hardware

- Phone: Pixel 3a (Obsolete) (1080x2220 440dpi)
- 16 KB Page Size Google APIs **ARM 64** v8a System Image, API 37.0 (CinnamonBun; Android 17.0)
- CPU cores: 4
- Graphics acceleration: Automatic
- RAM: 2 GB
- VM heap: 256 MB
- Internal Storage: 10 GB
- Expanded storage, Custom: 512 MB
- Enable Device Frame with pixel_3a skin
- Enable keyboard input

Once the emulator is booted:
- Set the Language to English (United States) (`en_US`)
- In the developer settings, set Window animation scale, Transition animation scale, and Animator duration scale to `off`
