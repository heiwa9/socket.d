package labs;

import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.EntityMetas;
import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.entity.FileEntity;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.EventListener;
import org.noear.socketd.transport.server.Server;
import org.noear.socketd.utils.RunUtils;
import org.noear.socketd.utils.StrUtils;

import java.io.File;

public class ServerTest {
    static final String[] schemas = new String[]{
            "sd:tcp-java",
            "sd:tcp-netty",
            "sd:tcp-smartsocket",
            "sd:tcp-neta",//3
            "sd:ws-java",
            "sd:udp-java",//5
            "sd:udp-netty",
            "sd:kcp-java",//7
    };

    /**
     * 启动服务，给别的客户端调试
     */
    public static void main(String[] args) throws Exception {
        String s1 = schemas[4];
        Server server = SocketD.createServer(s1)
                .config(c -> c.port(8602).fragmentSize(1024 * 1024))
                .listen(buildListener())
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            RunUtils.runAndTry(server::stop);
        }));
    }

    /**
     * 构建监听器
     */
    private static Listener buildListener() {
        return new EventListener()
                .doOnOpen(s -> {
                    s.handshake().outMeta("test","1");
                    System.out.println("onOpen: " + s.sessionId() + ", meta=" + s.handshake().paramMap());
                }).doOnMessage((s, m) -> {
                    System.out.println("onMessage: " + m);
                }).doOn("/demo", (s, m) -> {
                    if (m.isRequest()) {
                        s.reply(m, new StringEntity("me to! ref:" + m.dataAsString()));
                    }

                    if (m.isSubscribe()) {
                        int size = m.rangeSize();
                        for (int i = 1; i <= size; i++) {
                            s.reply(m, new StringEntity("me to-" + i));
                        }
                        s.replyEnd(m, new StringEntity("welcome to my home!"));
                    }
                }).doOn("/upload", (s, m) -> {
                    if (m.isRequest()) {
                        String fileName = m.meta(EntityMetas.META_DATA_DISPOSITION_FILENAME);
                        if (StrUtils.isEmpty(fileName)) {
                            s.reply(m, new StringEntity("no file! size: " + m.dataSize()));
                        } else {
                            s.reply(m, new StringEntity("file received: " + fileName + ", size: " + m.dataSize()));
                        }
                    }
                }).doOn("/download", (s, m) -> {
                    if (m.isRequest()) {
                        FileEntity fileEntity = new FileEntity(new File("/Users/noear/Movies/snack3-rce-poc.mov"));
                        s.reply(m, fileEntity);
                    }
                }).doOn("/push", (s, m) -> {
                    if (s.attrHas("push")) {
                        return;
                    }

                    s.attrPut("push", "1");

                    while (true) {
                        if (s.attrHas("push") == false) {
                            break;
                        }

                        s.send("/push", new StringEntity("push test"));
                        RunUtils.runAndTry(() -> Thread.sleep(200));
                    }
                }).doOn("/unpush", (s, m) -> {
                    s.attrMap().remove("push");
                })
                .doOnClose(s -> {
                    System.out.println("onClose: " + s.sessionId());
                }).doOnError((s, err) -> {
                    System.out.println("onError: " + s.sessionId());
                    err.printStackTrace();
                });
    }
}
