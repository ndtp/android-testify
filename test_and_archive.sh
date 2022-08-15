#!/bin/sh

./github_action_emulator_command.sh Sample:screenshotTest

./gradlew Sample:screenshotPull

mkdir -p ./output/

git status -s --porcelain | grep --color=no "^ M.*png$" | sed s/" M "/""/g | xargs -I {} cp {} ./output/
