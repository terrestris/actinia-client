package de.terrestris.actinia;

/**
 * This class can be used to monitor a running process.
 */
public class ProcessStatus {

  private final String url;

  private final ActiniaClient client;

  private String status;

  public ProcessStatus(String url, ActiniaClient client) {
    this.url = url;
    this.client = client;
  }

  public String getUrl() {
    return url;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * The status of the process in actinia, 'accepted', 'error' or 'finished'.
   */
  public String getStatus() {
    return status;
  }

  /**
   * Use this to check for updates of the process status.
   */
  public void update() {
    client.updateStatus(this);
  }

}
