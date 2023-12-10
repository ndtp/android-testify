package dev.testify.actions.base

import com.intellij.psi.PsiElement
import dev.testify.actions.screenshot.ScreenshotClearAction
import dev.testify.actions.screenshot.ScreenshotPullAction
import dev.testify.actions.screenshot.ScreenshotRecordAction
import dev.testify.actions.screenshot.ScreenshotTestAction
import dev.testify.actions.utility.DeleteBaselineAction
import dev.testify.actions.utility.RevealBaselineAction
import kotlin.reflect.KClass


@Suppress("UNCHECKED_CAST")
fun <TAction : BaseTestifyAction> KClass<TAction>.create(anchorElement: PsiElement): TAction =
    when {
        this == ScreenshotTestAction::class -> ScreenshotTestAction(anchorElement)
        this == ScreenshotRecordAction::class -> ScreenshotRecordAction(anchorElement)
        this == ScreenshotPullAction::class -> ScreenshotPullAction(anchorElement)
        this == ScreenshotClearAction::class -> ScreenshotClearAction(anchorElement)
        this == RevealBaselineAction::class -> RevealBaselineAction(anchorElement)
        this == DeleteBaselineAction::class -> DeleteBaselineAction(anchorElement)
        else -> throw IllegalArgumentException("Can not create instance of $this")
    } as TAction
