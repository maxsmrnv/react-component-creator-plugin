package fabs.util;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import fabs.component.ComponentCreator;
import fabs.component.ComponentCreatorDialog;

import java.awt.*;
import java.util.Map;

public abstract class AbstractCreatorAction extends AnAction {

    protected abstract AbstractCreator createCreator(VirtualFile directory, String componentName, Map<String, Object> templateModel, String[] files);

    @Override
    public void update(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

        Navigatable navigatable = anActionEvent.getData(CommonDataKeys.NAVIGATABLE);
        anActionEvent.getPresentation().setEnabledAndVisible(project != null && navigatable != null);
    }

    protected VirtualFile getLocation(VirtualFile file) {
        if (file.isDirectory()) {
            return file;
        }
        return file.getParent();
    }

     @Override
    public void actionPerformed(AnActionEvent e) {
        ComponentCreatorDialog dialog = new ComponentCreatorDialog();
        VirtualFile selectedLocation = e.getData(CommonDataKeys.VIRTUAL_FILE);
        VirtualFile targetLocation = getLocation(selectedLocation);

        final int width = dialog.getWidth();
        final int height = dialog.getHeight();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width / 2) - (width / 2);
        int y = (screenSize.height / 2) - (height / 2);
        dialog.setLocation(x, y);

        dialog.pack();
        dialog.setVisible(true);

        final String componentName = dialog.getComponentName();

        if (dialog.isCanceled()) {
            return;
        }

        ApplicationManager.getApplication().runWriteAction(
                this.createCreator(targetLocation, componentName, dialog.getTemplateVars(), dialog.getFiles())
        );
    }

}
