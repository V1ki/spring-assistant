package org.jetbrains.plugins.template.maven;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public interface MavenReIndexingDependencyChangeSubscriber {
  static MavenReIndexingDependencyChangeSubscriber getInstance(Project project) {
    return ServiceManager.getService(project, MavenReIndexingDependencyChangeSubscriber.class);
  }
}
