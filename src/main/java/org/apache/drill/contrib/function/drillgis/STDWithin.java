package org.apache.drill.contrib.function.drillgis;

import javax.inject.Inject;

import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.BitHolder;
import org.apache.drill.exec.expr.holders.Float8Holder;
import org.apache.drill.exec.expr.holders.VarBinaryHolder;

import io.netty.buffer.DrillBuf;

@FunctionTemplate(name = "st_dwithin", scope = FunctionTemplate.FunctionScope.SIMPLE, nulls = FunctionTemplate.NullHandling.NULL_IF_NULL)
public class STDWithin implements DrillSimpleFunc {
	@Param
	VarBinaryHolder geom1Param;

	@Param
	VarBinaryHolder geom2Param;

	@Param(constant = true)
	Float8Holder distanceParam;

	@Output
	BitHolder out;

	@Inject
	DrillBuf buffer;

	public void setup() {
	}

	public void eval() {
		double distance = distanceParam.value;

		byte[] geom1WKB = new byte[geom1Param.end + 1];
		byte[] geom2WKB = new byte[geom2Param.end + 1];
		geom1Param.buffer.getBytes(geom1Param.start, geom1WKB);
		geom2Param.buffer.getBytes(geom2Param.start, geom2WKB);

		com.vividsolutions.jts.geom.Geometry geom1;
		com.vividsolutions.jts.geom.Geometry geom2;

		try {
			geom1 = new com.vividsolutions.jts.io.WKBReader().read(geom1WKB);
			geom2 = new com.vividsolutions.jts.io.WKBReader().read(geom2WKB);
			//System.out.println("distance: " + geom1.distance(geom2));

			boolean isWithin = com.vividsolutions.jts.operation.distance.DistanceOp.isWithinDistance(geom1, geom2,
					distance);

			out.value = isWithin ? 1 : 0;
		} catch (com.vividsolutions.jts.io.ParseException e) {
			e.printStackTrace();
		}
	}
}
