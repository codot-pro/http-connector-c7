# Change Log

## [3.0.2] - 2025-09-10

### Fixed:
- Using HttpConnectorDelegate without the @Component annotation because the Camunda property 'camunda:class' **requires a no-argument constructor**.
- Using WebClientFactoryProvider, ResponseHandler, ResponseHandlerFactory **through static methods**.
- Fixed an issue where **response** fields were **not converted in debug mode**.

## [3.0.1] - 2025-09-09

### Added:
- Optional save raw HTTP response directly to a file without parsing.

### Fixed:
- Optimizing dependencies and building the correct jar.
- Optimized two-way SSL (mutual TLS) handling to **reuse SSL contexts and reduce connection overhead**.

### Security
- Updated **spring-boot-dependencies** to 3.1.12 (latest 3.1.x)
- Updated **junit4** to **junit5**

## [3.0.0] - 2025-07-25

### Added:
- Base HTTP connector for Camunda 7: supports GET/POST/PUT/PATCH/DELETE, multipart, SSL (none/basic/two-way), JSON/XML handling, input/output mapping.

### Fixed:
- Initial stability improvements and error handling.