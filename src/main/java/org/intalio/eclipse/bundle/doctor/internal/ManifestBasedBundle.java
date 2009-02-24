/******************************************************************************
 * Copyright (c) 2009, Intalio Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Intalio Inc. - initial API and implementation
 *******************************************************************************/
package org.intalio.eclipse.bundle.doctor.internal;

import java.util.jar.Manifest;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.RequiredBundle;

/**
 * An implementation of RequiredBundle for bundles that aren't installed just yet.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class ManifestBasedBundle implements RequiredBundle {

    public static final String SYMBOLIC_NAME_ENTRY = "Bundle-SymbolicName";
    
    public static final String VERSION_ENTRY = "Bundle-Version";
    
    private String _symbolicName = null;
    
    private String _version = null;

    private String _absolutePath;
    
    public ManifestBasedBundle(Manifest m, String absolutePath) {
        this(m.getMainAttributes().getValue(SYMBOLIC_NAME_ENTRY).split(";")[0], 
                m.getMainAttributes().getValue(VERSION_ENTRY), absolutePath);
    }
    public ManifestBasedBundle(String name, String version, String absolutePath) {
        _symbolicName = name;
        _version = version;
        _absolutePath = absolutePath;
    }
    
    public Bundle getBundle() {
        // TODO Auto-generated method stub
        return null;
    }

    public Bundle[] getRequiringBundles() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getSymbolicName() {
        return _symbolicName;
    }

    public Version getVersion() {
        return Version.parseVersion(_version);
    }

    public boolean isRemovalPending() {
        return false;
    }
    
    /**
     * @return the abolute path to the jar of the directory
     */
    public String getAbsolutePath() {
        return _absolutePath;
    }

}
