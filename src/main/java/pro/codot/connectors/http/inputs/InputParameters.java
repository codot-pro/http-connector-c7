package pro.codot.connectors.http.inputs;

import java.util.Properties;

public interface InputParameters {
    Properties getRequestProperties();
    Properties getSslProperties();
    Integer getTimeout();
    String getExpectedFileName();
    boolean shouldSaveAsFile();
}
