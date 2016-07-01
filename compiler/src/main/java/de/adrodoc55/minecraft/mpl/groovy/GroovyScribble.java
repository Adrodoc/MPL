/*
 * Minecraft Programming Language (MPL): A language for easy development of command block
 * applications including an IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * This file is part of MPL.
 *
 * MPL is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MPL is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with MPL. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 *
 * Minecraft Programming Language (MPL): Eine Sprache für die einfache Entwicklung von Commandoblock
 * Anwendungen, inklusive einer IDE.
 *
 * © Copyright (C) 2016 Adrodoc55
 *
 * Diese Datei ist Teil von MPL.
 *
 * MPL ist freie Software: Sie können diese unter den Bedingungen der GNU General Public License,
 * wie von der Free Software Foundation, Version 3 der Lizenz oder (nach Ihrer Wahl) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * MPL wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG,
 * bereitgestellt; sogar ohne die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN
 * BESTIMMTEN ZWECK. Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit MPL erhalten haben. Wenn
 * nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.adrodoc55.minecraft.mpl.groovy;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.SecureASTCustomizer;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * @author Adrodoc55
 */
public class GroovyScribble {
  public static void main(String[] args) {
    String scriptText = "for (i in 1..10) { mpl << \"/say hi ${i}\" }";
    // String scriptText = "new URL('https://github.com/Adrodoc55/MPL').openConnection()";
    try {
      List<CharSequence> result = runGroovyScript(scriptText);
      for (CharSequence string : result) {
        System.out.println(string);
      }
    } catch (InterruptedException ex) {
      throw new RuntimeException(ex);
    } catch (ExecutionException ex) {
      Throwable cause = ex.getCause();
      // if (cause instanceof MultipleCompilationErrorsException) {
      // ErrorCollector errorCollector =
      // ((MultipleCompilationErrorsException) cause).getErrorCollector();
      // Message error = errorCollector.getError(0);
      // error.write(new PrintWriter(System.out));
      // System.out.println("######");
      // }
      System.out.println(cause);
    } catch (TimeoutException ex) {
      System.out.println(ex.getMessage());
    }
  }

  private static List<CharSequence> runGroovyScript(String scriptText)
      throws InterruptedException, ExecutionException, TimeoutException {
    ExecutorService executor = getExecutor();
    Future<List<CharSequence>> future = executor.submit(new Callable<List<CharSequence>>() {
      @Override
      public List<CharSequence> call() throws Exception {
        Binding binding = new Binding();
        List<CharSequence> generatedLines = new ArrayList<>();
        binding.setVariable("mpl", generatedLines);
        GroovyShell shell = new GroovyShell(binding, getGroovyCompilerConfiguration());
        Script script = shell.parse(scriptText);
        script.run();
        return generatedLines;
      }
    });
    List<CharSequence> result = future.get(1, TimeUnit.SECONDS);
    return result;
  }

  private static ExecutorService EXECUTOR;

  private static ExecutorService getExecutor() {
    if (EXECUTOR == null) {
      EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
          Thread thread = new Thread(r);
          thread.setDaemon(true);
          return thread;
        }
      });
    }
    return EXECUTOR;
  }

  private static CompilerConfiguration CONFIG;

  private static CompilerConfiguration getGroovyCompilerConfiguration() {
    if (CONFIG == null) {
      CONFIG = new CompilerConfiguration();
      SecureASTCustomizer secure = new SecureASTCustomizer();
      secure.setImportsWhitelist(emptyList());
      ArrayList<String> starImportsWhitelist = new ArrayList<>();
      starImportsWhitelist.add("java.lang.*");
      starImportsWhitelist.add("java.util.*");
      secure.setStarImportsWhitelist(starImportsWhitelist);
      secure.setStaticImportsWhitelist(emptyList());
      secure.setStaticStarImportsWhitelist(emptyList());
      secure.setIndirectImportCheckEnabled(true);
      CONFIG.addCompilationCustomizers(secure);
    }
    return CONFIG;
  }
}
