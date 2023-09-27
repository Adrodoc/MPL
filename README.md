This repository has moved to https://codeberg.org/Adrodoc/MPL to avoid GitHubs two factor authentication (2FA) requirement. We believe that Microsofts decision to force all code contributors to use 2FA is very problematic for the following reasons:

1. 2FA significantly increases the risk of irreversible account loss. This is very different to 2FA for something like online banking where in the worst case you can contact your bank and verify your identity to regain access. With GitHub however, if you loose your phone and backup codes (both of which is possible), you will never gain access to your account again.
2. The decision to require 2FA for every code contributor seems very needless. Yes software supply chain attacks are a thing, but not every code contributor on GitHub is responsible for widely used libraries. It's quite the opposite: most code contributors are not responsible for widely used libraries and their code is reviewed and merged by those that are. Also, the details of the 2FA requirement seem arbitrary. Why for example is email not accepted as a second factor or why can WebAuth only be a second second factor and not a primary second factor? Just to make it really hard to not use a phone for 2FA? It feels like a "trust us, we know what's good for you" attitude from Microsoft and it is scary to think what arbitrary decision could come next.
3. Depending on how you use passwords the account security is not necessary improved that much by using 2FA, especially if it is forced onto people that don't want to use it. So why is there no opt out?
4. Many other developers publicly stated that they are leaving GitHub because of this, so staying on GitHub would prevent any code contributions from these people. This makes finding good contributors even harder than before. By moving to https://codeberg.org everyone can continue to contribute to this project.
5. Unfortunately Microsoft does not allow mail as a second factor and some companies do not allow you to bring your private phone to work or install proprietary software (such authenticators) for security reasons. This means 2FA can actually completely prevent you from logging into the website in some circumstances. This is really sad, because it can make it harder for professional developers at companies that use free and open source software to return something to the community.
6. Not everyone owns/can afford a smartphone or dedicated authenticator hardware and Microsoft makes it very inconvenient to use 2FA without that by requiring you to install authenticator software on every development machine. This discourages code contributions from poor people.

2FA is a good technology, but it should be up to repository owners to decide whether it is appropriate for the project at hand. Requiring 2FA for all code contributions, even for code that is reviewed and merged by other people, is completely unnecessary and discourages contributions.

Minecraft Programming Language (MPL)
====================================

Author: Adrodoc

What is MPL?
-----------
MPL is a language that makes it easier to write applications for Minecraft 1.9 or higher.
The final result of compiling an MPL application are command blocks that can be imported into your world in various ways.
MPL comes with it's own editor that supports syntax- and error-highlighting and has a built in compiler.

For an advanced example application written in MPL see [ApertureCraft Vanilla](https://github.com/Adrodoc/ApertureCraftVanilla)

While MPL can be used to create very complex command block applications, it is also very easy to use for small projects.
To use the command block generator you don't even need to know about any MPL specific syntax.

How To Use
----------
You can find a full tutorial for MPL [here](https://github.com/Adrodoc/MPL/wiki/Tutorial-1.-Basics).

There are multiple ways to compile `.mpl` files:
* Open the file in the IDE and compile it by hand. You can find a detailed explanation in the tutorial.
* Run the standalone compiler [from the command line](https://github.com/Adrodoc/MPL/wiki/Using-MPL-from-the-command-line).
* Compile MPL ingame using the Bukkit plugin [MplManager](https://gitlab.crazyblock-network.net/BrainStone/MplManager) by BrainStone.

License
-------
MPL is licensed under [GNU General Public License]. Any application compiled by the MPL compiler is free from this license even if the compiler inserted standard MPL code templates.

It would still be nice if you give credit to MPL, for example by using the following command in the installation of your application:

`/tellraw @a [{"text":"This project was created using the\n ","color":"yellow"},{"text":"Minecraft Programming Language ","color":"gold"},{"text":"[MPL]","color":"aqua","hoverEvent":{"action":"show_text","value":{"text":"Click to open GitHub","color":"red"}},"clickEvent":{"action":"open_url","value":"https://github.com/Adrodoc/MPL"}}]`

Download
--------
For manual use you can download the standalone compiler and IDE from [GitHub](https://github.com/Adrodoc/MPL/releases).

MPL is also available both as library and standalone application at [Sonatype OSS Maven Repository] and [Maven Central].
To get MPL using Maven add the following to your `pom`:
```
<dependency>
    <groupId>de.adrodoc55.mpl</groupId>
    <artifactId>mpl-compiler</artifactId>
    <version>1.3.2</version>
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
  compile 'de.adrodoc55.mpl:mpl-compiler:1.3.2'
}
```

Contributing
------------
If you are missing a feature or just want to help out you can contribute to MPL by opening a [pull request](https://help.github.com/articles/using-pull-requests/).

Here are some guidelines to make contributing easier:
* The MPL Project is split into two parts, the `compiler` and the `ide`. Both of these directories are subprojects, that should be imported into your IDE.
* You should configure your IDE to use the [google java formatter](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml).
* All files should be encoded in `UTF-8`.
* This project uses lombok. This does not make a difference on the command line, but depending on what IDE you use, you may have to install lombok into the IDE: https://projectlombok.org/setup/overview.
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
