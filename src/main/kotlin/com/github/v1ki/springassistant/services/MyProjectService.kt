package com.github.v1ki.springassistant.services

import com.github.v1ki.springassistant.MyBundle
import com.intellij.openapi.project.Project

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
