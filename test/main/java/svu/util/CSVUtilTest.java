package svu.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class CSVUtilTest {

	@Test
	public void testNormalize() {
		
		double[][] M = new double[][]{
				{ 0.4, 0.5, 0.6 },
				{ 0.4, 0.6, 0.7 },
				{ 0.5, 0.7, 0.8 },
				{ 0.5, 0.5, 0.9 }
		};
		
		double[][] v = CSVUtil.normalize(M);
		
		assertEquals(0.0, v[0][0], 0.1);
		assertEquals(0.0, v[1][0], 0.1);
		assertEquals(1.0, v[2][0], 0.1);
		assertEquals(1.0, v[3][0], 0.1);

		assertEquals(0.0, v[0][1], 0.1);
		assertEquals(0.5, v[1][1], 0.1);
		assertEquals(1.0, v[2][1], 0.1);
		assertEquals(0.0, v[3][1], 0.1);
	
		assertEquals(0.0, v[0][2], 0.1);
		assertEquals(0.3, v[1][2], 0.1);
		assertEquals(0.7, v[2][2], 0.1);
		assertEquals(1.0, v[3][2], 0.1);
		
		assertEquals(M[0][0], v[0][0], 0.0001);
	}

}
