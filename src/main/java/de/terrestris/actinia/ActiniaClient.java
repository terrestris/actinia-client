package de.terrestris.actinia;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class ActiniaClient {

  private final String url;

  private final HttpClient client;

  private final Map<String, Location> locations = new HashMap<>();

  private final Map<String, Module> modules = new HashMap<>();

  public ActiniaClient(String url, String username, String password) {
    this.url = url.endsWith("/") ? url : url + "/";
    client = HttpClient.newBuilder().authenticator(new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password.toCharArray());
      }
    }).build();
  }

  public List<Location> getLocations() {
    HttpRequest request;
    try {
      request = HttpRequest.newBuilder(new URI(String.format("%slatest/locations", url))).build();
      ObjectMapper mapper = new ObjectMapper();
      HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
      JsonNode node = mapper.readTree(response.body());
      if (!node.get("status").asText().equals("success")) {
        throw new ActiniaException("Getting the locations was unsuccessful.");
      }
      node = node.get("locations");
      if (node.isArray()) {
        List<Location> list = new ArrayList<>();
        for (JsonNode jsonNode : node) {
          Location l;
          list.add(l = new Location(jsonNode.asText(), this));
          this.locations.put(l.getName(), l);
        }
        return list;
      }
      throw new ActiniaException("Location list was not a list.");
    } catch (URISyntaxException | IOException | InterruptedException e) {
      log.warn("Unable to get locations: {}", e.getMessage());
      log.trace("Stack trace:", e);
      throw new ActiniaException("Unable to get locations.", e);
    }
  }

  public List<Mapset> getMapsets(String location) {
    Location l = locations.get(location);
    HttpRequest request;
    try {
      request = HttpRequest.newBuilder(new URI(String.format("%slatest/locations/%s/mapsets", url, location))).build();
      ObjectMapper mapper = new ObjectMapper();
      HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
      JsonNode node = mapper.readTree(response.body());
      node = node.get("process_results");
      List<Mapset> mapsets = new ArrayList<>();
      for (JsonNode n : node) {
        mapsets.add(new Mapset(n.asText(), this, l));
      }
      return mapsets;
    } catch (URISyntaxException | IOException | InterruptedException e) {
      log.warn("Unable to get mapsets for {}: {}", location, e.getMessage());
      log.trace("Stack trace:", e);
      throw new ActiniaException("Unable to get mapsets for " + location, e);
    }
  }

  public List<String> getRasterLayers(String location, String mapset) {
    HttpRequest request;
    try {
      request = HttpRequest.newBuilder(new URI(String.format("%slatest/locations/%s/mapsets/%s/raster_layers", url, location, mapset))).build();
      ObjectMapper mapper = new ObjectMapper();
      HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
      JsonNode node = mapper.readTree(response.body());
      node = node.get("process_results");
      List<String> layers = new ArrayList<>();
      for (JsonNode n : node) {
        layers.add(n.asText());
      }
      return layers;
    } catch (URISyntaxException | IOException | InterruptedException e) {
      log.warn("Unable to get raster layers for {} and {}: {}", location, mapset, e.getMessage());
      log.trace("Stack trace:", e);
      throw new ActiniaException("Unable to get raster layers for " + location + " and " + mapset, e);
    }
  }

  public List<String> getSpaceTimeRasterDatasets(String location, String mapset) {
    HttpRequest request;
    try {
      request = HttpRequest.newBuilder(new URI(String.format("%slatest/locations/%s/mapsets/%s/strds", url, location, mapset))).build();
      ObjectMapper mapper = new ObjectMapper();
      HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
      JsonNode node = mapper.readTree(response.body());
      node = node.get("process_results");
      List<String> datasets = new ArrayList<>();
      for (JsonNode n : node) {
        datasets.add(n.asText());
      }
      return datasets;
    } catch (URISyntaxException | IOException | InterruptedException e) {
      log.warn("Unable to get space time datasets for {} and {}: {}", location, mapset, e.getMessage());
      log.trace("Stack trace:", e);
      throw new ActiniaException("Unable to get space time datasets for " + location + " and " + mapset, e);
    }
  }

  public Location getLocation(String name) {
    if (locations.size() == 0) {
      getLocations();
    }
    return locations.get(name);
  }

  public List<Module> getModules() {
    if (!this.modules.isEmpty()) {
      return new ArrayList<>(this.modules.values());
    }
    HttpRequest request;
    try {
      request = HttpRequest.newBuilder(new URI(String.format("%slatest/modules", url))).build();
      ObjectMapper mapper = new ObjectMapper();
      HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
      JsonNode node = mapper.readTree(response.body());
      if (!node.get("status").asText().equals("success")) {
        throw new ActiniaException("Getting the modules was unsuccessful.");
      }
      node = node.get("processes");
      for (JsonNode n : node) {
        Module module = new Module(n.get("id").asText(), n.get("description").asText(), this);
        this.modules.put(module.getName(), module);
      }
      return new ArrayList<>(this.modules.values());
    } catch (URISyntaxException | IOException | InterruptedException e) {
      log.warn("Unable to get modules: {}", e.getMessage());
      log.trace("Stack trace:", e);
      throw new ActiniaException("Unable to get modules", e);
    }
  }

  public Module getModule(String name) {
    if (modules.isEmpty()) {
      getModules();
    }
    return modules.get(name);
  }

  public void updateDetails(Module module) {
    HttpRequest request;
    try {
      request = HttpRequest.newBuilder(new URI(String.format("%slatest/modules/%s", url, module.getName()))).build();
      ObjectMapper mapper = new ObjectMapper();
      HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
      JsonNode node = mapper.readTree(response.body());
      JsonNode params = node.get("parameters");
      for (JsonNode n : params) {
        module.addInputParameter(new Parameter(n));
      }
      JsonNode outputs = node.get("returns");
      for (JsonNode n : outputs) {
        module.addOutputParameter(new Parameter(n));
      }

    } catch (URISyntaxException | IOException | InterruptedException e) {
      log.warn("Unable to update module details for {}: {}", module.getName(), e.getMessage());
      log.trace("Stack trace:", e);
      throw new ActiniaException("Unable to update module details for " + module.getName(), e);
    }
  }

}
