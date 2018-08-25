package com.shiro.session;

import com.shiro.util.JedisUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.springframework.util.CollectionUtils;
import org.springframework.util.SerializationUtils;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CaiShunfeng
 */
public class RedisSessionDao extends AbstractSessionDAO {

    /**注入工具类**/
    @Resource
    private JedisUtil jedisUtil;

    /**定义session前缀**/
    private final String SHIRO_SESSION_PROFIX = "schuyler-session";

    /**
     * 一般session的键由sessionId和session的前缀组成的字节数组，因此写个转化方法
     */
    private byte[] getKey(String id) {
        return (SHIRO_SESSION_PROFIX + id).getBytes();
    }

    /**
     * 这里因为doCreate方法和update方法出现了冗余，因此进行抽取
     * @param session Session对象
     */
    private void saveSession(Session session) {
        if (session != null && session.getId() != null) {
            //生成session序列化对象
            Serializable sessionId = generateSessionId(session);
            //获取键值
            byte[] key = getKey(sessionId.toString());
            byte[] value = SerializationUtils.serialize(session);
            //为连接对象Jedis设置session的键值
            jedisUtil.set(key, value);
            //为连接对象Jedis设置指定session键的超时时间
            jedisUtil.expire(key, 600);
        }
    }

    /**
     *  创建session序列化对象
     * 因为我们使用redis进行session共享，所以在过程中将session放入redis中
     * @param session session对象
     * @return session的序列化对象
     */
    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        //将session和session序列化对象进行捆绑
        assignSessionId(session, sessionId);
        //将session的键值保存到redis中
        saveSession(session);
        return sessionId;
    }

    /**
     * 根据session的序列化对象来读取session
     * 这里我们根据session的序列化对象从redis中获取对应的session值
     * @param sessionId session的序列化对象
     * @return session对象
     */
    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            return null;
        }
        //通过session的序列化对象来获取session的键
        byte[] key = getKey(sessionId.toString());
        //通过session的键来获取session的值
        byte[] value = jedisUtil.getValue(key);
        //通过反序列化来生成Session对象
        return (Session) SerializationUtils.deserialize(value);
    }

    /**
     * 更新Session对象
     * 这里我们更新redis中的session
     * @param session session对象
     * @throws UnknownSessionException
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        saveSession(session);
    }

    /**
     * 删除session
     * 这里我们删除redis中的session
     * @param session session对象
     */
    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            return;
        }
        byte[] key = getKey(session.getId().toString());
        //删除redis中的session
        jedisUtil.delete(key);
    }

    /**
     * 获取session集合
     * 这里我们通过定义的session前缀从redis中获取所有session键再通过遍历从redis中获取所有的session
     * @return session集合
     */
    @Override
    public Collection<Session> getActiveSessions() {
        Set<byte[]> keys = jedisUtil.keys(SHIRO_SESSION_PROFIX);
        Set<Session> sessions = new HashSet<>();
        if (CollectionUtils.isEmpty(sessions)) {
            return sessions;
        }
        for (byte[] key : keys) {
            Session session = (Session) SerializationUtils.deserialize(key);
            sessions.add(session);
        }
        return sessions;
    }
}
