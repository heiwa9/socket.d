package org.noear.socketd.transport.client;

import org.noear.socketd.transport.core.*;
import org.noear.socketd.transport.stream.RequestStream;
import org.noear.socketd.transport.stream.SendStream;
import org.noear.socketd.transport.stream.SubscribeStream;

import java.io.Closeable;
import java.io.IOException;

/**
 * 客户会话
 *
 * @author noear
 */
public interface ClientSession extends Closeable {
    /**
     * 是否有效
     */
    boolean isValid();

    /**
     * 获取会话Id
     */
    String sessionId();

    /**
     * 手动重连（一般是自动）
     */
    void reconnect() throws IOException;

    /**
     * 发送
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    SendStream send(String event, Entity content) throws IOException;

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default RequestStream sendAndRequest(String event, Entity content) throws IOException {
        return sendAndRequest(event, content, 0L);
    }

    /**
     * 发送并请求
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（单位：毫秒）
     * @return 流
     */
    RequestStream sendAndRequest(String event, Entity content, long timeout) throws IOException;


    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @return 流
     */
    default SubscribeStream sendAndSubscribe(String event, Entity content) throws IOException {
        return sendAndSubscribe(event, content, 0L);
    }

    /**
     * 发送并订阅（答复结束之前，不限答复次数）
     *
     * @param event   事件
     * @param content 内容
     * @param timeout 超时（单位：毫秒）
     * @return 流
     */
    SubscribeStream sendAndSubscribe(String event, Entity content, long timeout) throws IOException;
}
