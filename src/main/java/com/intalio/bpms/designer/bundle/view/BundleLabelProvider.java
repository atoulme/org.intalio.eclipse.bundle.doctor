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
 *
 * Date         Author             Changes
 * Feb 16, 2009      Antoine Toulme     Created
 */
package com.intalio.bpms.designer.bundle.view;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.eclipse.osgi.framework.internal.core.BundleFragment;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.RequiredBundle;

import com.intalio.bpms.designer.bundle.BundlePlugin;

/**
 * the label provider for bundles.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class BundleLabelProvider extends LabelProvider implements
        ILabelProvider {

    @Override
    public String getText(Object element) {
        if (element instanceof RequiredBundle) {
            return getText(((RequiredBundle) element).getBundle());
        } else if (element instanceof Bundle) {
            return NLS.bind("{0} ({1})", ((Bundle) element).getSymbolicName(), 
                    ((AbstractBundle) element).getVersion());
        } else if (element instanceof TreeNode) {
            switch(((TreeNode) element).getType()) {
            case TreeNode.DEPENDENCIES:
                return "Dependencies";
            case TreeNode.FRAGMENTS:
                return "Fragments";
            default:
                throw new IllegalArgumentException(
                        "Don't know what text show for " + 
                            ((TreeNode) element).getType());
            }
        }
        return super.getText(element);
    }
    
    @Override
    public Image getImage(Object element) {
        if (element instanceof RequiredBundle) {
            return getImage(((RequiredBundle) element).getBundle());
        } else if (element instanceof Bundle) {
            switch (((Bundle) element).getState()) {
            case Bundle.ACTIVE:
            case Bundle.INSTALLED:
            case Bundle.RESOLVED:
                if (element instanceof BundleFragment) {
                    return BundlePlugin.getDefault().getImageRegistry().
                        get("icons/obj16/frgmt_obj.gif");
                }
                return BundlePlugin.getDefault().getImageRegistry().
                    get("icons/obj16/plugin_obj.gif");
            case Bundle.STARTING:
            case Bundle.STOPPING:
                
            case Bundle.UNINSTALLED:
                return BundlePlugin.getDefault().getImageRegistry().
                    get("icons/obj16/ext_plugin_obj.gif");
            }
        }
        return super.getImage(element);
    }
}
