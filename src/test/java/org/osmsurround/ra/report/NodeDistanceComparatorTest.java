package org.osmsurround.ra.report;

import static org.junit.Assert.*;

import org.junit.Test;
import org.osmtools.ra.data.Node;

public class NodeDistanceComparatorTest {

	@Test
	public void testCompare() throws Exception {

		NodeDistance nd1 = new NodeDistance(new Node(2, 0, 0), 0.01);
		NodeDistance nd2 = new NodeDistance(new Node(1, 0, 0), 0.02);

		NodeDistanceComparator comparator = new NodeDistanceComparator();

		assertTrue(comparator.compare(nd1, nd2) < 0);
		assertTrue(comparator.compare(nd2, nd1) > 0);
		assertTrue(comparator.compare(nd1, nd1) == 0);

	}
}
