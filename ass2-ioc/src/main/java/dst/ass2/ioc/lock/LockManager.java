package dst.ass2.ioc.lock;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {

    // Singleton pattern
    private static LockManager instance;

    public synchronized static LockManager getInstance() {
        if (instance == null) {
            instance = new LockManager();
        }
        return instance;
    }

    // Instance contents
    private HashMap<String, Lock> locks = new HashMap<>();

    // If a lock doesn't exist it will be implizitly created.
    public synchronized Lock getLock(String name) {
        var lock = locks.get(name);
        if (lock != null) {
            System.out.println("LOCK '" + name + "' " + lock);
            return lock;
        }
        System.out.println("NEW LOCK '" + name + "'");

        // We use ReentrantLock to allow recursion, which is required by the assignment.
        lock = new ReentrantLock();
        locks.put(name, lock);
        System.out.println("LOCK '" + name + "' " + lock);
        return lock;
    }
}
