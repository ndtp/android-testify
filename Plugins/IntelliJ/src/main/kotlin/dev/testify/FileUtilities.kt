package dev.testify

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.PsiShortNamesCache

fun findClassByName(className: String, project: Project): PsiClass? {
    val psiShortNamesCache = PsiShortNamesCache.getInstance(project)
    val classes = psiShortNamesCache.getClassesByName(className, GlobalSearchScope.projectScope(project))
    return classes.firstOrNull()
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
 * Input: ./src/test/snapshots/images/dev.testify.samples.paparazzi.ui.common.composables_CastMemberScreenshotTest_default.png
 */
fun findPaparazziMethod(imageFile: VirtualFile, project: Project): PsiMethod? {
    if (imageFile.path.contains("/test").not()) return null
    imageFile.nameWithoutExtension.let { imageName ->
        // imageName = composables_CastMemberScreenshotTest_default
        val parts = imageName.split("_")
        if (parts.size == 3) {
            val (_, className, methodName) = parts
            // _ = composables
            // className = CastMemberScreenshotTest
            // methodName = default
            findClassByName(className, project)?.let { psiClass ->
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
