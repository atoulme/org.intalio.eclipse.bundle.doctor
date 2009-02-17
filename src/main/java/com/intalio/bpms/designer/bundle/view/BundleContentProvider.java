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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.service.packageadmin.RequiredBundle;

import com.intalio.bpms.designer.bundle.IBundleManager;

/**
 * A content provider to provide a list of the current bundles.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class BundleContentProvider implements ITreeContentProvider  {

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IBundleManager) {
            return ((IBundleManager) parentElement).getAllBundles().toArray();
        } else if (parentElement instanceof TreeNode) {
            return ((TreeNode) parentElement).getChildren();
        } else if (parentElement instanceof RequiredBundle) {
            return new Object[] {new TreeNode(TreeNode.DEPENDENCIES, (RequiredBundle) parentElement), 
                    new TreeNode(TreeNode.FRAGMENTS, (RequiredBundle) parentElement)};
        }
        return null;
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {
        Object[] children = getChildren(element);
        return children != null && children.length > 0;
    }

    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
    
}
