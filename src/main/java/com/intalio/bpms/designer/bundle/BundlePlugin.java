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
package com.intalio.bpms.designer.bundle;


import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.intalio.bpms.designer.bundle.internal.BundleManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class BundlePlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.intalio.bpms.designer.bundle"; //$NON-NLS-1$

	// The shared instance
	private static BundlePlugin plugin;
	
    private BundleManager _bundleManager;
	
	/**
	 * The constructor
	 */
	public BundlePlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		_bundleManager = new BundleManager(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		_bundleManager.close();
		_bundleManager = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static BundlePlugin getDefault() {
		return plugin;
	}
	
	/**
	 * @return the bundle manager hooked to the framework to manipulate it.
	 */
    public IBundleManager getBundleManager() {
        return _bundleManager;
    }   
    
    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        reg.put("icons/obj16/ext_plugin_obj.gif", 
                imageDescriptorFromPlugin(PLUGIN_ID, "icons/obj16/ext_plugin_obj.gif"));
        reg.put("icons/obj16/frgmt_obj.gif", 
                imageDescriptorFromPlugin(PLUGIN_ID, "icons/obj16/frgmt_obj.gif"));
        reg.put("icons/obj16/plugin_obj.gif", 
                imageDescriptorFromPlugin(PLUGIN_ID, "icons/obj16/plugin_obj.gif"));
    }

}
