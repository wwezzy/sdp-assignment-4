# Assignment 4 — Facade Pattern

ShP-2216 Software Design Patterns · Java 17

In this assignment, I use the Facade pattern to coordinate a home theater. The client starts and ends a movie through `HomeTheaterFacade`, while the subsystem classes manage their own state and operations.

## Components

| Class | Responsibility |
|---|---|
| `FacadeDemo` | Runs the movie scenario through the facade |
| `HomeTheaterFacade` | Coordinates startup, shutdown and error handling |
| `DvdPlayer` | Controls power and playback |
| `Amplifier` | Selects the audio source and sets volume |
| `Projector` | Checks the video source, playback and screen position |
| `Screen` | Raises and lowers the screen |
| `TheaterLights` | Adjusts and restores brightness |

## Usage

```java
HomeTheaterFacade theater = new HomeTheaterFacade();
theater.watchMovie("Interstellar");
theater.endMovie();
```

The facade creates the components internally. Subsystem classes remain available for direct use and do not depend on the facade.

## UML

![Home Theater Facade UML](docs/facade-uml.png)

[PlantUML source](docs/facade-uml.puml) · [SVG diagram](docs/facade-uml.svg)

## Run

Open the project in IntelliJ IDEA, select JDK 17 and run `kz.aitu.sdp.facade.FacadeDemo`.

From PowerShell, run:

```powershell
.\run.ps1
```

The scripts use `JAVA_HOME`, a JDK on `PATH`, or a Java 17 installation in `.jdks`. To select a JDK explicitly:

```powershell
.\run.ps1 -JdkHome "C:\path\to\jdk-17"
```

## Tests

```powershell
.\verify.ps1
```

The test runner checks 22 scenarios, including device state, invalid arguments, volume and brightness limits, shared sources, repeated sessions and cleanup after failures. It uses only the JDK and requires no external Java libraries.

## Operation order

The facade prepares the screen and lights, powers the player, configures audio and video, then starts playback. Shutdown stops playback before turning off the DVD player and restores the remaining components.

Invalid movie titles are rejected before setup begins. If startup fails, the facade attempts cleanup. Shutdown continues with the remaining actions if one device fails, and the reported exception retains the original cause.

Device operations are simulated through console output.

## Report

[Assignment 4 report](docs/SDP_Assignment_4_Facade_Report.docx)
