package org.jetbrains.plugins.template.maven;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.plugins.template.ModuleBuilderPostProcessor;

import java.util.List;

import static org.jetbrains.idea.maven.project.MavenProjectsManager.getInstance;
import static org.jetbrains.plugins.template.PsiCustomUtil.findFilesUnderRootInModule;

public class MavenModuleBuilderPostProcessor implements ModuleBuilderPostProcessor {
  @Override
  public boolean postProcess(Module module) {
    // TODO: Find a way to use GradleModuleBuilder instead of GradleProjectImportBuilder when adding a child module to the parent
    Project project = module.getProject();
    List<VirtualFile> pomFiles = findFilesUnderRootInModule(module, "pom.xml");
    if (pomFiles.size() == 0) { // not a maven project
      return true;
    } else {
      MavenProjectsManager mavenProjectsManager = getInstance(project);
      mavenProjectsManager.addManagedFiles(pomFiles);
      return false;
    }
  }
}
