# TomBoy to MyTetra data converter

Convert notes from TomBoy to MyTetra PIM manager.

Developed on the [Compose Multiplatform](https://www.jetbrains.com/lp/compose-mpp/) (Desktop) framework in [IntelliJ IDEA](https://www.jetbrains.com/idea/) in the [Kotlin](https://kotlinlang.org/) language.

Information about the framework and assembly of the application distribution:

* [Getting started](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Getting_Started)
* [Native distributions & local execution](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution)

The framework is based on the [Google toolkit](https://developer.android.com/jetpack/compose).

The application runs on the Java Virtual Machine (JVM) and is self-contained, i.e. includes all the necessary components of the Java runtime environment without requiring the JDK to be installed on the target system (that's why the distribution is so big).

The application can be built for the following operating systems:

* macOS to `.dmg` or `.pkg`
* Windows to `.exe` or `.msi`
* Linux to `.deb` or `.rpm`

At the moment, the framework does **not support cross-compilation**, so formats can only be built using a specific operating system (for example, you need to use Linux to build a `.deb`). Gradle tasks that are incompatible with the current OS are skipped by default.

To compile the installation distribution on the target system (e.g. `.exe` on Windows), you need to run the appropriate task (e.g. `packageExe`).

The minimum JDK version for the framework is 11.
However, to minimize the size of the distribution by including only the required JDK modules, the [Gradle](https://gradle.org/) build system uses the [jlink](https://plugins.gradle.org/plugin/org.beryx.jlink) plugin, which requires JDK version 15.
