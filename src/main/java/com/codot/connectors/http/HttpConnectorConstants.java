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

    public static final String TRUSTED_STORE_TYPE = "trustedStoreType";
    public static final String TRUSTED_STORE_PATH = "trustedStorePath";
    public static final String TRUSTED_STORE_PASS = "trustedStorePass";

    public static final String KEY_STORE_TYPE = "keyStoreType";
    public static final String KEY_STORE_PATH = "keyStorePath";
    public static final String KEY_STORE_PASS = "keyStorePass";

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

    // OUTPUT
    public static final String OUTPUT_STATUS_CODE = "statusCode";
    public static final String OUTPUT_RESPONSE_TYPE = "responseType";
    public static final String OUTPUT_RESPONSE = "response";
    public static final String OUTPUT_HEADERS = "headers";

    // RESPONSE TYPE
    public static final String OUTPUT_RESPONSE_TYPE_FILE = "file";
    public static final String OUTPUT_RESPONSE_TYPE_JSON = "json";
    public static final String OUTPUT_RESPONSE_TYPE_XML = "xml";
}
