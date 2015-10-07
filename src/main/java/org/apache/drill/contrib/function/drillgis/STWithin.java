package org.apache.drill.contrib.function.drillgis;

import javax.inject.Inject;

import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.BitHolder;
import org.apache.drill.exec.expr.holders.VarBinaryHolder;

import io.netty.buffer.DrillBuf;

@FunctionTemplate(name = "st_within", scope = FunctionTemplate.FunctionScope.SIMPLE, nulls = FunctionTemplate.NullHandling.NULL_IF_NULL)
public class STWithin implements DrillSimpleFunc {
	@Param
	VarBinaryHolder geom1Param;

	@Param
	VarBinaryHolder geom2Param;

	@Output
	BitHolder out;

	@Inject
	DrillBuf buffer;

	public void setup() {
	}

	public void eval() {
		com.esri.core.geometry.ogc.OGCGeometry geom1;
		com.esri.core.geometry.ogc.OGCGeometry geom2;

		geom1 = com.esri.core.geometry.ogc.OGCGeometry.fromBinary(geom1Param.buffer.nioBuffer(geom1Param.start, geom1Param.end));
		geom2 = com.esri.core.geometry.ogc.OGCGeometry.fromBinary(geom2Param.buffer.nioBuffer(geom2Param.start, geom2Param.end));

		int isWithin = geom1.within(geom2) ? 1 : 0;

		out.value = isWithin;
	}
}
