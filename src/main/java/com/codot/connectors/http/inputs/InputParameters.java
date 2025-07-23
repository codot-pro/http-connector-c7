package com.codot.connectors.http.inputs;

import java.util.Properties;

public interface InputParameters {
    Properties getRequestProperties();
    Properties getSslProperties();
}
