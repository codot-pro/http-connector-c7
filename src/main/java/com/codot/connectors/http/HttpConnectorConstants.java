package com.codot.connectors.http;

public class HttpConnectorConstants {
    // INPUT
    public static final String METHOD = "method";
    public static final String URL = "url";
    public static final String HEADERS = "headers";
    public static final String TIMEOUT = "timeout";
    public static final String PAYLOAD = "payload";
    public static final String RESPONSE_FILE_NAME = "responseFileName";
    public static final String DEBUG_MODE = "debugMode";

    public static final String SSL_TYPE = "sslType";
    public static final String TLS_SETTINGS = "tlsSettings";

    public static final String TLS_STORE_TYPE = "type";
    public static final String TLS_STORE_PATH = "path";
    public static final String TLS_STORE_PASSWORD = "password";

    public static final String TLS_KEY_STORE = "keyStore";
    public static final String TLS_TRUSTED_STORE = "trustedStore";

    // PAYLOAD_TYPE values
    public static final String PAYLOAD_TYPE_TEXT = "text";
    public static final String PAYLOAD_TYPE_MULTIPART = "multipart";
    public static final String PAYLOAD_TYPE_BINARY = "binary";
    public static final String PAYLOAD_TYPE_EMPTY = "empty";

    // PAYLOAD KEYS
    public static final String PAYLOAD_KEY_TYPE = "type";
    public static final String PAYLOAD_KEY_TEXT = "text";
    public static final String PAYLOAD_KEY_FILE_NAME = "fileName";
    public static final String PAYLOAD_KEY_FILE_PATH = "filePath";
    public static final String PAYLOAD_KEY_DELETE = "delete";
    public static final String PAYLOAD_MULTIPART = "parts";
    public static final String PAYLOAD_MULTIPART_KEY = "key";
    public static final String PAYLOAD_MULTIPART_TYPE = "type";
    public static final String PAYLOAD_MULTIPART_TEXT = "text";
    public static final String PAYLOAD_MULTIPART_FILE = "file";
    public static final String PAYLOAD_MULTIPART_FILE_NAME = "fileName";
    public static final String PAYLOAD_MULTIPART_FILE_PATH = "filePath";
    public static final String PAYLOAD_MULTIPART_DELETE = "delete";

    // SSL_TYPE values
    public static final String SSL_TYPE_ENABLED = "enable";
    public static final String SSL_TYPE_DISABLE = "disable";
    public static final String SSL_TYPE_TWO_WAY_SSL = "2waySsl";

    // RESPONSE TYPE
    public static final String OUTPUT_RESPONSE_TYPE_FILE = "file";
    public static final String OUTPUT_RESPONSE_TYPE_JSON = "json";
    public static final String OUTPUT_RESPONSE_TYPE_XML = "xml";

    // OUTPUT VARIABLES
    public static final String OUTPUT_VARIABLE_STATUS_CODE = "outputStatusCode";
    public static final String OUTPUT_VARIABLE_RESPONSE_TYPE = "outputResponseType";
    public static final String OUTPUT_VARIABLE_RESPONSE = "outputResponse";
    public static final String OUTPUT_VARIABLE_HEADERS = "outputHeaders";
}
