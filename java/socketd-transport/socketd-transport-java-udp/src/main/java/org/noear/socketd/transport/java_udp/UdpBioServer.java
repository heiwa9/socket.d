package org.noear.socketd.transport.java_udp;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.java_udp.impl.DatagramFrame;
import org.noear.socketd.transport.java_udp.impl.DatagramTagert;
import org.noear.socketd.transport.core.Channel;
import org.noear.socketd.transport.core.internal.ChannelDefault;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.transport.server.ServerBase;
import org.noear.socketd.transport.server.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RejectedExecutionException;

/**
 * Udp-Bio 服务端实现（支持 ssl, host）
 *
 * @author Urara
 * @since 2.0
 */
public class UdpBioServer extends ServerBase<UdpBioChannelAssistant> {
    private static final Logger log = LoggerFactory.getLogger(UdpBioServer.class);

    private Map<String, Channel> channelMap = new HashMap<>();
    private DatagramSocket server;

    public UdpBioServer(ServerConfig config) {
        super(config, new UdpBioChannelAssistant(config));
    }

    private DatagramSocket createServer() throws IOException {
        return new DatagramSocket(config().getPort());
    }

    @Override
    public String title() {
        return "udp/bio/java-udp/" + SocketD.version();
    }

    @Override
    public Server start() throws IOException {
        if (isStarted) {
            throw new IllegalStateException("Server started");
        } else {
            isStarted = true;
        }

        server = createServer();

        config().getIoExecutor().submit(() -> {
            while (true) {
                try {
                    DatagramFrame datagramFrame = assistant().read(server);
                    if (datagramFrame == null) {
                        continue;
                    }

                    Channel channel = getChannel(datagramFrame);

                    try {
                        config().getIoExecutor().submit(() -> {
                            try {
                                processor().onReceive(channel, datagramFrame.getFrame());
                            } catch (Throwable e) {
                                log.debug("{}", e);
                            }
                        });
                    } catch (RejectedExecutionException e) {
                        log.warn("Server thread pool is full", e);
                    } catch (Throwable e) {
                        log.debug("{}", e);
                    }
                } catch (Throwable e) {
                    if (server.isClosed()) {
                        //说明被手动关掉了
                        return;
                    }

                    log.debug("{}", e);
                }
            }
        });

        log.info("Server started: {server=" + config().getLocalUrl() + "}");

        return this;
    }

    private Channel getChannel(DatagramFrame datagramFrame) {
        String addressAndPort = datagramFrame.getPacketAddress();
        Channel channel0 = channelMap.get(addressAndPort);

        if (channel0 == null) {
            DatagramTagert tagert = new DatagramTagert(server, datagramFrame.getPacket(), false);
            channel0 = new ChannelDefault<>(tagert, config(), assistant());
            channelMap.put(addressAndPort, channel0);
        }

        return channel0;
    }


    @Override
    public void stop() {
        if (isStarted) {
            isStarted = false;
        } else {
            return;
        }

        try {
            server.close();
        } catch (Exception e) {
            log.debug("{}", e);
        }
    }
}