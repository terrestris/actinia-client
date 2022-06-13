package de.terrestris.actinia;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class Location {

  private final String name;
  private final ActiniaClient client;

  private Map<String, Mapset> mapsets;

  public Location(String name, ActiniaClient client) {
    this.name = name;
    this.client = client;
  }

  public String getName() {
    return name;
  }

  public List<Mapset> getMapsets() {
    if (mapsets != null) {
      return new ArrayList<>(mapsets.values());
    }
    List<Mapset> list = client.getMapsets(name);
    mapsets = new HashMap<>();
    list.forEach(m -> mapsets.put(m.getName(), m));
    return new ArrayList<>(mapsets.values());
  }

  public Mapset getMapset(String name) {
    if (mapsets == null) {
      getMapsets();
    }
    return mapsets.get(name);
  }

  @Override
  public String toString() {
    return "Location " + name;
  }

}
