package no.bekk.boss.bpep.view;

import java.util.ArrayList;
import java.util.List;

import no.bekk.boss.bpep.generator.Generator;
import no.bekk.boss.bpep.resolver.Resolver;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class CreateDialog extends AbstractModalDialog {

    private Generator generator;

    public CreateDialog(Shell parent, Generator generator) {
        super(parent);
        this.generator = generator;
    }

    public void show(final ICompilationUnit compilationUnit) throws JavaModelException {
        final Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CENTER);

        shell.setText("Generate Builder Pattern Code");

        shell.setLayout(new GridLayout(2, false));

        Group fieldGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
        fieldGroup.setText("Select fields to include:");
        fieldGroup.setLayout(new RowLayout(SWT.VERTICAL));
        GridData fieldGroupLayoutData = new GridData();
        fieldGroupLayoutData.verticalSpan = 2;
		fieldGroup.setLayoutData(fieldGroupLayoutData);

        final List<Button> fieldButtons = createFieldSelectionCheckboxes(compilationUnit, fieldGroup);
        createSelectAllButton(shell, fieldButtons);
        createSelectNoneButton(shell, fieldButtons);

        Group optionGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
        optionGroup.setText("Options:");
        optionGroup.setLayout(new RowLayout(SWT.VERTICAL));
        GridData optionGridData = new GridData();
        optionGridData.horizontalSpan = 2;
        optionGridData.horizontalAlignment = SWT.FILL;
		optionGroup.setLayoutData(optionGridData);

        createCreateClassConstructorOption(optionGroup);

        final Button createBuilderConstructor = new Button(optionGroup, SWT.RADIO);
        createBuilderConstructor.setText("Create constructor in builder");

        final Button createCopyConstructorButton = new Button(optionGroup, SWT.CHECK);
        createCopyConstructorButton.setSelection(true);
        createCopyConstructorButton.setText("Create copy constructor in builder");

        final Button formatSourceButton = new Button(optionGroup, SWT.CHECK);
        formatSourceButton.setSelection(true);
        formatSourceButton.setText("Format source (entire file)");
        
        final Button executeButton = new Button(shell, SWT.PUSH);
        executeButton.setText("Generate");
        shell.setDefaultButton(executeButton);

        final Button cancelButton = new Button(shell, SWT.PUSH);
        cancelButton.setText("Cancel");

        Listener clickListener = new Listener() {
        	public void handleEvent(Event event) {
        		if (event.widget == executeButton) {

        			List<IField> selectedFields = new ArrayList<IField>();
        			for (Button button : fieldButtons) {
						if (button.getSelection()) {
							selectedFields.add((IField)button.getData());
						}
					}

        			generator.generate(compilationUnit, createBuilderConstructor.getSelection(), createCopyConstructorButton.getSelection(), formatSourceButton.getSelection(), selectedFields);
        			shell.dispose();
        		} else {
        			shell.dispose();
        		}
        	}
        };

        executeButton.addListener(SWT.Selection, clickListener);
        cancelButton.addListener(SWT.Selection, clickListener);

        optionGroup.pack();

        display(shell);
    }

	private List<Button> createFieldSelectionCheckboxes(final ICompilationUnit compilationUnit, Group fieldGroup) {
		List<IField> fields = Resolver.findAllFields(compilationUnit);
		final List<Button> fieldButtons = new ArrayList<Button>();
		for (IField field : fields) {
			Button button = new Button(fieldGroup, SWT.CHECK);
			button.setText(Resolver.getName(field) + "(" + Resolver.getType(field) + ")");
			button.setData(field);
			button.setSelection(true);
			fieldButtons.add(button);
		}
		return fieldButtons;
	}

	private void createSelectAllButton(final Shell shell, final List<Button> fieldButtons) {
		Button btnSelectAll = new Button(shell, SWT.PUSH);
		btnSelectAll.setText("Select All");
		GridData btnSelectAllLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		btnSelectAllLayoutData.verticalIndent = 10;
		btnSelectAll.setLayoutData(btnSelectAllLayoutData);
		btnSelectAll.addSelectionListener(new FieldSelectionAdapter(fieldButtons, true));
	}

	private void createSelectNoneButton(final Shell shell, final List<Button> fieldButtons) {
		Button btnSelectNone = new Button(shell, SWT.PUSH);
		btnSelectNone.setText("Deselect All");
		GridData selectNoneGridData = new GridData();
		selectNoneGridData.verticalAlignment = SWT.BEGINNING;
		btnSelectNone.setLayoutData(selectNoneGridData);
		btnSelectNone.addSelectionListener(new FieldSelectionAdapter(fieldButtons, false));
	}

	private void createCreateClassConstructorOption(Group optionGroup) {
		final Button createClassConstructor = new Button(optionGroup, SWT.RADIO);
		createClassConstructor.setSelection(true);
		createClassConstructor.setText("Create class constructor");
	}
	
	private class FieldSelectionAdapter extends SelectionAdapter {
		private final List<Button> buttons;
		private final boolean checked;

		public FieldSelectionAdapter(final List<Button> buttons, final boolean checked) {
			this.buttons = buttons;
			this.checked = checked;
		}

		@Override
		public void widgetSelected(SelectionEvent event) {
			for (Button button : buttons) {
				button.setSelection(checked);
			}
		}
	}
}
