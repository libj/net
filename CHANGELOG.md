# Changes by Version

## [v0.5.3-SNAPSHOT](https://github.com/libj/net/compare/01c48784915d7e10e0f7b207f5408309494d9e5e..HEAD)

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

## v0.5.0 (2019-05-13)
* Initial public release.