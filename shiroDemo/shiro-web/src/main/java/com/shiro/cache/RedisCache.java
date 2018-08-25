package com.shiro.cache;

import com.shiro.util.JedisUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

@Component
public class RedisCache<K, V> implements Cache<K, V> {

    @Resource
    private JedisUtil jedisUtil;

    //定义一个cache前缀
    private final String CACHE_PREFIX = "Schuyuler";

    /**
     * 私有方法，通过传入的参数来获取键
     * @return key的字节数组
     */
    private byte[] getKey(K k) {
        if (k instanceof String) {
            return (k + CACHE_PREFIX).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    /**
     * 获取缓存值
     * @param k 缓存键
     * @return 缓存值
     * @throws CacheException
     */
    @Override
    public V get(K k) throws CacheException {
        //从redis中获取缓存值
        byte[] value = jedisUtil.getValue(getKey(k));
        if (value != null) {
            return (V) SerializationUtils.deserialize(value);
        }
        return null;
    }

    /**
     * 进行缓存
     * 这里将缓存放入redis中
     * @param k 缓存键
     * @param v 缓存值
     * @return 缓存值
     * @throws CacheException
     */
    @Override
    public V put(K k, V v) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = SerializationUtils.serialize(v);
        //将键值放入redis中
        jedisUtil.set(key, value);
        //设置redis中对应key的保存时间
        jedisUtil.expire(key, 600);
        return v;
    }

    /**
     * 删除缓存
     * @param k 缓存键
     * @return 缓存值
     * @throws CacheException
     */
    @Override
    public V remove(K k) throws CacheException {
        byte[] key = getKey(k);
        V v = get(k);
        jedisUtil.delete(key);
        if (v != null) {
            return v;
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {
        //这里不需要去重写了，redis中不仅仅存放了权限的缓存，一旦清空就都没了
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
