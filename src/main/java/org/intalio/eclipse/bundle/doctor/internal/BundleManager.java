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
package org.intalio.eclipse.bundle.doctor.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.internal.provisional.frameworkadmin.BundleInfo;
import org.eclipse.equinox.internal.provisional.frameworkadmin.BundlesState;
import org.eclipse.equinox.internal.provisional.frameworkadmin.FrameworkAdmin;
import org.eclipse.osgi.baseadaptor.BaseData;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.intalio.eclipse.bundle.doctor.BundlePlugin;
import org.intalio.eclipse.bundle.doctor.IBundleManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.packageadmin.RequiredBundle;
import org.osgi.util.tracker.ServiceTracker;


/**
 * The bundle manager is the object that contains the bundles.
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
        required.addAll(Arrays.asList(packageAdmin.getRequiredBundles(null)));
        
        required.addAll(findDisabledPlugins(extractLocations(required)));
        Collections.sort(required, new Comparator<RequiredBundle>() {
            public int compare(RequiredBundle o1, RequiredBundle o2) {
                return o1.getSymbolicName().compareTo(o2.getSymbolicName());
            }
        });
        return required;
    }
    
    private List<String> extractLocations(List<RequiredBundle> required) {
        List<String> locations = new ArrayList<String>();
        for (RequiredBundle b : required) {
            locations.add(((BaseData) ((AbstractBundle) b.getBundle()).getBundleData()).getBundleFile().getBaseFile().getAbsolutePath());
        }
        return locations;
    }

    private List<RequiredBundle> findDisabledPlugins(List<String> locations) {
        File platform = new File(Platform.getInstallLocation().getURL().getFile());
        File pluginsDir = new File(platform, "plugins");
        File dropinsDir = new File(platform, "dropins");
        // now look them up
        List<RequiredBundle> bundles = new ArrayList<RequiredBundle>();
        bundles.addAll(internalFindInDirectory(pluginsDir, false, locations));
        bundles.addAll(internalFindInDirectory(dropinsDir, true, locations));
        return bundles;
    }
    
    private List<RequiredBundle> internalFindInDirectory(File dir, boolean lookInSubFolders, List<String> ignore) {
        List<RequiredBundle> bundles = new ArrayList<RequiredBundle>();
        for (File f : dir.listFiles()) {
            if (ignore.contains(f.getAbsolutePath())) {
                continue;
            }
            if (!f.isDirectory() && f.getName().endsWith(".jar")) {// if it's a jar
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(f);
                    Manifest manifest = jarFile.getManifest();
                    if (manifest.getMainAttributes().getValue(ManifestBasedBundle.SYMBOLIC_NAME_ENTRY) != null) {
                        bundles.add(new ManifestBasedBundle(manifest, f.getAbsolutePath()));
                    }
                } catch (IOException e) {
                    BundlePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, 
                            BundlePlugin.PLUGIN_ID, e.getMessage(), e));
                } finally {
                    if (jarFile != null) {
                        try {
                            jarFile.close();
                        } catch (IOException e) {
                        }
                    }
                }
                
            } else if (f.isDirectory()) {//maybe a directory
                //check if it contains META-INF/MANIFEST.MF
                File manifestFile = new File(f, "META-INF" + File.pathSeparator 
                        + "MANIFEST.MF");
                if (manifestFile.exists()) {
                    InputStream input = null;
                    try {
                        input = new FileInputStream(manifestFile);
                        Manifest manifest = new Manifest(input);
                        if (manifest.getMainAttributes().containsKey(
                                ManifestBasedBundle.SYMBOLIC_NAME_ENTRY)) {
                            bundles.add(new ManifestBasedBundle(manifest, 
                                    f.getAbsolutePath()));
                        }
                    } catch(IOException e) {
                        BundlePlugin.getDefault().getLog().log(new Status(IStatus.ERROR, 
                                BundlePlugin.PLUGIN_ID, e.getMessage(), e));
                    } finally {
                        if (input != null) {
                            try {
                                input.close();
                            } catch (IOException e1) {
                            }
                        }
                    }
                } else if (lookInSubFolders) {
                    bundles.addAll(internalFindInDirectory(f, true, ignore));
                }
            }
        }
        return bundles;
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
    public void install(BundleInfo bundle) {
        FrameworkAdmin framework = (FrameworkAdmin) _framework.getService();
        BundlesState state = framework.getRunningManipulator().getBundlesState();
        state.installBundle(bundle);
        state.resolve(true);
    }
    
    public void install(String location) throws BundleException {
        install(new BundleInfo(location));
    }
    
    
}