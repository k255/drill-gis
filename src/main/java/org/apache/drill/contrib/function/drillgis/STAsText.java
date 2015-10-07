package org.apache.drill.contrib.function.drillgis;

import javax.inject.Inject;

import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.VarBinaryHolder;
import org.apache.drill.exec.expr.holders.VarCharHolder;

import io.netty.buffer.DrillBuf;

@FunctionTemplate(name = "st_astext", scope = FunctionTemplate.FunctionScope.SIMPLE, nulls = FunctionTemplate.NullHandling.NULL_IF_NULL)
public class STAsText implements DrillSimpleFunc {
	@Param
	VarBinaryHolder geom1Param;

	@Output
	VarCharHolder out;

	@Inject
	DrillBuf buffer;

	public void setup() {
	}

	public void eval() {
		com.esri.core.geometry.ogc.OGCGeometry geom1 = com.esri.core.geometry.ogc.OGCGeometry
				.fromBinary(geom1Param.buffer.nioBuffer(geom1Param.start, geom1Param.end));
		String geomWKT = geom1.asText();
		System.out.println(geomWKT);
		out.buffer = buffer;
		out.start = 0;
		out.end = geomWKT.getBytes().length;
		buffer.setBytes(0, geomWKT.getBytes());
	}
}
