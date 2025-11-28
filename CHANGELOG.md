# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased/Snapshot]

### Added
- Enhanced `ExtEmDataConnection` with `simulateInternal` method [#398](https://github.com/ie3-institute/simonaAPI/issues/398)
- Added API structure to the docs [#405](https://github.com/ie3-institute/simonaAPI/issues/405)

### Changed

### Fixed

## [0.12.0] - 2025-11-20

### Added
- Adding some utility methods [#368](https://github.com/ie3-institute/simonaAPI/issues/368)
- Extending flex option handling in `ExtInputContainer` [#371](https://github.com/ie3-institute/simonaAPI/issues/371)
- Enhancing `ExtInputContainer` with `hasData` methods [#382](https://github.com/ie3-institute/simonaAPI/issues/382)

### Changed
- Changes to sent and received em data [#2366](https://github.com/ie3-institute/simonaAPI/issues/366)
- Changed the PRated in the EvModel to SRated and added cosPhi to the model [#259](https://github.com/ie3-institute/simonaAPI/issues/259)
- Update `Gradle` to 9.2.0 [#381](https://github.com/ie3-institute/simonaAPI/issues/381)
- Update `Gradle` to 9.2.1 [#389](https://github.com/ie3-institute/simonaAPI/issues/389)

### Fixed
- Issues related to em data [#366](https://github.com/ie3-institute/simonaAPI/issues/366)
- Fixed bugs in `ExtEntityMapping` [#373](https://github.com/ie3-institute/simonaAPI/issues/373)
- Fixed changelog entry #366 [#378](https://github.com/ie3-institute/simonaAPI/issues/366)
- Fix considered data in `ExtEntityMapping.getAssets()` [#384](https://github.com/ie3-institute/simonaAPI/issues/384)

## [0.11.0] - 2025-10-23

### Added
- General flex options [#348](https://github.com/ie3-institute/simonaAPI/issues/348)
- Enhanced em set points [#357](https://github.com/ie3-institute/simonaAPI/issues/348)
- Added dedicated flex communication classes [#358](https://github.com/ie3-institute/simonaAPI/issues/348)

### Changed
- Updated the maven central publishing scripts [#339](https://github.com/ie3-institute/simonaAPI/issues/339)
- Refactoring `ExtSimAdapterData` [#347](https://github.com/ie3-institute/simonaAPI/issues/347)
- Enhanced `ExtEntityMapping` [#355](https://github.com/ie3-institute/simonaAPI/issues/355)

## [0.10.0] - 2025-09-10

### Added
- Added support for external em communication [#304](https://github.com/ie3-institute/simonaAPI/issues/304)

### Changed
- Removed Jenkinsfile [#290](https://github.com/ie3-institute/simonaAPI/issues/290)
- Adapted dependabot workflow and added CODEOWNERS [#294](https://github.com/ie3-institute/simonaAPI/issues/294)
- Refactoring external data connection [#267](https://github.com/ie3-institute/simonaAPI/issues/267)
- Refactoring data containers [#268](https://github.com/ie3-institute/simonaAPI/issues/268)
- Refactoring messages [#269](https://github.com/ie3-institute/simonaAPI/issues/269)
- Refactoring models [#270](https://github.com/ie3-institute/simonaAPI/issues/270)
- Refactoring external entity mapping [#314](https://github.com/ie3-institute/simonaAPI/issues/314)
- Refactoring and simplifying ext em communication [#323](https://github.com/ie3-institute/simonaAPI/issues/323)
- Refactoring result handling [#325](https://github.com/ie3-institute/simonaAPI/issues/325)

### Updates
- Updated java to version 21 [#326](https://github.com/ie3-institute/simonaAPI/issues/326)

## [0.9.0] - 2025-05-09

### Changed
- Updated to `scala3` [#251](https://github.com/ie3-institute/simonaAPI/issues/251)

### Updates
- Updated PSU to 3.1.0
- Updated PSDM to 7.0.0

## [0.8.0] - 2025-04-17

### Added
- Implementing auto-merge for dependabot PRs [#273](https://github.com/ie3-institute/simonaAPI/issues/273)
- Implemented GitHub Actions pipeline [#247](https://github.com/ie3-institute/simonaAPI/issues/247)

### Changed
- Converting pekko classic to typed [#232](https://github.com/ie3-institute/simonaAPI/issues/232)

## [0.7.0] - 2025-03-11

### Added
- Added Bao and Staudt to the list of reviewers [#216](https://github.com/ie3-institute/simonaAPI/issues/216)
- Documentation for this API [#230](https://github.com/ie3-institute/simonaAPI/issues/230)

#### Changed
- Updated PSDM to 6.0.0

## [0.6.0] - 2024-12-02

### Added
- Enable initialization of external data simulation [#167](https://github.com/ie3-institute/simonaAPI/issues/167)
- `ExtResultContainer` returns result map [#217](https://github.com/ie3-institute/simonaAPI/issues/217)

### Changed
- Renaming power fields in `EvModel` [#208](https://github.com/ie3-institute/simonaAPI/issues/208)
- Enhancing external data connections [#219](https://github.com/ie3-institute/simonaAPI/issues/219)

## [0.5.0] - 2024-08-09

### Added
- Implemented `ExtPrimaryData` and `ExtResultsData` [#145](https://github.com/ie3-institute/simonaAPI/issues/145)
- Have EV simulation communicate the next tick [#170](https://github.com/ie3-institute/simonaAPI/issues/170)
- Only communicate the next tick of activation of EV simulation as a whole [#176](https://github.com/ie3-institute/simonaAPI/issues/176)

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

[Unreleased/Snapshot]: https://github.com/ie3-institute/simonaapi/compare/0.11.0...HEAD
[0.11.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.10.0...0.11.0
[0.10.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.9.0...0.10.0
[0.9.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.8.0...0.9.0
[0.8.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.7.0...0.8.0
[0.7.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.6.0...0.7.0
[0.6.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.5.0...0.6.0
[0.5.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.4.0...0.5.0
[0.4.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.3.0...0.4.0
[0.3.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/0.2.0...0.3.0
[0.2.0]: https://github.com/ie3-institute/powersystemdatamodel/compare/e3f0c247d9d2a92840f49412aa729c5f033cb4de...0.2.0
