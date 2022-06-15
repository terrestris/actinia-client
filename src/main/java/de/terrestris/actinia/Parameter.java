package de.terrestris.actinia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Parameter {

  private final String name;

  private final String description;

  private final String type;

  private final String defaultValue;

  private final boolean optional;

  private final String schema;

  public Parameter(JsonNode node) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    if (node.has("default")) {
      defaultValue = node.get("default").asText();
    } else {
      defaultValue = null;
    }
    optional = node.get("optional").asBoolean();
    name = node.get("name").asText();
    description = node.get("description").asText();
    type = node.get("schema").get("type").asText();
    schema = mapper.writeValueAsString(node.get("schema"));
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getType() {
    return type;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public boolean isOptional() {
    return optional;
  }

  /**
   * This contains the schema of the parameter as a JSON string, deserialize it if you need the details.
   */
  public String getSchema() {
    return schema;
  }

}
