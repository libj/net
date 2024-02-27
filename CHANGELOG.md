# Changes by Version

## [v0.5.5-SNAPSHOT](https://github.com/libj/util/compare/8cb8305c9e8fbf53ba32cce1c8c838413dbcd4e0..HEAD)

## [v0.5.4](https://github.com/libj/util/compare/8f820b9e9bae3e820a45a5bb310bd4ebe8ae0b52..8cb8305c9e8fbf53ba32cce1c8c838413dbcd4e0) (2024-02-27)
* #12 Implement `ProxyURI`

## [v0.5.3](https://github.com/libj/net/compare/01c48784915d7e10e0f7b207f5408309494d9e5e..8f820b9e9bae3e820a45a5bb310bd4ebe8ae0b52) (2023-09-20)
* #11 Implement `URLConnections.checkFollowRedirect(...)`
* #10 Implement `BufferedServletInputStream`
* #9 Implement `URIs.decodeParameters`
* #8 Add `connectTimeout` and `readTimeout` parameters to `Downloads.downloadFile(...)`
* #7 Upgrade Maven dependencies
* #6 Use NIO for `Downloads`
* #5 Transition to GitHub Actions
* #4 Add SPI mechanism to load `URLStreamHandlerFactory` classes
* #3 Add `ClasspathURLStreamHandler`
* #2 Refactor method names in HTTP to resemble `ClassLoader`
* #1 Rename `getShortName(T)` to `getSimpleName(T)` in `URIs`, and `URLs`

## [v0.5.2](https://github.com/libj/net/compare/3136a63796aec4121b60e2328f5ec2b5093b9eaa..01c48784915d7e10e0f7b207f5408309494d9e5e) (2020-05-23)
* Improve handling of `InvocationTargetException`.
* Add `equals` and `hashCode` to `Basic` and `Bearer`.
* Remove `Not Modified` check in `Downloads.downloadFile(String,File)`.
* Add supplementary utility methods to `URIs`.
* Add `OfflineURLStreamHandler`.
* Improve tests.
* Improve javadocs.

## [v0.5.1](https://github.com/libj/net/compare/62bb6f68821ec189f3466a3f258ad9897e71bb94..3136a63796aec4121b60e2328f5ec2b5093b9eaa) (2019-07-21)
* Fix `NoSuchMethodError` in `URLs` for jdk1.8.
* Add `URLs#create(String)`.
* Upgrade `org.libj:util:0.7.9` to `0.8.0`.

## [v0.5.0](https://github.com/entinae/pom/compare/56fd805048d7311f81e6932b919b58c67dc8e7c8..62bb6f68821ec189f3466a3f258ad9897e71bb94) (2019-05-13)
* Initial public release.