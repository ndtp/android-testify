/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2026 ndtp
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dev.testify

import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.fileTypes.UnknownFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache

fun findClassByName(className: String, project: Project, packageName: String? = null): PsiClass? {
    val psiShortNamesCache = PsiShortNamesCache.getInstance(project)
    val classes = psiShortNamesCache.getClassesByName(className, GlobalSearchScope.projectScope(project))
    return if (packageName != null) {
        classes.firstOrNull { psiClass ->
            val pkg = psiClass.qualifiedName?.substringBeforeLast(".")
            packageName == pkg
        }
    } else {
        classes.firstOrNull()
    }
}

fun findMethod(methodName: String, psiClass: PsiClass): PsiMethod? {
    return psiClass.findMethodsByName(methodName, false).firstOrNull()
}

/**
 * The class and method a Testify baseline image name refers to, or `null` if it is not one.
 *
 * Testify names its baselines `<Class>_<method>.png`. Only the first `_` is a separator, so a test
 * named in the `renders_empty_state` style still resolves.
 *
 *     CastMemberScreenshotTest_renders_empty_state -> (CastMemberScreenshotTest, renders_empty_state)
 */
fun parseTestifyImageName(imageName: String): TestMethodReference? {
    val parts = imageName.split("_")
    if (parts.size < 2) return null
    return TestMethodReference(
        className = parts[0],
        methodName = parts.drop(1).joinToString("_")
    )
}

/**
 * The package, class and method a Paparazzi baseline image name refers to, or `null` if it is not
 * one.
 *
 * Paparazzi names its baselines `<package>_<Class>_<method>.png`, so the first two `_` are the
 * separators and the rest belong to the method name.
 *
 *     dev.testify.samples_CastMemberScreenshotTest_renders_empty_state
 *         -> (dev.testify.samples, CastMemberScreenshotTest, renders_empty_state)
 *
 * A package containing `_`, or a nested test class — which Paparazzi flattens as `Outer_Inner` —
 * would need the candidate splits to be resolved one by one, and are not supported.
 */
fun parsePaparazziImageName(imageName: String): TestMethodReference? {
    val parts = imageName.split("_")
    if (parts.size < 3) return null
    return TestMethodReference(
        packageName = parts[0],
        className = parts[1],
        methodName = parts.drop(2).joinToString("_")
    )
}

/** The test method a baseline image was recorded from. */
data class TestMethodReference(
    val className: String,
    val methodName: String,
    val packageName: String? = null
)

fun findTestifyMethod(imageFile: VirtualFile, project: Project): PsiMethod? {
    if (imageFile.path.contains("/androidTest/").not()) return null
    val reference = parseTestifyImageName(imageFile.nameWithoutExtension) ?: return null
    val psiClass = findClassByName(reference.className, project) ?: return null
    return findMethod(reference.methodName, psiClass)
}

fun findPaparazziMethod(imageFile: VirtualFile, project: Project): PsiMethod? {
    if (imageFile.path.contains("/test/").not()) return null
    val reference = parsePaparazziImageName(imageFile.nameWithoutExtension) ?: return null
    val psiClass = findClassByName(reference.className, project, reference.packageName) ?: return null
    return findMethod(reference.methodName, psiClass)
}

/**
 * Whether [fileName] is the baseline named by [baselineImageName], or a flavour-prefixed form of it.
 *
 * The two flavours name their baselines differently — Testify uses `Class_method.png` while
 * Paparazzi prefixes the package, `com.example_Class_method.png` — so an exact match is too strict.
 * The `_` boundary is what keeps it from being too loose: searching for `FooTest_default.png` must
 * not match `com.example_SubFooTest_default.png`, which a plain "contains" check would.
 */
fun isBaselineImageName(fileName: String, baselineImageName: String): Boolean =
    fileName.equals(baselineImageName, ignoreCase = true) ||
        fileName.endsWith("_$baselineImageName", ignoreCase = true)

/**
 * Whether [fileName] is the file [pattern] names, where a `*` in [pattern] stands for the method
 * name.
 *
 * Matching the two literal halves of the pattern is exact, where converting the glob to a regex
 * would leave the package separators as wildcards.
 */
fun matchesScreenshotName(fileName: String, pattern: String): Boolean {
    if (!pattern.contains('*')) return fileName == pattern

    val prefix = pattern.substringBefore('*')
    val suffix = pattern.substringAfter('*')
    return fileName.length >= prefix.length + suffix.length &&
        fileName.startsWith(prefix) &&
        fileName.endsWith(suffix)
}

/**
 * Every image in [scope] matching [baselineImageName] under [isBaselineImageName].
 *
 * Results are sorted so that a project with more than one match resolves to the same file every
 * time; the file type index itself gives no ordering guarantee.
 */
fun findBaselineImageFiles(
    baselineImageName: String,
    scope: GlobalSearchScope
): List<VirtualFile> {
    val fileType = FileTypeManager.getInstance().getStdFileType("Image")
    if (fileType is UnknownFileType) return emptyList()

    return FileTypeIndex.getFiles(fileType, scope)
        .filter { file -> isBaselineImageName(file.name, baselineImageName) }
        .sortedBy { it.path }
}
