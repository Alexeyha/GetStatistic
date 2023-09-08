package com.github.alexeyha.getclassstatisticsintentionaction

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.codeInsight.intention.preview.IntentionPreviewInfo
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.analysis.decompiler.psi.KotlinBuiltInFileType
import org.jetbrains.kotlin.psi.KtClass

class GetClassStatisticsIntentionAction : PsiElementBaseIntentionAction(), IntentionAction {

    private var previewText : String? = null

    private fun getStatistic(kotlinClass: KtClass) : String {
        val builder = StringBuilder()
        builder.append("Class: ${kotlinClass.fqName?.toString() ?: ""}\n")
            .append("Primary constructor: ${if (kotlinClass.primaryConstructor == null) 0 else 1}\n")
            .append("Number of secondary constructors: ${kotlinClass.secondaryConstructors.size}\n")
            .append("Number of functions: ${kotlinClass.body?.functions?.size ?: 0}")
        return builder.toString()
    }

    override fun getText() = familyName

    override fun getFamilyName() = "Get statistics"

    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        if (element.parent is KtClass) {
            val kotlinClass = element.parent as KtClass
            if (element.text.equals(kotlinClass.fqName.toString())) {
                previewText = getStatistic(kotlinClass)
                return true
            }
        }
        return false
    }

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        // Do nothing
    }

    override fun generatePreview(project: Project, editor: Editor, file: PsiFile): IntentionPreviewInfo {
        if (previewText == null) return IntentionPreviewInfo.EMPTY
        return IntentionPreviewInfo.CustomDiff(KotlinBuiltInFileType, "", previewText.orEmpty())
    }

    override fun startInWriteAction() = true
}