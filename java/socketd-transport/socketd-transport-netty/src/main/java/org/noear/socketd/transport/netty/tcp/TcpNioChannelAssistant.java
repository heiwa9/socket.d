package org.noear.socketd.transport.netty.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import org.noear.socketd.transport.core.ChannelInternal;
import org.noear.socketd.transport.core.Frame;
import org.noear.socketd.transport.core.ChannelAssistant;
import org.noear.socketd.utils.IoCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Tcp-Nio 通道助理实现
 *
 * @author noear
 * @since 2.0
 */
public class TcpNioChannelAssistant implements ChannelAssistant<Channel> {
    @Override
    public void write(Channel target, Frame frame, ChannelInternal channel, IoCompletionHandler completionHandler) {
        try {
            ChannelPromise writePromise = target.newPromise();
            writePromise.addListener(future -> {
                if (future.isSuccess()) {
                    //成功
                    completionHandler.completed(true, null);
                } else {
                    //失败
                    completionHandler.completed(false, future.cause());
                }
            });

            target.writeAndFlush(frame, writePromise);
        } catch (Throwable e) {
            completionHandler.completed(false, e);
        }
    }

    @Override
    public boolean isValid(Channel target) {
        return target.isActive();
    }

    @Override
    public void close(Channel target) throws IOException {
        target.close();
    }

    @Override
    public InetSocketAddress getRemoteAddress(Channel target) {
        return (InetSocketAddress) target.remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(Channel target) {
        return (InetSocketAddress) target.localAddress();
    }
}
