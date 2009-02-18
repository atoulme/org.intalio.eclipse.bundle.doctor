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
package org.intalio.eclipse.bundle.doctor;

import java.util.Collection;

import org.eclipse.equinox.internal.provisional.frameworkadmin.BundleInfo;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.RequiredBundle;

/**
 * An interface to manage bundles.
 * It deals with the underlying framework.
 * 
 * The implementation of this object is expected to manage the lifecycle
 * of the platform.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public interface IBundleManager {

    /**
     * @return all the bundles currently installed in this instance.
     */
    public Collection<RequiredBundle> getAllBundles();

    /**
     * @param bundle the host bundle 
     * @return the fragments for the bundle
     */
    public Collection<Bundle> getFragments(Bundle bundle);

    /**
     * @param bundle the bundle that requires the bundles returned
     * @return the bundles that the bundle passed as parameter depends on.
     */
    public Collection<Bundle> getRequiringBundles(RequiredBundle bundle);
    
    /**
     * @param bundle installs a bundle in the current framework.
     * @throws BundleException
     */
    public void install(BundleInfo bundle) throws BundleException;
    
    /**
     * @param location installs the bundle located at the location passed as argument.
     * @throws BundleException
     */
    public void install(String location) throws BundleException;
}
