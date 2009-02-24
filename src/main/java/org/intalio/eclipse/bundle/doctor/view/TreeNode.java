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
package org.intalio.eclipse.bundle.doctor.view;

import org.intalio.eclipse.bundle.doctor.BundlePlugin;
import org.intalio.eclipse.bundle.doctor.internal.ManifestBasedBundle;
import org.osgi.service.packageadmin.RequiredBundle;


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
        if (_bundle instanceof ManifestBasedBundle) {
            return new Object[] {};
        }
        switch(_type) {
        case FRAGMENTS:
            return BundlePlugin.getDefault().getBundleManager().
                getFragments(_bundle.getBundle()).toArray();
        case DEPENDENCIES:
            return BundlePlugin.getDefault().getBundleManager().
                getRequiringBundles(_bundle).toArray();
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
