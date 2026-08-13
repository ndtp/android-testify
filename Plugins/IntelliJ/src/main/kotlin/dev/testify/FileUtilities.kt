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

fun findTestifyMethod(imageFile: VirtualFile, project: Project): PsiMethod? {
    if (imageFile.path.contains("/androidTest").not()) return null
    imageFile.nameWithoutExtension.let { imageName ->
        val parts = imageName.split("_")
        if (parts.size == 2) {
            val (className, methodName) = parts
            findClassByName(className, project)?.let { psiClass ->
                return findMethod(methodName, psiClass)
            }
        }
    }
    return null
}

/**
 * Input: /Users/danjette/dev/android-testify/Samples/Paparazzi/src/test/snapshots/images/dev.testify.samples.paparazzi.ui.common.composables_CastMemberScreenshotTest_b.png
 */
fun findPaparazziMethod(imageFile: VirtualFile, project: Project): PsiMethod? {
    if (imageFile.path.contains("/test").not()) return null
    imageFile.nameWithoutExtension.let { imageName ->
        // imageName = composables_CastMemberScreenshotTest_default
        val parts = imageName.split("_")
        if (parts.size == 3) {
            val (packageName, className, methodName) = parts
            // _ = composables
            // className = CastMemberScreenshotTest
            // methodName = default
            findClassByName(className, project, packageName)?.let { psiClass ->
                return findMethod(methodName, psiClass)
            }
        }
    }
    return null
}

fun findPreviewMethod(imageFile: VirtualFile, project: Project): PsiMethod? {
    if (imageFile.path.contains("/screenshotTest").not()) return null
    val className = imageFile.parent.name
    imageFile.nameWithoutExtension.let { imageName ->
        val methodName = imageName.split("_").first()
        findClassByName(className, project)?.let { psiClass ->
            return findMethod(methodName, psiClass)
        }
    }
    return null
}

/**
 * Every image in [scope] whose name is [baselineImageName], or ends with it on a `_` boundary.
 *
 * The two flavours name their baselines differently — Testify uses `Class_method.png` while
 * Paparazzi prefixes the package, `com.example_Class_method.png` — so an exact match is too strict.
 * The `_` boundary is what keeps it from being too loose: searching for `FooTest_default.png` must
 * not match `com.example_SubFooTest_default.png`, which a plain "contains" check would.
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
        .filter { file ->
            file.name.equals(baselineImageName, ignoreCase = true) ||
                file.name.endsWith("_$baselineImageName", ignoreCase = true)
        }
        .sortedBy { it.path }
}
