package com.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * 自定义SessionManager
 */
public class CustomSessionManager extends DefaultWebSessionManager {
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        //获取session序列化对象
        Serializable sessionId = sessionKey.getSessionId();
        ServletRequest request = null;
        //获取request
        if (sessionKey instanceof WebSessionKey) {
             request = ((WebSessionKey) sessionKey).getServletRequest();
        }
        //从request中获取session
        Session session = null;
        if (request != null && sessionId != null) {
            session = (Session) request.getAttribute(sessionId.toString());
        }
        if (session != null) {
            return session;
        }
        //如果request中没有session，则从redis中获取并设置到request中
        session = super.retrieveSession(sessionKey);
        if (request != null && sessionId != null) {
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }
}
