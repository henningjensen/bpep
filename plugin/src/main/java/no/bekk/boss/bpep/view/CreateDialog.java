package no.bekk.boss.bpep.view;

import java.util.ArrayList;
import java.util.List;

import no.bekk.boss.bpep.generator.Generator;

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

        List<IField> fields = generator.findAllFIelds(compilationUnit);
        final List<Button> fieldButtons = new ArrayList<Button>();
        for(IField field: fields) {
        	Button button = new Button(fieldGroup, SWT.CHECK);
        	button.setText(generator.getName(field) + "(" + generator.getType(field) + ")");
        	button.setData(field);
        	button.setSelection(true);
        	fieldButtons.add(button);
        }

        Button btnSelectAll = new Button(shell, SWT.PUSH);
        btnSelectAll.setText("Select All");
        GridData btnSelectAllLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
        btnSelectAllLayoutData.verticalIndent = 10;
		btnSelectAll.setLayoutData(btnSelectAllLayoutData);
        btnSelectAll.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
        		for (Button button : fieldButtons) {
					button.setSelection(true);
				}
        	}
        });
        Button btnSelectNone = new Button(shell, SWT.PUSH);
        btnSelectNone.setText("Deselect All");
        GridData selectNoneGridData = new GridData();
        selectNoneGridData.verticalAlignment = SWT.BEGINNING;
		btnSelectNone.setLayoutData(selectNoneGridData);
        btnSelectNone.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
        		for (Button button : fieldButtons) {
					button.setSelection(false);
				}
        	}
        });

        Group optionGroup = new Group(shell, SWT.SHADOW_ETCHED_IN);
        optionGroup.setText("Options:");
        optionGroup.setLayout(new RowLayout(SWT.VERTICAL));
        GridData optionGridData = new GridData();
        optionGridData.horizontalSpan = 2;
        optionGridData.horizontalAlignment = SWT.FILL;
		optionGroup.setLayoutData(optionGridData);

        final Button createPrivateClassConstructor = new Button(optionGroup, SWT.RADIO);
        createPrivateClassConstructor.setSelection(true);
        createPrivateClassConstructor.setText("Create private class constructor");

        final Button createBuilderConstructor = new Button(optionGroup, SWT.RADIO);
        createBuilderConstructor.setText("Create constructor in builder");

        final Button formatSourceButton = new Button(optionGroup, SWT.CHECK);
        formatSourceButton.setSelection(true);
        formatSourceButton.setText("Format source (entire file)");
        
        final Button executeButton = new Button(shell, SWT.PUSH);
        executeButton.setText("Generate");
//        GridData generateButtonGridData = new GridData();
//        generateButtonGridData.horizontalAlignment = SWT.END;
//		executeButton.setLayoutData(generateButtonGridData);

        final Button cancelButton = new Button(shell, SWT.PUSH);
        cancelButton.setText("Cancel");
//		GridData cancelButtonGridData = new GridData();
//		cancelButtonGridData.horizontalAlignment = SWT.END;
//		cancelButton.setLayoutData(cancelButtonGridData);

        Listener clickListener = new Listener() {
        	public void handleEvent(Event event) {
        		if (event.widget == executeButton) {

        			List<IField> selectedFields = new ArrayList<IField>();
        			for (Button button : fieldButtons) {
						if (button.getSelection()) {
							selectedFields.add((IField)button.getData());
						}
					}

        			generator.generate(compilationUnit, createBuilderConstructor.getSelection(), formatSourceButton.getSelection(), selectedFields);
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

}
