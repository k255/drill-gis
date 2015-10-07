package org.apache.drill.contrib.function.drillgis;

import javax.inject.Inject;

import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.Float8Holder;
import org.apache.drill.exec.expr.holders.VarBinaryHolder;

import io.netty.buffer.DrillBuf;

@FunctionTemplate(name = "st_buffer", scope = FunctionTemplate.FunctionScope.SIMPLE, nulls = FunctionTemplate.NullHandling.NULL_IF_NULL)
public class STBuffer implements DrillSimpleFunc {
	@Param
	VarBinaryHolder geom1Param;

	@Param(constant = true)
	Float8Holder bufferRadiusParam;

	@Output
	VarBinaryHolder out;

	@Inject
	DrillBuf buffer;

	public void setup() {
	}

	public void eval() {
		double bufferRadius = bufferRadiusParam.value;

		com.esri.core.geometry.ogc.OGCGeometry geom1 = com.esri.core.geometry.ogc.OGCGeometry
				.fromBinary(geom1Param.buffer.nioBuffer(geom1Param.start, geom1Param.end));

		com.esri.core.geometry.ogc.OGCGeometry bufferedGeom = geom1.buffer(bufferRadius);
		System.out.println("test" + bufferedGeom.asText());
		java.nio.ByteBuffer bufferedGeomBytes = geom1.asBinary();
		out.buffer = buffer;
		out.start = 0;
		out.end = bufferedGeomBytes.remaining();;
		buffer.setBytes(0, bufferedGeomBytes);
	}
}
