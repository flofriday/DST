package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.SessionCreationFailedException;
import dst.ass1.kv.SessionNotFoundException;
import redis.clients.jedis.JedisPool;

import java.util.Map;
import java.util.UUID;

public class SessionManager implements ISessionManager {

    private static final String USER_PREFIX = "userSession:"; // For a map from userId to the session
    private static final String DATA_PREFIX = "sessionData:"; // The actual session data
    private final JedisPool pool;

    public SessionManager(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public String createSession(Long userId, int timeToLive) throws SessionCreationFailedException {
        try (var jedis = pool.getResource()) {
            jedis.del(USER_PREFIX + userId);
        }
        return requireSession(userId, timeToLive);
    }

    @Override
    public void setSessionVariable(String sessionId, String key, String value) throws SessionNotFoundException {
        try (var jedis = pool.getResource()) {
            if (!jedis.exists(DATA_PREFIX + sessionId)) throw new SessionNotFoundException();
            jedis.hset(DATA_PREFIX + sessionId, key, value);
        }
    }

    @Override
    public String getSessionVariable(String sessionId, String key) throws SessionNotFoundException {
        try (var jedis = pool.getResource()) {
            if (!jedis.exists(DATA_PREFIX + sessionId)) throw new SessionNotFoundException();
            return jedis.hget(DATA_PREFIX + sessionId, key);
        }
    }

    @Override
    public Long getUserId(String sessionId) throws SessionNotFoundException {
        return Long.parseLong(getSessionVariable(sessionId, "userId"));
    }

    @Override
    public int getTimeToLive(String sessionId) throws SessionNotFoundException {
        return Integer.parseInt(getSessionVariable(sessionId, "timeToLive"));
    }

    @Override
    public String requireSession(Long userId, int timeToLive) throws SessionCreationFailedException {
        try (var jedis = pool.getResource()) {
            // Return existing session token
            var sessionToken = jedis.get(USER_PREFIX + userId.toString());
            if (sessionToken != null) return sessionToken;

            // Create new token
            jedis.watch(USER_PREFIX + userId);
            var t = jedis.multi();

            sessionToken = UUID.randomUUID().toString();
            Map<String, String> value = Map.of(
                    "userId", userId.toString(),
                    "timeToLive", ((Integer) timeToLive).toString()
            );

            t.hmset(DATA_PREFIX + sessionToken, value);
            t.expire(DATA_PREFIX + sessionToken, timeToLive);
            t.set(USER_PREFIX + userId, sessionToken);
            t.expire(USER_PREFIX + userId, timeToLive);
            var res = t.exec();
            if (res == null) throw new SessionCreationFailedException();
            return sessionToken;
        }
    }

    @Override
    public void close() {
        pool.close();
    }
}
