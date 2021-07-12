package org.jetbrains.plugins.template

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.module.Module

interface ModuleBuilderPostProcessor {

    /**
     * @param module module
     * @return true if project is imported, false otherwise
     */
    fun postProcess(module: Module?): Boolean
}