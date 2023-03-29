package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.ISessionManagerFactory;
import redis.clients.jedis.JedisPool;

import java.util.Properties;

public class SessionManagerFactory implements ISessionManagerFactory {

    @Override
    public ISessionManager createSessionManager(Properties properties) {
        // read "redis.host" and "redis.port" from the properties
        String host = properties.getProperty("redis.host");
        int port = Integer.parseInt(properties.getProperty("redis.port"));
        return new SessionManager(new JedisPool(host, port));
    }
}
