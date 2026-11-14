package com.example.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class HelloWorld {
    public static void main(String[] args) {
        // 1. Create the Display (handles the OS event loop) and Shell (the window)
        final Display display = new Display();
        final Shell shell = new Shell(display);
        shell.setText("Hello World");
        shell.setLayout(new GridLayout(2, false));

        // 2. Create the widgets
        final Label label = new Label(shell, SWT.LEFT);
        label.setText("Your &Name:");
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        final Text text = new Text(shell, SWT.BORDER | SWT.SINGLE);
        final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false);
        data.minimumWidth = 120;
        text.setLayoutData(data);

        final Button button = new Button(shell, SWT.PUSH);
        button.setText("Say Hello");
        shell.setDefaultButton(button);
        button.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false, 2, 1));

        final Label output = new Label(shell, SWT.CENTER);
        output.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        // 3. Add an event listener to the button
        button.addListener(SWT.Selection, event -> {
            String name = text.getText().trim();
            if (name.length() == 0) { name = "world"; }
            output.setText("Hello " + name + "!");
        });

        // 4. Open the window and start the event loop
        shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
    }
}