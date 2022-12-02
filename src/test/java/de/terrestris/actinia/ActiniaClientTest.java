package de.terrestris.actinia;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActiniaClientTest {

  public static final String ACTINIA_USER = System.getenv("ACTINIA_USER");

  public static final String ACTINIA_PASSWORD = System.getenv("ACTINIA_PASSWORD");

  private final ActiniaClient client1 = new ActiniaClient("https://actinia.mundialis.de/", ACTINIA_USER, ACTINIA_PASSWORD);

  private final ActiniaClient client2 = new ActiniaClient("https://actinia.mundialis.de", ACTINIA_USER, ACTINIA_PASSWORD);

  @Test
  public void testGettingLocations() {
    List<Location> locations = client1.getLocations();
    Assertions.assertFalse(locations.isEmpty());
  }

  @Test
  public void testTrailingSlash() {
    List<Location> locations1 = client1.getLocations();
    List<Location> locations2 = client2.getLocations();
    Assertions.assertEquals(locations1.size(), locations2.size());
  }

  @Test
  public void testGettingMapsets() {
    Location loc = client1.getLocation("nc_spm_08");
    List<Mapset> mapsets = loc.getMapsets();
    Assertions.assertFalse(mapsets.isEmpty());
  }

  @Test
  public void testGettingRasterLayers() {
    List<String> rasterLayers = client1.getLocation("nc_spm_08").getMapset("PERMANENT").getRasterLayers();
    Assertions.assertFalse(rasterLayers.isEmpty());
  }

  @Test
  public void testGettingSpaceTimeRasterDatasets() {
    List<String> datasets = client1.getLocation("nc_spm_08").getMapset("modis_lst").getSpaceTimeRasterDatasets();
    Assertions.assertFalse(datasets.isEmpty());
  }

  @Test
  public void testGettingModules() {
    List<Module> modules = client1.getModules();
    Assertions.assertFalse(modules.isEmpty());
  }

  @Test void testGettingParameters() {
    client1.getModules();
    Module module = client1.getModule("r.rescale");
    List<Parameter> params = module.getInputParameters();
    Assertions.assertFalse(params.isEmpty());
  }

  @Test void testGettingOutputs() {
    client1.getModules();
    Module module = client1.getModule("r.rescale");
    List<Parameter> params = module.getOutputParameters();
    Assertions.assertFalse(params.isEmpty());
  }

  @Test
  public void testExecuteChain() {
    Module region = client1.getModule("g.region");
    Module ivi = client1.getModule("i.vi");
    Map<String, String> regionParams = new HashMap<>();
    Map<String, String> iviParams = new HashMap<>();
    regionParams.put("raster", "lsat7_2000_50@landsat,lsat7_2000_61@landsat");
    iviParams.put("red", "lsat7_2000_50@landsat");
    iviParams.put("nir", "lsat7_2000_61@landsat");
    iviParams.put("iviname", "ndvi");
    iviParams.put("output", "astest");
    List<Module> modules = List.of(region, ivi);
    List<Map<String, String>> params = List.of(regionParams, iviParams);
    ProcessStatus status = client1.runProcess("nc_spm_08", "astest", modules, params);
    status.update();
    Assertions.assertTrue(status.getStatus().equals("accepted") || status.getStatus().equals("finished"));
  }

}
