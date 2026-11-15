package org.eclipse.fennec.codec.jsonschema.v2;

import java.io.FileInputStream;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.fennec.codec.jsonschema.v2.converter.JsonSchemaToEPackageConverter;
import org.junit.jupiter.api.Test;

class TmpOasConvertTest {
	@Test
	void convert() throws Exception {
		String dir = "/tmp/claude-1000/-opt-git-emf-codec/a60adb33-75d9-45c6-8058-250821f71cd6/scratchpad/oas-schemas/";
		for (String[] v : new String[][] { { "3.0", "definitions" }, { "3.1", "$defs" }, { "3.2", "$defs" } }) {
			JsonSchemaToEPackageConverter c = new JsonSchemaToEPackageConverter();
			EPackage pkg;
			try (FileInputStream in = new FileInputStream(dir + "oas-" + v[0] + ".json")) {
				pkg = c.convert(in, v[1]);
			}
			System.out.println("=== " + v[0] + " classifiers=" + pkg.getEClassifiers().size() + " diagnostics=" + c.getDiagnostics().size());
			c.getDiagnostics().stream().limit(40).forEach(d -> System.out.println("  D " + d));
			for (EClassifier cl : pkg.getEClassifiers()) {
				StringBuilder sb = new StringBuilder("  " + cl.eClass().getName() + " " + cl.getName());
				if (cl instanceof EClass ec) {
					sb.append(" super=").append(ec.getESuperTypes().stream().map(EClass::getName).toList());
					for (EStructuralFeature f : ec.getEStructuralFeatures()) sb.append("\n      ").append(f.getName()).append(":").append(f.getEType() == null ? "null" : f.getEType().getName()).append("[").append(f.getLowerBound()).append("..").append(f.getUpperBound()).append("]");
				}
				System.out.println(sb);
			}
			Resource r = new EcoreResourceFactoryImpl().createResource(URI.createFileURI(dir + "oas-" + v[0] + ".ecore"));
			r.getContents().add(pkg);
			try { r.save(Map.of()); } catch (Exception e) { System.out.println("SAVE FAILED " + e); }
		}
	}
}
