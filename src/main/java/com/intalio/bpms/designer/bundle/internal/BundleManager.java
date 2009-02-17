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
package com.intalio.bpms.designer.bundle.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.internal.provisional.frameworkadmin.BundleInfo;
import org.eclipse.equinox.internal.provisional.frameworkadmin.BundlesState;
import org.eclipse.equinox.internal.provisional.frameworkadmin.FrameworkAdmin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.util.tracker.ServiceTracker;

import com.intalio.bpms.designer.bundle.IBundleManager;

/**
 * The BundleInfo is the object that contains the bundles.
 *
 *
 * @author <a href="http://www.intalio.com">Intalio Inc.</a>
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class BundleManager implements IBundleManager {

    private ServiceTracker _bundleTracker;
    private ServiceTracker _framework;
    
    public BundleManager(BundleContext context) {
        _bundleTracker = new ServiceTracker(context, PackageAdmin.class.getName(), null);
        _bundleTracker.open();
        _framework = new ServiceTracker(context, FrameworkAdmin.class.getName(), null);
        _framework.open();
    }
    
    public void close() {
        _bundleTracker.close();
        _framework.close();
    }
    
    public Collection<RequiredBundle> getAllBundles() {
        PackageAdmin packageAdmin = (PackageAdmin) _bundleTracker.getService();
        List<RequiredBundle> required = new ArrayList<RequiredBundle>();
        for (RequiredBundle r : packageAdmin.getRequiredBundles(null)) {
            required.add(r);
        }
        Collections.sort(required, new Comparator<RequiredBundle>() {
            public int compare(RequiredBundle o1, RequiredBundle o2) {
                return o1.getSymbolicName().compareTo(o2.getSymbolicName());
            }
        });
        return required;
    }
    
    public Collection<Bundle> getFragments(Bundle bundle) {
        PackageAdmin packageAdmin = (PackageAdmin) _bundleTracker.getService();
        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle[] fragments = packageAdmin.getFragments(bundle);
        if (fragments != null) {
            bundles.addAll(Arrays.asList(fragments));
        }
        Collections.sort(bundles, new Comparator<Bundle>() {
            public int compare(Bundle o1, Bundle o2) {
                return o1.getSymbolicName().compareTo(o2.getSymbolicName());
            }
        });
        return bundles;
    }

    public Collection<Bundle> getRequiringBundles(RequiredBundle bundle) {
        List<Bundle> bundles = new ArrayList<Bundle>();
        Bundle[] dependencies = bundle.getRequiringBundles();
        if (dependencies != null) {
            bundles.addAll(Arrays.asList(dependencies));
        }
        Collections.sort(bundles, new Comparator<Bundle>() {
            public int compare(Bundle o1, Bundle o2) {
                return o1.getSymbolicName().compareTo(o2.getSymbolicName());
            }
        });
        return bundles;
    }
    
    @SuppressWarnings("restriction")
    public void install(BundleInfo bundle) throws BundleException {
        FrameworkAdmin framework = (FrameworkAdmin) _framework.getService();
        BundlesState state = framework.getRunningManipulator().getBundlesState();
        state.installBundle(bundle);
        state.resolve(true);
    }
    
    public void install(String location) throws BundleException {
        install(new BundleInfo(location));
    }
    
    
}