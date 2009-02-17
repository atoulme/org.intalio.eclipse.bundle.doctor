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
package com.intalio.bpms.designer.bundle.view;

import org.osgi.service.packageadmin.RequiredBundle;

import com.intalio.bpms.designer.bundle.BundlePlugin;

/**
 * A tree node to represent intermediate nodes, currently the fragments
 * and the dependencies.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class TreeNode {
    public static final int FRAGMENTS = 0, DEPENDENCIES = 1;
    
    private int _type;
    
    private RequiredBundle _bundle;
    
    public TreeNode(int type, RequiredBundle bundle) {
        _type = type;
        _bundle = bundle;
    }
    
    /**
     * @return its children depending on its type.
     */
    public Object[] getChildren() {
        switch(_type) {
        case FRAGMENTS:
            return BundlePlugin.getDefault().getBundleManager().getFragments(_bundle.getBundle()).toArray();
        case DEPENDENCIES:
            return BundlePlugin.getDefault().getBundleManager().getRequiringBundles(_bundle).toArray();
        default:
            throw new IllegalArgumentException("Invalid node type");
        }
    }

    /**
     * @return the node type
     */
    public int getType() {
        return _type;
    }
}
