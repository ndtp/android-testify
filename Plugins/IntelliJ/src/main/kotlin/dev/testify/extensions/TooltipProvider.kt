package dev.testify.extensions

import com.intellij.psi.PsiElement
import com.intellij.util.Function

typealias TooltipProvider = Function<PsiElement?, String?>

const val SCREENSHOT_INSTRUMENTATION = "dev.testify.annotation.ScreenshotInstrumentation"
const val SCREENSHOT_INSTRUMENTATION_LEGACY = "com.shopify.testify.annotation.ScreenshotInstrumentation"
