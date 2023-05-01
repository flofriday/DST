package dst.ass2.aop.impl;

import dst.ass2.aop.IPluginExecutable;
import dst.ass2.aop.IPluginExecutor;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;

public class PluginExecutor implements IPluginExecutor {

    HashMap<File, WatchKey> watchKeys = new HashMap<>();
    HashMap<WatchKey, File> watchFiles = new HashMap<>();
    WatchService watchService = null;
    Thread watchThread = null;
    ExecutorService pluginPool = Executors.newCachedThreadPool();

    public PluginExecutor() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void monitor(File dir) {
        // FIXME: do I need to initialize all plugins in that folder here?
        // What if the Executor hast even started yet?

        if (watchKeys.containsKey(dir)) return;

        WatchKey key = null;
        try {
            key = dir.toPath().register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        watchKeys.put(dir, key);
        watchFiles.put(key, dir);
    }

    @Override
    public void stopMonitoring(File dir) {
        var key = watchKeys.remove(dir);
        if (key == null) return;
        watchFiles.remove(key);
        key.cancel();
    }

    @Override
    public void start() {
        if (watchThread != null) return;

        // FIXME: Do we need to "clean" the events from the watchservice here?
        watchThread = new Thread(this::watchAndExecute);
        watchThread.start();
    }

    @Override
    public void stop() {
        if (watchThread == null)
            return;

        watchThread.interrupt();
    }

    // This method runs in a seperate thread and if any changes are detected it will execute the plugins
    private void watchAndExecute() {
        var lastLoaded = new HashMap<Path, Long>();

        while (true) {
            WatchKey key = null;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                // This thread can be stopped at any time.
                return;
            }
            for (var event : key.pollEvents()) {
                var path = watchFiles.get(key).toPath().resolve((Path) event.context());

                if (!path.toString().toLowerCase().endsWith(".jar")) {
                    System.out.println("Not a plugin: " + path);
                    continue;
                }

                // Sometimes two events are created for the same file so lets lookup the last modified and if we have
                // loaded that one already.
                if (lastLoaded.containsKey(path) && lastLoaded.get(path) <= path.toFile().lastModified())
                    continue;

                lastLoaded.put(path, path.toFile().lastModified());


                System.out.println("Loading:" + path.getFileName());

                try {
                    var plugins = loadPlugin(path);
                    plugins.forEach(
                            (plugin) -> pluginPool.submit(plugin)
                    );
                } catch (RuntimeException e) {
                    System.out.println(e);
                }
            }
        }
    }

    private List<Runnable> loadPlugin(Path plugin) {

        var runnables = new ArrayList<Runnable>();

        try (var jarFile = new JarFile(plugin.toFile())) {
            for (var entry : Collections.list(jarFile.entries())) {
                if (entry.isDirectory() || !entry.getName().toLowerCase().endsWith("class"))
                    continue;

                var className = entry.getName().replace("/", ".").replace(".class", "");

                Class clazz = null;
                try (var classLoader = URLClassLoader.newInstance(new URL[]{plugin.toUri().toURL()})) {
                    clazz = classLoader.loadClass(className);
                }

                if (!IPluginExecutable.class.isAssignableFrom(clazz))
                    continue;

                Class<? extends IPluginExecutable> pluginClass = (Class<? extends IPluginExecutable>) clazz;

                runnables.add(() -> {
                    try {
                        pluginClass.getConstructor().newInstance().execute();
                    } catch (ReflectiveOperationException e) {
                        System.out.println("Could not create new class: " + pluginClass.getName());
                        throw new RuntimeException(e);
                    }
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return runnables;
    }
}
