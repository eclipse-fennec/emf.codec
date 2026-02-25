/********************************************************************
 * Copyright (c) 2026 Contributors to the Eclipse Foundation.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Data In Motion Consulting - initial implementation
 ********************************************************************/
package org.eclipse.fennec.model.metadata.service;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.fennec.model.metadata.api.AspectProvider;
import org.eclipse.fennec.model.metadata.api.MetadataHandler;
import org.eclipse.fennec.model.metadata.api.MetadataIndex;
import org.eclipse.fennec.model.metadata.api.MetadataService;
import org.eclipse.fennec.model.metadata.api.MetadataWhiteboard;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

/**
 * OSGi Declarative Services component that exposes {@link MetadataServiceImpl}
 * as a whiteboard service.
 * <p>
 * Registered as both {@link MetadataWhiteboard} (lifecycle management) and
 * {@link MetadataService} (read-only consumer access).
 * </p>
 *
 * <h3>Automatic whiteboard behaviour</h3>
 * <ul>
 *   <li><b>EPackage services</b> — any bundle that registers an {@link EPackage}
 *       as an OSGi service (via the generated {@code *ConfigurationComponent}) is
 *       automatically picked up and passed to {@link MetadataServiceImpl#registerPackage}.
 *       Late-arriving EPackages are still handled because the reference policy is
 *       {@code DYNAMIC}.</li>
 *   <li><b>AspectProvider services</b> — any {@link AspectProvider} registered as
 *       an OSGi service (e.g., {@code CodecAspectProviderComponent}) is automatically
 *       applied to all currently registered packages and to any packages that arrive
 *       afterwards.</li>
 *   <li><b>MetadataIndex</b> — optional; if no index service is present the
 *       built-in {@link MapBasedMetadataIndex} (created by the default constructor)
 *       remains active.</li>
 *   <li><b>MetadataHandler services</b> — any {@link MetadataHandler} registered as
 *       an OSGi service receives lifecycle callbacks for package registration /
 *       unregistration.</li>
 * </ul>
 *
 * @author Data In Motion Consulting
 * @since 2026
 */
@Component(
        name = "MetadataServiceComponent",
        service = { MetadataWhiteboard.class, MetadataService.class },
        immediate = true
)
public class MetadataServiceComponent extends MetadataServiceImpl {

    // -------------------------------------------------------------------------
    // EPackage whiteboard
    // -------------------------------------------------------------------------

    /**
     * Bind: called by DS when an {@link EPackage} OSGi service appears.
     */
    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removeEPackage"
    )
    void addEPackage(EPackage ePackage) {
        registerPackage(ePackage);
    }

    /**
     * Unbind: called by DS when an {@link EPackage} OSGi service disappears.
     */
    void removeEPackage(EPackage ePackage) {
        unregisterPackage(ePackage);
    }

    // -------------------------------------------------------------------------
    // AspectProvider whiteboard
    // -------------------------------------------------------------------------

    /**
     * Bind: called by DS when an {@link AspectProvider} OSGi service appears.
     * Late-arriving providers are applied retroactively to all already-registered
     * packages by {@link MetadataServiceImpl#registerAspectProvider}.
     */
    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removeAspectProvider"
    )
    void addAspectProvider(AspectProvider provider) {
        registerAspectProvider(provider);
    }

    /**
     * Unbind: called by DS when an {@link AspectProvider} OSGi service disappears.
     * Removes all aspects and profiles contributed by that provider.
     */
    void removeAspectProvider(AspectProvider provider) {
        unregisterAspectProvider(provider);
    }

    // -------------------------------------------------------------------------
    // MetadataIndex (optional, swappable)
    // -------------------------------------------------------------------------

    /**
     * Bind: called by DS when a {@link MetadataIndex} OSGi service appears.
     * Replaces the default {@link MapBasedMetadataIndex} and re-indexes all
     * existing packages into the new index.
     */
    @Reference(
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetIndex"
    )
    void setIndex(MetadataIndex index) {
        setMetadataIndex(index);
    }

    /**
     * Unbind: called by DS when the {@link MetadataIndex} OSGi service disappears.
     * Clears and removes the index; fast lookups are unavailable until a new index
     * is bound.
     */
    void unsetIndex(MetadataIndex index) {
        unsetMetadataIndex(index);
    }

    // -------------------------------------------------------------------------
    // MetadataHandler whiteboard
    // -------------------------------------------------------------------------

    /**
     * Bind: called by DS when a {@link MetadataHandler} OSGi service appears.
     * Late-arriving handlers receive {@code onPackageRegistered} for all packages
     * that are already registered.
     */
    @Reference(
            cardinality = ReferenceCardinality.MULTIPLE,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "removeHandler"
    )
    void addHandler(MetadataHandler handler) {
        addMetadataHandler(handler);
    }

    /**
     * Unbind: called by DS when a {@link MetadataHandler} OSGi service disappears.
     * Calls {@link MetadataHandler#clear()} before removing it.
     */
    void removeHandler(MetadataHandler handler) {
        removeMetadataHandler(handler);
    }
}
