package dst.ass2.ioc.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {

    // Singleton pattern
    private static LockManager instance;

    public static LockManager getInstance() {
        if (instance == null) {
            instance = new LockManager();
        }
        return instance;
    }

    // Instance contents
    private ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();

    public Lock getLock(String name) {
        return locks.get(name);
    }

    public void addLock(String name) {
        if (locks.containsKey(name)) return;

        // We use ReentrantLock to allow recursion, which is required by the assignment.
        locks.put(name, new ReentrantLock());
    }

}
