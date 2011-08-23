package org.osmsurround.ra.export;

import java.util.ArrayList;
import java.util.List;

import org.osmsurround.ra.context.AnalyzerContext;
import org.osmsurround.ra.data.Node;
import org.osmsurround.ra.dijkstra.DijkstraAlgorithm;
import org.osmsurround.ra.dijkstra.Vertex;
import org.osmsurround.ra.segment.ConnectableSegment;
import org.osmsurround.ra.web.IntersectionNode;
import org.osmsurround.ra.web.IntersectionWeb;
import org.springframework.stereotype.Service;

@Service
public class TraverseService {

	public List<Node> traverse(IntersectionNode startNode, IntersectionNode endNode) {
		SingleRouteTraverser traverser = new SingleRouteTraverser(startNode, endNode);
		return traverser.getNodes();
	}

	public List<Node> traverse(AnalyzerContext analyzerContext, IntersectionNode startNode, IntersectionNode endNode) {

		IntersectionWeb intersectionWeb = analyzerContext.getGraphs().get(0);
		DijkstraAlgorithm dijkstraAlgorithm = new DijkstraAlgorithm(intersectionWeb);
		dijkstraAlgorithm.execute(startNode);
		List<Vertex> path = dijkstraAlgorithm.getPath(endNode);

		List<Node> result = new ArrayList<Node>();

		if (!path.isEmpty()) {
			Vertex startVertex = path.get(0);
			result.add(startVertex.getNode());
			for (int x = 1; x < path.size(); x++) {

				Vertex currentVertex = path.get(x);

				for (ConnectableSegment connectableSegment : analyzerContext.getSegments()) {
					if (connectableSegment.containsNodes(startVertex.getNode(), currentVertex.getNode())) {
						connectableSegment.appendNodesBetween(result, startVertex.getNode(), currentVertex.getNode());
						break;
					}
				}

				startVertex = currentVertex;

			}
		}
		return result;
	}
}
