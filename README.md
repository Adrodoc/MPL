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
You can find a full tutorial for MPL [here](https://github.com/Adrodoc55/MPL/wiki/Tutorial-1.-Basics).

There are multiple ways to compile `.mpl` files:
* Open the file in the IDE and compile it by hand. You can find a detailed explanation in the tutorial.
* Run the standalone compiler [from the command line](https://github.com/Adrodoc55/MPL/wiki/Using-MPL-from-the-command-line).
* Compile MPL ingame using the Bukkit plugin [MplManager](https://gitlab.crazyblock-network.net/BrainStone/MplManager) by BrainStone.

License
-------
MPL is licensed under [GNU General Public License]. Any application compiled by the MPL compiler is free from this license even if the compiler inserted standard MPL code templates.

It would still be nice if you give credit to MPL, for example by using the following command in the installation of your application:

`/tellraw @a [{"text":"This project was created using the\n ","color":"yellow"},{"text":"Minecraft Programming Language ","color":"gold"},{"text":"[MPL]","color":"aqua","hoverEvent":{"action":"show_text","value":{"text":"Click to open GitHub","color":"red"}},"clickEvent":{"action":"open_url","value":"https://github.com/Adrodoc55/MPL"}}]`

Download
--------
For manual use you can download the standalone compiler and IDE from [GitHub](https://github.com/Adrodoc55/MPL/releases).

MPL is also available both as library and standalone application at [Sonatype OSS Maven Repository] and [Maven Central].
To get MPL using Maven add the following to your `pom`:
```
<dependency>
    <groupId>de.adrodoc55.mpl</groupId>
    <artifactId>mpl-compiler</artifactId>
    <version>1.2.1</version>
    <scope>compile</scope>
</dependency>
```
In [Gradle] you can use the following example `build.gradle`:
```gradle
apply plugin: 'java'

repositories {
  mavenCentral()
}

dependencies {
  compile 'de.adrodoc55.mpl:mpl-compiler:1.2.1'
}
```

Contributing
------------
If you are missing a feature or just want to help out you can contribute to MPL by opening a [pull request](https://help.github.com/articles/using-pull-requests/).

Here are some guidelines to make contributing easier:
* The MPL Project is split into two parts, the `compiler` and the `ide`. Both of these directories are subprojects, that should be imported into your IDE.
* You should configure your IDE to use the [google java formatter](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml).
* All files should be encoded in `UTF-8`.
* This project uses lombok. This does not make a difference on the command line, but depending on what IDE you use, you may have to install lombok into the IDE: https://projectlombok.org/download.html.
* When using [Eclipse] configure the project by using the [Gradle Wrapper] and executing `gradlew eclipse` on a command line in the root directory.
* Most tests are written in groovy. If you are using [Eclipse] you might want to use the [greclipse](https://github.com/groovy/groovy-eclipse/wiki) plugin.

### Building

MPL is built using [Gradle]. You don't have to install Gradle, because the project contains a [Gradle Wrapper] along with the source files. To build custom jar files execute `gradlew build` on Windows or `./gradlew build` on Unix systems. The jar files will be created in the directories `MPL/ide/build/libs` and `MPL/compiler/build/libs`.

[Sonatype OSS Maven Repository]: https://oss.sonatype.org/content/repositories/releases/de/adrodoc55/mpl
[Maven Central]: http://search.maven.org/#search|ga|1|g%3Ade.adrodoc55.mpl
[GNU General Public License]: http://www.gnu.org/licenses/gpl-3.0
[Gradle]: http://gradle.org/
[Gradle Wrapper]: https://docs.gradle.org/current/userguide/gradle_wrapper.html
[Eclipse]: https://eclipse.org/
