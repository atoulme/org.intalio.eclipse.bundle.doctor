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
package org.intalio.eclipse.bundle.doctor.view;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.internal.misc.StringMatcher;
import org.eclipse.ui.part.ViewPart;
import org.intalio.eclipse.bundle.doctor.BundlePlugin;
import org.intalio.eclipse.bundle.doctor.IBundleManager;
import org.intalio.eclipse.bundle.doctor.internal.ManifestBasedBundle;
import org.intalio.eclipse.bundle.doctor.wizard.InstallWizard;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.RequiredBundle;


/**
 * A view showing the bundles of the running instance.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class BundleView extends ViewPart {

    /**
     * An action to activate the current plugin under selection
     * @author <a href="http://www.intalio.com">Intalio Inc.</a>
     * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
     */
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
        
        @Override
        public boolean isEnabled() {
            Object elt = ((IStructuredSelection) _viewer.getSelection()).getFirstElement();
            if (elt instanceof RequiredBundle) {
                elt = ((RequiredBundle) elt).getBundle();
            }
            return elt != null;
        }
    }
    
    /**
     * Install a bundle selected in the tree.
     * @author <a href="http://www.intalio.com">Intalio Inc.</a>
     * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
     */
    private class InstallAction extends Action {

        @Override
        public String getText() {
            return "Install";
        }

        @Override
        public void run() {
            Object elt = ((IStructuredSelection) _viewer.getSelection()).getFirstElement();
            String location = null;
            String symbolicName = null;
            if (elt instanceof ManifestBasedBundle) {
                location = ((ManifestBasedBundle)elt).getAbsolutePath();
                symbolicName = ((ManifestBasedBundle)elt).getSymbolicName();
            } else {
                throw new IllegalArgumentException("Don't know how to handle this bundle :" + elt);
            }
            try {
                BundlePlugin.getDefault().getBundleManager().
                    install(new URL("file", "", location).toString());
            } catch (Exception e) {
                MessageDialog.openError(getSite().getShell(), 
                        NLS.bind("Error while installing {0}", symbolicName), 
                        e.getMessage());
                e.printStackTrace();
            }
            _viewer.refresh();
        }

        @Override
        public boolean isEnabled() {
            Object elt = ((IStructuredSelection) _viewer.getSelection()).getFirstElement();
            if (elt instanceof RequiredBundle) {
                elt = ((RequiredBundle) elt).getBundle();
            }
            return elt == null || (elt instanceof Bundle 
                && ((Bundle) elt).getState() == Bundle.UNINSTALLED);
        }
    }

    /**
     * An action to install a plugin through a wizard.
     * @author <a href="http://www.intalio.com">Intalio Inc.</a>
     * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
     */
    private class InstallWizardAction extends Action {
        
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
        Composite toolbar = new Composite(parent, SWT.NONE);
        toolbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        TableWrapLayout toolbarLayout = new TableWrapLayout();
        toolbarLayout.numColumns = 2;
        toolbar.setLayout(toolbarLayout);
        
        _filterText = new Text(toolbar, SWT.SEARCH | SWT.CANCEL);
        _filterText.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        _filterText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                if (_viewer != null && !_viewer.getTree().isDisposed()) {
                    _viewer.refresh();
                }
            }
        });
        
        Composite buttonsComposite = new Composite(toolbar, SWT.NONE);
        buttonsComposite.setLayout(new TableWrapLayout());
        buttonsComposite.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
        ImageHyperlink refreshButton = new ImageHyperlink(buttonsComposite, SWT.NONE);
        refreshButton.setImage(BundlePlugin.getDefault().getImageRegistry().
                get("icons/obj16/refresh.gif"));
        refreshButton.addHyperlinkListener(new HyperlinkAdapter() {
            
            @Override
            public void linkActivated(HyperlinkEvent e) {
                if (_viewer != null && !_viewer.getTree().isDisposed()) {
                    _viewer.refresh();
                }
            }
        });
        
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
        mgr.add(new Separator());
        mgr.add(new InstallWizardAction());
        return mgr;
    }

    
    @Override
    public void setFocus() {
        if (_viewer != null && !_viewer.getTree().isDisposed()) {
            _viewer.getTree().setFocus();
        }
    }

}
