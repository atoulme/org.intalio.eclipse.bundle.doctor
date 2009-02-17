/******************************************************************************
 * Copyright (c) 2009, Intalio Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Intalio Inc. - initial API and implementation
 *******************************************************************************
 * Date         Author             Changes
 * Feb 17, 2009      Antoine Toulme     Created
 */
package com.intalio.bpms.designer.bundle.wizard;

import java.net.URL;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.intalio.bpms.designer.bundle.BundlePlugin;

/**
 * The first page of the installation wizard.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class InstallWizardPage extends WizardPage {

    private StyledText _directoryBrowseText;
    private String _exportPath;
    private StyledText _feedbackText;
    
    public InstallWizardPage() {
        super("installWizardPage", "Install a new plugin", null);
    }

    public void createControl(Composite parent) {
        ManagedForm form = new ManagedForm(parent);
        FormToolkit toolkit = form.getToolkit();
        form.getForm().getBody().setLayout(new GridLayout());
        Group grp = new Group(form.getForm().getBody(), SWT.NONE);
        grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        grp.setText("Plugin location");
        TableWrapLayout layout = new TableWrapLayout();
        layout.numColumns = 3;
        grp.setLayout(layout);
        _directoryBrowseText = new StyledText(grp, SWT.READ_ONLY);
        _directoryBrowseText.setEditable(false);
        _directoryBrowseText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        Button browseButton = toolkit.createButton(grp, "Browse", SWT.PUSH);
        browseButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(getShell(), 
                        SWT.OPEN | SWT.MULTI);
                dialog.setFilterPath(_exportPath);
                dialog.setText("Choose a .jar or a directory representing a plugin");
                String res = dialog.open();
                if (res != null) {
                    _exportPath = res;
                    _directoryBrowseText.setText(res);
                }
            }
        });
        
        Button installButton = toolkit.createButton(grp, "Install", SWT.PUSH);
        installButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e1) {
                if (_exportPath == null || _exportPath.length() == 0) {
                    _feedbackText.setText("Please enter a path to a plugin");
                } else {
                    try {
                        BundlePlugin.getDefault().getBundleManager().
                            install(new URL("file", "", _exportPath).toString());
                        _feedbackText.setText("Plugin installed successfully!");
                    } catch(Exception e) {
                        e.printStackTrace();
                        _feedbackText.setText(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
                    }
                }
            }
        });
        
        Group feedbackGroup = new Group(form.getForm().getBody(), SWT.NONE);
        feedbackGroup.setText("Feedback");
        feedbackGroup.setLayout(new FillLayout());
        feedbackGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        _feedbackText = new StyledText(feedbackGroup, SWT.MULTI | SWT.WRAP | SWT.READ_ONLY);
//        _feedbackText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        setControl(form.getForm());
    }

}
