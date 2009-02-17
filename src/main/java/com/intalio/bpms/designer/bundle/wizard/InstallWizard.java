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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

/**
 * A wizard to install one or more bundles.
 * It's really just a prototype at this point.
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class InstallWizard extends Wizard implements IWorkbenchWizard {

    /**
     * Add one page: the installation page.
     */
    @Override
    public void addPages() {
        addPage(new InstallWizardPage());
    }
    
    /**
     * No particular step for initializing the wizard just now
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }
    
    /**
     * Most of the action takes place when clicking on install.
     * 
     */
    @Override
    public boolean performFinish() {
        return true;
    }


    
}
