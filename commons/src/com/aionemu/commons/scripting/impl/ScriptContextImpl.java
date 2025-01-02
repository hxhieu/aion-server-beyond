package com.aionemu.commons.scripting.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.configs.CommonsConfig;
import com.aionemu.commons.scripting.CompilationResult;
import com.aionemu.commons.scripting.ScriptCompiler;
import com.aionemu.commons.scripting.ScriptCompilerCache;
import com.aionemu.commons.scripting.ScriptContext;
import com.aionemu.commons.scripting.classlistener.ClassListener;
import com.aionemu.commons.scripting.impl.javacompiler.ScriptCompilerImpl;

/**
 * This class is actual implementation of {@link com.aionemu.commons.scripting.ScriptContext}
 * 
 * @author SoulKeeper
 */
public class ScriptContextImpl implements ScriptContext {

	private static final Logger log = LoggerFactory.getLogger(ScriptContextImpl.class);

	/**
	 * Root directories of this script context. It and it's subdirectories will be scanned for .java files.
	 */
	private final File[] directories;

	/**
	 * Result of compilation of script context
	 */
	private CompilationResult compilationResult;

	/**
	 * Classlistener for this script context
	 */
	private ClassListener classListener;

	/**
	 * Creates new scriptcontext with given root file
	 * 
	 * @param directories
	 *          directories where java files will be loaded from (recursively)
	 * @throws NullPointerException
	 *           if dirPattern is null
	 * @throws IllegalArgumentException
	 *           if no directory exists for dirPattern
	 */
	public ScriptContextImpl(File... directories) {
		if (directories.length == 0 || !Stream.of(directories).allMatch(File::isDirectory))
			throw new IllegalArgumentException("Invalid directories given: " + Arrays.toString(directories));
		this.directories = directories;
	}

	@Override
	public synchronized void init() {
		if (compilationResult != null) {
			log.error("Init request on initialized ScriptContext");
			return;
		}

		ScriptCompiler scriptCompiler = new ScriptCompilerImpl();
		List<File> sourceFiles = findFiles();
		if (CommonsConfig.SCRIPT_COMPILER_CACHING)
			scriptCompiler.setClasses(ScriptCompilerCache.findValidCachedClassFiles(sourceFiles));
		try {
			compilationResult = scriptCompiler.compile(sourceFiles);
		} catch (ClassFormatError e) {
			if (!CommonsConfig.SCRIPT_COMPILER_CACHING)
				throw e;
			log.warn("Couldn't load cached classes from " + ScriptCompilerCache.CACHE_DIR + ", refreshing files in cache...", e);
			ScriptCompilerCache.invalidate(sourceFiles = findFiles());
			scriptCompiler.setClasses(Collections.emptyMap());
			compilationResult = scriptCompiler.compile(sourceFiles);
		}
		if (CommonsConfig.SCRIPT_COMPILER_CACHING)
			ScriptCompilerCache.cacheClasses(compilationResult.getBinaryClasses());

		getClassListener().postLoad(compilationResult.getCompiledClasses());
	}

	private List<File> findFiles() {
		List<File> files = new ArrayList<>();
		for (File dir : directories) {
			try {
				Files.find(dir.toPath(), Integer.MAX_VALUE, (path, attrs) -> attrs.isRegularFile() && path.toString().endsWith(".java"))
					.forEach(path -> files.add(path.toFile()));
			} catch (IOException e) {
				throw new RuntimeException("Error scanning " + dir, e);
			}
		}
		return files;
	}

	@Override
	public synchronized void shutdown() {
		if (compilationResult == null) {
			log.error("Shutdown of not initialized script context", new Exception());
			return;
		}
		getClassListener().preUnload(compilationResult.getCompiledClasses());
		compilationResult = null;
	}

	@Override
	public void reload() {
		shutdown();
		init();
	}

	@Override
	public CompilationResult getCompilationResult() {
		return compilationResult;
	}

	@Override
	public synchronized boolean isInitialized() {
		return compilationResult != null;
	}

	@Override
	public void setClassListener(ClassListener cl) {
		classListener = cl;
	}

	@Override
	public ClassListener getClassListener() {
		return classListener;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ScriptContextImpl another && Arrays.equals(another.directories, directories);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(directories);
	}
}
