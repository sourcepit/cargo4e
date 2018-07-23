package org.sourcepit.cargo4e.ui.wizards;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class NewRustProjectWizardPage extends WizardPage {
	private Text projectNameText;

	public NewRustProjectWizardPage() {
		super("wizardPage");
		setTitle("Rust Project");
		setDescription("This wizard creates a new Rust project.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		projectNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectNameText.setLayoutData(gd);
		projectNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		setPageComplete(false);
		setControl(container);
	}

	private void dialogChanged() {
		String projectName = getProjectName();
		if (projectName.length() == 0) {
			updateStatus("Project name must be specified");
			return;
		}
		if (ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).exists()) {
			updateStatus("Project with same name already exists");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getProjectName() {
		return projectNameText.getText();
	}
}