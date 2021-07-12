package org.jetbrains.plugins.template;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public interface Bootstrap {
  static Bootstrap getInstance(Project project) {
    return ServiceManager.getService(project, Bootstrap.class);
  }
}
