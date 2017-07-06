package de.adrodoc55.commons;

import java.io.File;

import javax.annotation.Nullable;

import org.apache.commons.lang.SystemUtils;

public interface CommonDirectories {
  public static @Nullable File getFurthestExistingSubDir(@Nullable File parentDirectory,
      String relativePath) {
    if (parentDirectory == null || !parentDirectory.isDirectory()) {
      return null;
    }
    File file = new File(parentDirectory, relativePath);
    while (!file.isDirectory()) {
      file = file.getParentFile();
    }
    return file;
  }

  public static File getMCEditDir() {
    if (SystemUtils.IS_OS_WINDOWS) {
      return new File(SystemUtils.getUserHome(), "Documents/MCEdit");
    } else {
      return new File("/.mcedit");
    }
  }

  public static @Nullable File getMinecraftDir() {
    String path = getDirInAppData("minecraft");
    if (path != null) {
      return new File(path);
    }
    return null;
  }

  public static @Nullable String getDirInAppData(String dir) {
    if (SystemUtils.IS_OS_WINDOWS) {
      return System.getenv("APPDATA") + "/." + dir;
    } else if (SystemUtils.IS_OS_MAC) {
      return SystemUtils.USER_HOME + "/Library/Application Support/" + dir;
    } else if (SystemUtils.IS_OS_UNIX) {
      return SystemUtils.USER_HOME + "/." + dir;
    }
    return null;
  }
}
