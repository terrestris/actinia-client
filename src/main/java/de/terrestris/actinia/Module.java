package de.terrestris.actinia;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class Module {

  private final String name;

  private final ActiniaClient client;

  private final String description;

  private List<Parameter> inputParameters;

  private List<Parameter> outputParameters;

  public Module(String name, String description, ActiniaClient client) {
    this.name = name;
    this.description = description;
    this.client = client;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<Parameter> getInputParameters() {
    if (inputParameters == null) {
      client.updateDetails(this);
    }
    return inputParameters;
  }

  public List<Parameter> getOutputParameters() {
    if (outputParameters == null) {
      client.updateDetails(this);
    }
    return outputParameters;
  }

  public void addInputParameter(Parameter parameter) {
    if (inputParameters == null) {
      inputParameters = new ArrayList<>();
    }
    inputParameters.add(parameter);
  }

  public void addOutputParameter(Parameter parameter) {
    if (outputParameters == null) {
      outputParameters = new ArrayList<>();
    }
    outputParameters.add(parameter);
  }

  @Override
  public String toString() {
    return name;
  }

}
