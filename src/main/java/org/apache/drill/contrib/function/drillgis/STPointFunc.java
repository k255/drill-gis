package org.apache.drill.contrib.function.drillgis;

import javax.inject.Inject;

import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.Float8Holder;
import org.apache.drill.exec.expr.holders.VarBinaryHolder;

import io.netty.buffer.DrillBuf;

@FunctionTemplate(name = "st_point", scope = FunctionTemplate.FunctionScope.SIMPLE, nulls = FunctionTemplate.NullHandling.NULL_IF_NULL)
public class STPointFunc implements DrillSimpleFunc {
	@Param
	Float8Holder lonParam;

	@Param
	Float8Holder latParam;

	@Output
	VarBinaryHolder out;

	@Inject
	DrillBuf buffer;

	public void setup() {
	}

	public void eval() {

		double lon = lonParam.value;
		double lat = latParam.value;

		com.esri.core.geometry.ogc.OGCPoint point = new com.esri.core.geometry.ogc.OGCPoint(
				new com.esri.core.geometry.Point(lon, lat), com.esri.core.geometry.SpatialReference.create(4326));

		// System.out.println(wkbPoint.toString());
		java.nio.ByteBuffer pointBytes = point.asBinary();
		out.buffer =  buffer;
		out.start = 0;
		out.end = pointBytes.remaining();
		buffer.setBytes(0, pointBytes);
	}
}
