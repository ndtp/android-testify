---
title: Re-recording the Testify sample baseline images
description: The Testify samples and library tests are moving from an API 29 emulator to API 37.
slug: baseline-updates
authors:
  - name: Daniel Jette
    title: Core Contributor to Android Testify
    url: https://github.com/DanielJette
    image_url: https://github.com/DanielJette.png
tags: [samples, api, emulator]
hide_table_of_contents: false
---

# Re-recording the Testify sample baseline images

Starting with Testify 7.0, the sample and library baseline images are recorded on an API 37 emulator. The API 29 emulator we have used since 2021 is being retired.

<!--truncate-->

---

### Why we stayed on API 29

We settled on API 29 in January 2021. We were running the library and sample tests on API 30 at the time, and the API 30 emulator was unreliable on GitHub Actions runners. API 29 was more stable, so we dropped down a level and stayed there.

We kept it for a different reason than we picked it. The screenshots checked into this repository are regression tests for Testify itself. Holding the emulator constant means that when one of those tests fails, the failure is about a change in the library and not about a change in the platform. Re-recording a baseline is not free either. Every image has to be reviewed by eye, and it is easy to miss a real regression in a diff of several hundred files. A fixed emulator kept that signal clean across the 2.0 through 6.0 releases.

### Why we are updating now

Three things pushed us off API 29.

The gap got too large. API 29 is Android 10. It no longer resembles the platform that anyone is building against, so the samples were demonstrating Testify on rendering behaviour that has since moved on.

An API 29 emulator cannot test 16 KB memory pages. Devices with 16 KB pages are shipping and the current toolchain expects apps to support them. This is worth exercising in the samples, and the new system image does.

The developer experience had fallen behind. Several of the debugging and inspection features in recent Android Studio releases do not work against an API 29 device. Contributors were giving those up for no particular benefit.

### Why API 37

We are going to the newest stable level for the same reason we sat on API 29 for so long. Picking the top of the range buys several years before this comes up again. Android Studio Quail also creates new projects at API 37, so it is the system image a developer setting up Testify today is most likely to already have installed.

### What is changing

All baseline images in the Testify repository have been re-recorded on an API 37 emulator, and CI is moving to API 37 as well.

Testify keys baseline images by device configuration, so the images now live under `37-1080x2220@440dp-en_US` rather than `29-1080x2220@440dp-en_US`. If you run the sample or library tests against the old API 29 emulator, they will fail looking for a baseline directory that no longer exists.

To run the tests, configure a new emulator with the following settings:

- Phone: Pixel 3a (Obsolete) (1080x2220 440dpi)
- 16 KB Page Size Google APIs ARM 64 v8a System Image, API 37.0 (CinnamonBun; Android 17.0)
- CPU cores: 4
- Graphics acceleration: Automatic
- RAM: 2 GB
- VM heap: 256 MB
- Internal Storage: 10 GB
- Expanded storage, Custom: 512 MB
- Enable Device Frame with pixel_3a skin
- Enable keyboard input

Once the emulator is booted, set the language to English (United States) (`en_US`), and set Window animation scale, Transition animation scale, and Animator duration scale to `off` in the developer settings.

### What this means for your project

Nothing changes for your own tests. Testify does not require a particular API level, and `minSdk` is still 26. The baselines described here are the ones checked into the Testify repository for our samples and library tests. Your baselines, and the emulator you record them on, remain your choice.

---
