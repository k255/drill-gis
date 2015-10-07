package org.apache.drill.contrib.function.drillgis;

import javax.inject.Inject;

import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.VarBinaryHolder;

import io.netty.buffer.DrillBuf;

@FunctionTemplate(name = "st_union", scope = FunctionTemplate.FunctionScope.SIMPLE, nulls = FunctionTemplate.NullHandling.NULL_IF_NULL)
public class STUnion implements DrillSimpleFunc {
	@Param
	VarBinaryHolder geom1Param;
	
	@Param
	VarBinaryHolder geom2Param;

	@Output
	VarBinaryHolder out;

	@Inject
	DrillBuf buffer;

	public void setup() {
	}

	public void eval() {
		com.esri.core.geometry.ogc.OGCGeometry geom1;
		com.esri.core.geometry.ogc.OGCGeometry geom2;
		geom1 = com.esri.core.geometry.ogc.OGCGeometry.fromBinary(geom1Param.buffer.nioBuffer(geom1Param.start, geom1Param.end));
		geom2 = com.esri.core.geometry.ogc.OGCGeometry.fromBinary(geom1Param.buffer.nioBuffer(geom2Param.start, geom2Param.end));

		com.esri.core.geometry.ogc.OGCGeometry unionGeom = geom1.union(geom2);

		java.nio.ByteBuffer bufferedGeomBytes = unionGeom.asBinary();
		out.buffer =  buffer;
		out.start = 0;
		out.end = bufferedGeomBytes.remaining();
		buffer.setBytes(0, bufferedGeomBytes);
	}
}
