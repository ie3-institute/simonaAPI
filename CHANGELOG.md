# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased/Snapshot]

### Added
- Implemented `ExtPrimaryData` and `ExtResultsData` [#145](https://github.com/ie3-institute/simonaAPI/issues/145)
-  Have EV simulation communicate the next tick [#170](https://github.com/ie3-institute/simonaAPI/issues/170)

## [0.4.0] - 2023-11-22

### Changed
- Replacing akka with pekko [#138](https://github.com/ie3-institute/simonaAPI/issues/138)

## [0.3.0] - 2023-11-19

### Changed
- Updating to gradle 8.4 [#133](https://github.com/ie3-institute/simonaAPI/issues/133)
- Adapted to changed SIMONA scheduler protocol [#131](https://github.com/ie3-institute/simonaAPI/issues/131)

## [0.2.0] - 2023-08-01

### Added
- Introducing termination message indicating that the main simulation would like to quit [#5](https://github.com/ie3-institute/simonaAPI/issues/5)
- Enabled tests for this project and implemented partial test for `ExtSimulation` as part of [#5](https://github.com/ie3-institute/simonaAPI/issues/5)
- JavaDoc for many message classes [#59](https://github.com/ie3-institute/simonaAPI/issues/59)
- Implemented `ExtEvDataTest` as part of [#77](https://github.com/ie3-institute/simonaAPI/issues/77)

### Changed
- Refactored `ExtTrigger` -> `ExtSimMessage` and `ExtTriggerResponse` -> `ExtSimMessageResponse` [#5](https://github.com/ie3-institute/simonaAPI/issues/5)
- Renamed messages to ease understanding [#62](https://github.com/ie3-institute/simonaAPI/issues/62)
- Separating departures and arrivals in message protocol, properly handling exceptions [#77](https://github.com/ie3-institute/simonaAPI/issues/77)

[Unreleased/Snapshot]: https://github.com/ie3-institute/simonaapi/compare/0.4.0...HEAD
[0.4.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.2.0...0.3.0
[0.2.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/e3f0c247d9d2a92840f49412aa729c5f033cb4de...0.2.0
