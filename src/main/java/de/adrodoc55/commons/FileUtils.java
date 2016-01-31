package de.adrodoc55.commons;

import java.io.File;

public class FileUtils {

  private FileUtils() throws Exception {
    throw new Exception("Utils Classes cannot be instantiated");
  }

  public static String getFilenameWithoutExtension(File file) {
    return getFileNameWithoutExtension(file.getName());
  }

  public static String getFileNameWithoutExtension(String fileName) {
    int idx = fileName.lastIndexOf('.');
    String name = (idx == -1) ? fileName : fileName.substring(0, idx);
    return name;
  }

}
