/**
 * Copyright (c) 2012 - 2026 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.eclipse.fennec.codec.playground;



import org.eclipse.emf.ecore.EObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsName;
import org.osgi.service.jakartars.whiteboard.propertytypes.JakartarsResource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author ilenia
 * @since Jun 3, 2026
 */
@JakartarsResource()
@JakartarsName("ExporterResource")
@Component(name = "ExporterResource", service = ExporterResource.class, scope = ServiceScope.PROTOTYPE)
@Path("/export")
public class ExporterResource {
	
	@GET
	public Response hello() {
		return Response.ok("Hello Exporter Resource").build();
	}
	
	@POST
	@Consumes("application/json")
	@Produces({
	      "text/csv",
	      "application/x-csv-zip",
	      "application/vnd.oasis.opendocument.spreadsheet",
	      "application/x-rdata",
	      "application/x-rdata-zip",
	      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
	      "application/json",
	      "application/yaml",
	      "text/yaml",
	      "application/bson"
	  })

    public Response export(EObject eObject) {
		return Response.ok(eObject)
		          .header("Content-Disposition", "attachment; filename=\"export\"")
		          .build();
	}
}
