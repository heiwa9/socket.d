package org.noear.socketd.transport.server;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.core.impl.ProcessorDefault;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.socketd.utils.RunUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端基类
 *
 * @author noear
 * @since 2.0
 */
public abstract class ServerBase<T extends ChannelAssistant> implements Server,Listener {
    private final Processor processor = new ProcessorDefault();
    private final Collection<Session> sessionSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private Listener listener = new SimpleListener();

    private final ServerConfig config;
    private final T assistant;
    protected boolean isStarted;

    public ServerBase(ServerConfig config, T assistant) {
        this.config = config;
        this.assistant = assistant;
        this.processor.setListener(this);
    }

    /**
     * 获取通道助理
     */
    public T getAssistant() {
        return assistant;
    }

    /**
     * 获取配置
     */
    @Override
    public ServerConfig getConfig() {
        return config;
    }

    /**
     * 配置
     */
    public Server config(ServerConfigHandler configHandler) {
        if (configHandler != null) {
            configHandler.serverConfig(config);
        }
        return this;
    }


    /**
     * 获取处理器
     */
    public Processor getProcessor() {
        return processor;
    }


    /**
     * 设置监听器
     */
    @Override
    public Server listen(Listener listener) {
        if (listener != null) {
            this.listener = listener;
        }
        return this;
    }

    @Override
    public void prestop() {
        prestopDo();
    }

    @Override
    public void stop() {
        stopDo();
        sessionClear();
    }

    @Override
    public void onOpen(Session s) throws IOException {
        sessionAdd(s);
        listener.onOpen(s);
    }

    @Override
    public void onMessage(Session s, Message m) throws IOException {
        listener.onMessage(s, m);
    }

    @Override
    public void onClose(Session s) {
        sessionRemove(s);
        listener.onClose(s);
    }

    @Override
    public void onError(Session s, Throwable e) {
        listener.onError(s, e);
    }

    /**
     * 执行预停止（发送 close-starting 指令）
     */
    protected void prestopDo() {
        for (Session s1 : sessionSet) {
            if (s1.isValid()) {
                RunUtils.runAndTry(s1::preclose);
            }
        }
    }

    /**
     * 执行预停止（发送 close 指令）
     */
    protected void stopDo() {
        for (Session s1 : sessionSet) {
            if (s1.isValid()) {
                RunUtils.runAndTry(s1::close);
            }
        }
    }

    protected void sessionAdd(Session s) {
        sessionSet.add(s);
    }

    protected void sessionRemove(Session s) {
        sessionSet.remove(s);
    }

    protected void sessionClear() {
        sessionSet.clear();
    }
}