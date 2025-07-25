# Camunda 7 HTTP connector
HTTP Connector for Camunda 7

### Recommended Requirement:
- JDK:   17

## Deploying the HTTP Connector

```bash
git clone https://github.com/codot-pro/http-connector-c7
cd http-connector-c7
mvn clean install
```

After build, we can find .jar in ./target/ folder

## Run

#### Maven dependency
You can create a **Maven** project and add a **dependency** to run the connector

    <dependency>
        <groupId>com.codot.camundaconnectors</groupId>
        <artifactId>modern-http-connector-c7</artifactId>
        <version></version>
    </dependency>

#### Start SpringBootApplication
```bash
mvn exec:java -Dexec.mainClass="com.codot.connectors.http.Main"
```

#### Add .jar to Camunda 7 dir
After build, we can find .jar in ./target/ folder.
Place the .jar with other .jar`s and don't forget to add the missing libraries as needed.

## Connector template

To add a template to Camunda Modeler, you need to open the application
**modeler_root_folder/resources/element-templates** and put inside the template from
**http-connector-c7/element-templates** with the name **modern-http-connector-c7.json**.

Reload the application, and you will be able to assign a template.

### Input data

- Method:
    - GET
    - POST
    - DELETE

- URL

- Headers (json)
- Payload
    - Type 'text'
      ```json
      {
         "type": "text",
         "text": "{ * content * }"
      }
      ```

    - Type 'binary'
      ```json
      {
         "type": "binary",
         "filePath": "path/to/file.txt",
         "delete": true
      }
      ```
      or
      ```json
      {
         "type": "binary",
         "fileName": "file.bin",
         "delete": true
      }
      ```

    - Type 'multipart'
      ```json
      {
         "type": "multipart",
         "parts": [
            {
               "key": "myFile1",
               "type": "file",
               "filePath": "path/to/file.txt",
               "delete": true
            },
            {
               "key": "myFile2",
               "type": "file",
               "fileName": "file.txt",
               "delete": true
            },
            {
               "key": "myKey",
               "type": "text",
               "text": "{ * content * }"
            }
         ]
      }

      ```

- Timeout
    - Integer (ms)
- Response file name
    - Must always be specified
    - If the response is not a file, then the response will be written to the **response body**

### Output data

> It is important to fill in **all fields**
- Status code:
    - 400 - client error (Invalid headers/method)
    - 500 - server error
    - 504 - timeout error
    - other code returned by the server
- Status msg
- Response can be
    - body (if it is possible to parse it)
    - file (it will be written to the TEMP directory with the specified file name)

### Debug mode
If you enable debug mode, then in the logs you will see logs with input and output data. They will be in the format [processDefinitionKey : processDefinitionVersion : currentActivityName : processInstanceId]: { output/input variables...}


