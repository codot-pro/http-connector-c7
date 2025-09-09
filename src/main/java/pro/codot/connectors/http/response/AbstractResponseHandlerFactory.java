package pro.codot.connectors.http.response;

import lombok.Setter;

@Setter
public abstract class AbstractResponseHandlerFactory implements ResponseHandlerFactory {
    protected String responseType;
    protected Object response;
}
