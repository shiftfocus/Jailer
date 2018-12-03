/*
 * Copyright 2007 - 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jailer.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;

import net.sf.jailer.configuration.Configuration;
import net.sf.jailer.render.HtmlDataModelRenderer;
import net.sf.jailer.ui.util.LogUtil;

/**
 * @author Ralf Wisser
 */
public class Environment {

	private static File home = null;
	public static Locale initialLocal = Locale.ENGLISH;
	
	public static void init() {
		initialLocal = Locale.getDefault();
		Locale.setDefault(Locale.ENGLISH);
		if (new File(".singleuser").exists() || !LogUtil.testCreateTempFile()) {
			home = new File(System.getProperty("user.home"), ".jailer");
			home.mkdirs();
			LogUtil.reloadLog4jConfig(home);
			Configuration configuration = Configuration.getInstance();
			try {
				copyIfNotExists("datamodel");
				copyIfNotExists("extractionmodel");
				copyIfNotExists("layout");
				copyIfNotExists(".cdsettings");
				copyIfNotExists(".exportdata.ui");
				copyIfNotExists(".selecteddatamodel");
				copyIfNotExists("demo-scott.h2.db");
				copyIfNotExists("demo-sakila.h2.db");

				configuration.setTempFileFolder(newFile("tmp").getPath());
				HtmlDataModelRenderer renderer = configuration.getRenderer();
				if (renderer != null) {
					renderer.setOutputFolder(newFile(renderer.getOutputFolder()).getAbsolutePath());
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static boolean copyIfNotExists(String f) throws IOException {
		File sFile = new File(f);
		File dFile = new File(home, f);
		
		if (dFile.exists() || !sFile.exists()) {
			return false;
		}
		
		Path sourcePath = sFile.toPath();
		Path targetPath = dFile.toPath();
		Files.walkFileTree(sourcePath, new CopyFileVisitor(targetPath));
		return true;
	}

	static class CopyFileVisitor extends SimpleFileVisitor<Path> {
	    private final Path targetPath;
	    private Path sourcePath = null;
	    public CopyFileVisitor(Path targetPath) {
	        this.targetPath = targetPath;
	    }

	    @Override
	    public FileVisitResult preVisitDirectory(final Path dir,
	    final BasicFileAttributes attrs) throws IOException {
	        if (sourcePath == null) {
	            sourcePath = dir;
	        }
	        Files.createDirectories(targetPath.resolve(sourcePath
	                    .relativize(dir)));
	        return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFile(final Path file,
	    final BasicFileAttributes attrs) throws IOException {
	    Files.copy(file,
	    		sourcePath == null? targetPath : targetPath.resolve(sourcePath.relativize(file)));
	    return FileVisitResult.CONTINUE;
	    }
	}

	public static File newFile(String name) {
		if (home == null || new File(name).isAbsolute()) {
			return new File(name);
		}
		return new File(home, name);
	}

}
