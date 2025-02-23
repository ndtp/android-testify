# Flix

A modern Android application built with Jetpack Compose and Android Clean Architecture.
The information and images displayed are provided by themoviedb.org.

## Setup

1. Visit https://developer.themoviedb.org/reference/intro/getting-started and create an API key.
   There are two types of API keys. This app makes use of the V4 style key that has the format:
   "Bearer keymaterial".
2. Create or edit `local.properties`
3. Update the `defaultPropertiesFileName` value in the `build.gradle` to point to your `local.properties` file
3. Add `TMDB_API_READ_ACCESS_BEARER_TOKEN=Bearer <<your-api-read-access-token>>`

## Building

```
./gradlew FlixSample:assembleDebug
```

---

# License

    MIT License
    
    Modified work copyright (c) 2023 ndtp
    Original work copyright (c) 2023 Andrew Carmichael
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
