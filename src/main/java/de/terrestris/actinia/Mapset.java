package de.terrestris.actinia;

import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class Mapset {

  private final String name;
  private final ActiniaClient client;
  private final Location location;
  private List<String> rasterLayers;
  private List<String> spaceTimeRasterDatasets;

  public Mapset(String name, ActiniaClient client, Location location) {
    this.name = name;
    this.client = client;
    this.location = location;
  }

  public String getName() {
    return name;
  }

  public Location getLocation() {
    return location;
  }

  public List<String> getRasterLayers() {
    if (rasterLayers != null) {
      return rasterLayers;
    }
    return rasterLayers = client.getRasterLayers(location.getName(), name);
  }

  public List<String> getSpaceTimeRasterDatasets() {
    if (spaceTimeRasterDatasets != null) {
      return spaceTimeRasterDatasets;
    }
    return spaceTimeRasterDatasets = client.getSpaceTimeRasterDatasets(location.getName(), name);
  }

  @Override
  public String toString() {
    return "Mapset " + name;
  }

}
