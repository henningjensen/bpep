package no.bekk.boss.bpep.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public abstract class AbstractModalDialog extends Dialog {

    private final Display display;

    public AbstractModalDialog(Shell parent) {
        super(parent, SWT.APPLICATION_MODAL);
        display = getParent().getDisplay();
    }

    protected void display(Shell shell) {
        shell.pack();
        placeDialogInCenter(shell);
        shell.open();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) display.sleep();
        }
    }

    private void placeDialogInCenter(Shell shell){
        Rectangle parentSize = getParent().getBounds();
        Rectangle mySize = shell.getBounds();

        int locationX, locationY;
        locationX = (parentSize.width - mySize.width)/2+parentSize.x;
        locationY = (parentSize.height - mySize.height)/2+parentSize.y;

        shell.setLocation(new Point(locationX, locationY));
    }

}
