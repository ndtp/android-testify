# Publishing

Testify is published to Bintray and jcenter under the `shopify-android` organization. https://bintray.com/shopify/shopify-android

- Update the `testify` version in [build.gradle](https://github.com/Shopify/android-testify/blob/master/build.gradle#L33)
- Define the environment variable `BINTRAY_USER`. Set this to a valid user name from the Shopify Bintray organization.
Thus value can be found at https://bintray.com/profile/edit
- Define the environment variable `BINTRAY_KEY`. This can be found in a Bintray user profile's left menu, under "API Key".
- Upload the library to Bintray using the following gradle command:
`./gradlew Library:clean Library:assemble Library:bintrayUpload`
- Upload the Gradle plugin to Bintray using the following gradle command:
`./gradlew Plugin:clean Plugin:assemble Plugin:bintrayUpload`
