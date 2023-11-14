---
title: Testify Embraces Semantic Versioning
description: Testify is gearing up for a significant change in its release strategy.
slug: semantic-versioning
authors:
  - name: Daniel Jette
    title: Core Contributor to Android Testify
    url: https://github.com/DanielJette
    image_url: https://github.com/DanielJette.png
tags: [versioning, releases]
hide_table_of_contents: true
---

# Testify Embraces Semantic Versioning

Testify, the open-source library for Android Screenshot Testing, is gearing up for a significant change in its release strategy. We are excited to announce the adoption of Semantic Versioning, a standardized versioning system that promises to bring more clarity and efficiency to Testify's development process.

---

Testify empowers Android developers to effortlessly set up screenshot tests for their applications. Capturing screenshots offers a valuable tool for monitoring UI quality and reviewing changes efficiently. As Testify continues to evolve, we are making pivotal changes to our release process, transitioning to Semantic Versioning for a more streamlined and user-friendly experience.

Testify currently follows a custom release process where Major Versions are set as milestones leading up to a complete feature set. This approach, while effective, has resulted in long release cycles, stretching over many month. This has prompted us to explore a more standardized versioning system for improved communication and quicker releases.

The decision to embrace Semantic Versioning is driven by a desire to move more quickly, providing more frequent changes while better communicating the risk of changes and backward compatibility expectations. This change aims to enhance the overall development experience for Testify users and contributors.

These changes will take effect following the public release of Testify 2.0.0 in late Q4, 2023.

#### Benefits of Semantic Versioning

- **More Agile Development:** Frequent releases for smaller, focused changes.
- **Improved Communication:** Clear versioning communicates the nature of changes effectively.
- **Clearer Backward Compatibility Expectations:** Users can quickly assess the risk associated with each release.

### Release Cadence and Versioning Scheme

Testify will adopt the three-component Semantic Versioning scheme and release cadence which aligns with industry best practices:

- **Major Versions (X.0.0):** Significant changes, potentially including breaking API changes. Incremented for incompatible API changes.
- **Minor Versions (X.Y.0):** Backward-compatible additions of new features or enhancements. Increased for backward-compatible feature additions.
- **Patch Versions (X.Y.Z):** Backward-compatible bug fixes and minor improvements. Raised for backward-compatible bug fixes.

Under the new system, there will be no pre-release builds, and all changes will be categorized directly under the appropriate Semantic Version.

### Communication Strategy

To keep the Testify community well-informed, we have devised a comprehensive communication strategy:

- **Changelog:** A detailed changelog for all releases, available on [Testify.dev](https://testify.dev) and [GitHub](https://github.com/ndtp/android-testify/blob/main/CHANGELOG.md).
- **Blog Posts:** Major version updates will be accompanied by detailed blog posts on [Testify.dev](https://testify.dev/blog).
- **Documentation Updates:** Regular updates with every Major and Minor release.

Ensuring the stability and backward compatibility of Testify is paramount. A robust set of regression tests will verify backward compatibility for each release.

#### Community Involvement

We value community involvement and encourage users to actively participate:

- **GitHub Interaction:** Log bugs, submit requests, and propose changes directly through the [GitHub repository](https://github.com/ndtp/android-testify/issues/new/choose).
- **Stack Overflow Tag:** Tag questions with `android-testify` on [Stack Overflow](https://stackoverflow.com/questions/tagged/android-testify) for community-driven support.

Testify is on a journey of continuous improvement, and the adoption of Semantic Versioning is a crucial step in this evolution. We look forward to a more agile, transparent, and collaborative future with our vibrant community.

Stay tuned for more updates and releases as Testify embraces Semantic Versioning for a brighter tomorrow!

