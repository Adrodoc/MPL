Minecraft Programming Language (MPL)
====================================

Author: Adrodoc55

What is MPL?
-----------
MPL is a language that makes it easier to write applications for Minecraft 1.9 or higher.
The final result of compiling an MPL application are command blocks that can be imported into your world in various ways.
MPL comes with it's own editor that supports syntax- and error-highlighting and has a built in compiler.

For an advanced example application written in MPL see [ApertureCraft Vanilla](https://github.com/Adrodoc55/ApertureCraftVanilla)

While MPL can be used to create very complex command block applications, it is also very easy to use for small projects.
To use the command block generator you don't even need to know about any MPL specific syntax.

How To Use
----------
You can find a full tutorial for MPL [here](https://github.com/Adrodoc55/MPL/wiki/Tutorial).

There are multiple ways to compile `.mpl` files:
* Open the file in the IDE and compile it by hand. You can find a detailed explanation in the tutorial.
* Run the standalone compiler [from the command line](https://github.com/Adrodoc55/MPL/wiki/Using-MPL-from-the-command-line).
* Compile MPL ingame using the Bukkit plugin [MplManager](https://gitlab.crazyblock-network.net/BrainStone/MplManager) by BrainStone.

Download
--------
For manual use you can download the standalone compiler and IDE from [GitHub](https://github.com/Adrodoc55/MPL/releases).

MPL is also available both as library and standalone application at [Sonatype OSS Maven Repository] and [Maven Central].
To get MPL using Maven add the following to your `pom`:
```
<dependency>
    <groupId>de.adrodoc55.mpl</groupId>
    <artifactId>mpl-compiler</artifactId>
    <version>0.11.0</version>
    <scope>compile</scope>
</dependency>
```
In Gradle you can use the following example `build.gradle`:
```gradle
apply plugin: 'java'

repositories {
  mavenCentral()
}

dependencies {
  compile 'de.adrodoc55.mpl:mpl-compiler:0.11.0'
}
```
[Sonatype OSS Maven Repository]: https://oss.sonatype.org/content/repositories/releases/de/adrodoc55/mpl
[Maven Central]: http://search.maven.org/#search|ga|1|g%3Ade.adrodoc55.mpl
