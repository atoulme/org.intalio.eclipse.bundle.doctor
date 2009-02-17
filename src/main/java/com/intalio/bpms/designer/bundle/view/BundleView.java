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
 * Feb 16, 2009      Antoine Toulme     Created
 */
package com.intalio.bpms.designer.bundle.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.misc.StringMatcher;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.RequiredBundle;

import com.intalio.bpms.designer.bundle.BundlePlugin;
import com.intalio.bpms.designer.bundle.IBundleManager;
import com.intalio.bpms.designer.bundle.wizard.InstallWizard;

/**
 * A view showing the bundles of the running instance.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class BundleView extends ViewPart {

    private class ActivateAction extends Action {

        @Override
        public String getText() {
            return "Activate";
        }
        
        @Override
        public void run() {
            Object elt = ((IStructuredSelection) _viewer.getSelection()).getFirstElement();
            if (elt instanceof RequiredBundle) {
                elt = ((RequiredBundle) elt).getBundle();
            }
            if (elt instanceof Bundle) {
                try {
                    ((Bundle) elt).start();
                    MessageDialog.openInformation(PlatformUI.getWorkbench().
                            getActiveWorkbenchWindow().getShell(), 
                            "Bundle started", ((Bundle) elt).getSymbolicName() + 
                            " was started successfully");
                } catch (BundleException e) {
                    MessageDialog.openError(PlatformUI.getWorkbench().
                            getActiveWorkbenchWindow().getShell(), 
                            "Error starting a bundle", ((Bundle) elt).getSymbolicName() + 
                            " could not be started:\n" + e.getMessage());
                }
            }
            _viewer.refresh();
        }
    }
    
    private class InstallAction extends Action {
        
        @Override
        public String getText() {
            return "Install...";
        }
        
        @Override
        public void run() {
            
            WizardDialog dialog = new WizardDialog(getSite().getShell(), new InstallWizard());
            dialog.open();
        }
    }
    
    private TreeViewer _viewer;
    private Text _filterText;
    
    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout());
        Group filterGroup = new Group(parent, SWT.NONE);
        filterGroup.setLayout(new FillLayout());
        filterGroup.setText("(Filter the bundles by name)");
        _filterText = new Text(filterGroup, SWT.NONE);
        _filterText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (_viewer != null && !_viewer.getTree().isDisposed()) {
                    _viewer.refresh();
                }
            }
        });
        filterGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        _viewer = new TreeViewer(parent);
        _viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
        _viewer.setContentProvider(new BundleContentProvider());
        _viewer.setLabelProvider(new BundleLabelProvider());
        _viewer.setInput(BundlePlugin.getDefault().getBundleManager());
        
        MenuManager manager = createMenuManager();
        _viewer.getTree().setMenu(manager.createContextMenu(_viewer.getTree()));
        _viewer.addFilter(new ViewerFilter() {
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                    Object element) {
                if (parentElement instanceof IBundleManager) {
                    if (_filterText.getText().length() == 0) {
                        return true;
                    }
                    StringMatcher matcher = new StringMatcher(
                            _filterText.getText(), true, false);
                    return matcher.match(((RequiredBundle) element).getSymbolicName());
                }
                return true;
            }
        });
    }

    private MenuManager createMenuManager() {
        MenuManager mgr = new MenuManager();
        mgr.add(new ActivateAction());
        mgr.add(new InstallAction());
        return mgr;
    }

    
    @Override
    public void setFocus() {
        if (_viewer != null && !_viewer.getTree().isDisposed()) {
            _viewer.getTree().setFocus();
        }
    }

}
