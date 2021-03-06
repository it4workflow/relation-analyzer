package org.osmsurround.ra.elevation;

import java.util.ArrayList;
import java.util.List;

import org.osmsurround.ra.export.ExportService;
import org.osmtools.api.LonLat;
import org.osmtools.api.Section;
import org.osmtools.ra.context.AnalyzerContext;
import org.osmtools.srtm.SrtmService;
import org.osmtools.utils.LonLatMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ElevationService {

	private static final int VOID = -32768;

	private Logger log = LoggerFactory.getLogger(getClass());

	@Value("${srtmDataDirectory}")
	private String srtmDataDirectory;

	@Autowired
	private ExportService exportService;

	private SrtmService srtmService;

	public void assertSrtmService() {
		if (srtmService == null) {
			srtmService = new SrtmService();
			if (srtmDataDirectory != null) {
				log.info("Scanning dir for Srtm Data... " + srtmDataDirectory);
				srtmService.scanDirectory(srtmDataDirectory);
			}
		}
	}

	public List<double[]> createElevationProfile(AnalyzerContext context) {
		assertSrtmService();
		List<double[]> list = new ArrayList<double[]>();
		try {
			List<Section> sections = exportService.convertToSections(context);
			if (!sections.isEmpty()) {

				Section section = sections.get(0);
				if (!section.getCoordinateLists().isEmpty()) {

					double length = 0;
					LonLat lastLonLat = null;

					for (LonLat lonLat : section.getCoordinateLists().iterator().next()) {

						double lon = lonLat.getLon();
						double lat = lonLat.getLat();

						int height = srtmService.getElevation(lon, lat);
						if (height != VOID) {

							if (lastLonLat != null) {
								double distance = LonLatMath.distance(lastLonLat.getLon(), lastLonLat.getLat(),
										lonLat.getLon(), lonLat.getLat());
								length += distance;
								list.add(new double[] { length, height });
							}
							lastLonLat = lonLat;
						}
					}
				}
			}
		}
		catch (Exception e) {
		}
		return list;
	}
}
