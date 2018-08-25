package com.shiro.util;

import org.apache.shiro.session.Session;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author CaiShunfeng
 */
public class JedisUtil {
    /**注入连接池**/

    @Resource
    private JedisPool jedisPool;

    /**
     * 获取连接
     * @return Jedis
     */
    private Jedis getResource() {
        return jedisPool.getResource();
    }

    /**
     * 为session设置键值
     * @param key session的键
     * @param value session的值
     * @return session的值
     */
    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = getResource();
        try {
            jedis.set(key, value);
            return value;
        } finally {
            jedis.close();
        }
    }

    /**
     * 设置指定key的超时时间
     * @param key sesion的键
     * @param i 时间，以秒为单位
     */
    public void expire(byte[] key, int i) {
        Jedis jedis = getResource();
        try {
            jedis.expire(key, i);
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据session的键从Jedis中获取对应的session值
     * @param key session的键
     * @return 该键对应的值
     */
    public byte[] getValue(byte[] key) {
        Jedis jedis = getResource();
        try {
            return jedis.get(key);
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据session的键删除redis中的值
     * @param key session对应的键
     */
    public void delete(byte[] key) {
        Jedis jedis = getResource();
        try {
            jedis.del(key);
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据我们定义的session前缀来获取所有的键
     * @param SHIRO_SESSION_PROFIX session的前缀
     * @return 键的集合
     */
    public Set<byte[]> keys(String SHIRO_SESSION_PROFIX) {
        Jedis jedis = getResource();
        try {
            return jedis.keys((SHIRO_SESSION_PROFIX + "*").getBytes());
        } finally {
            jedis.close();
        }
    }
}
