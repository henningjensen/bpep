package no.bekk.boss.bpep.popup.actions;

import no.bekk.boss.bpep.generator.BuilderGenerator;
import no.bekk.boss.bpep.view.CreateDialog;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class GenerateBuilderAction extends Action implements IEditorActionDelegate, IWorkbenchWindowActionDelegate {

	private IEditorPart editor;

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		IEditorInput editorInput = editor.getEditorInput();
		try {
			manager.connect(editorInput);
			ICompilationUnit workingCopy = manager.getWorkingCopy(editorInput);

			CreateDialog dialog = new CreateDialog(new Shell(), new BuilderGenerator());
			dialog.show(workingCopy);

			synchronized (workingCopy) {
				workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
			}

		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			manager.disconnect(editorInput);
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = targetEditor;
	}

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public void init(IWorkbenchWindow window) {
        editor = window.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    }

}