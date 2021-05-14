# Publishing

Testify is published to the Maven Central Repository via GitHub Actions.

- Update the `testify` version in [build.gradle](https://github.com/Shopify/android-testify/blob/master/build.gradle#L33) and commit the version bump
- Create a new release on Github. This will invoke the publish Github action and automatically build, sign, and upload to the OSSRH staging repository.
- Promotion from OSSRH to Central Repository requires a manual review for validation before becoming publicly available
