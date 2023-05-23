import OpenNew from '@site/static/img/open_new.svg';

# Multi-user support


Testify provides support for emulators running in multi-user configuration.

## Background

[This page <OpenNew />](https://source.android.com/docs/devices/admin/multi-user-testing) describes important aspects of testing multiple users on the Android platform. For information about implementing multi-user support, see [Supporting Multiple Users <OpenNew />](https://source.android.com/docs/devices/admin/multi-user).


## Automatic user selection

Testify automatically reads and writes files to the currently running user. The Testify plugin will correctly pull screenshots from the current user.


### Configuring the Gradle Plugin

You may optionally configure the Gradle Plugin to pull screenshots from a different user. You can override the user by specifying the `user=<number>` argument.

```
./gradlew app:screenshotPull -Puser=10

```


