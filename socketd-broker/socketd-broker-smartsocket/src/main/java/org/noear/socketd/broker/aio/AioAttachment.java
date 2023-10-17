package org.noear.socketd.broker.aio;

import org.noear.socketd.broker.aio.util.FixedLengthFrameDecoder;
import org.noear.socketd.protocol.Channel;
import org.smartboot.socket.transport.AioSession;

import java.util.HashMap;

/**
 * @author noear
 * @since 2.0
 */
public class AioAttachment extends HashMap<Class<?>,Object> {
    public static AioAttachment get(AioSession aioSession) {
        AioAttachment tmp = aioSession.getAttachment();
        if (tmp == null) {
            tmp = new AioAttachment();
            aioSession.setAttachment(new AioAttachment());
        }

        return tmp;
    }

    public static Channel getChannel(AioSession aioSession, AioExchanger exchanger){
        return null;
    }

    public static FixedLengthFrameDecoder getDecoder(AioSession aioSession) {
        return (FixedLengthFrameDecoder) get(aioSession).get(FixedLengthFrameDecoder.class);
    }

    public static void setDecoder(AioSession aioSession, FixedLengthFrameDecoder decoder) {
        get(aioSession).put(FixedLengthFrameDecoder.class, decoder);
    }
}
