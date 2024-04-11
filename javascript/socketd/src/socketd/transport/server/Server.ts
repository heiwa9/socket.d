import {IoConsumer} from "../core/Typealias";
import {ServerConfig} from "./ServerConfig";
import {Listener, SimpleListener} from "../core/Listener";
import {ChannelAssistant} from "../core/ChannelAssistant";
import {Processor, ProcessorDefault} from "../core/Processor";
import {Session} from "../core/Session";
import {Message} from "../core/Message";
import {RunUtils} from "../../utils/RunUtils";

/**
 * 服务端
 *
 * @author noear
 * @since 2.0
 */
export interface Server {
    /**
     * 获取台头
     * */
    getTitle(): string;

    /**
     * 获取配置
     * */
    getConfig(): ServerConfig;

    /**
     * 配置
     */
    config(configHandler: IoConsumer<ServerConfig>): Server;

    /**
     * 监听
     */
    listen(listener: Listener): Server;

    /**
     * 启动
     */
    start(): Server;

    /**
     * 停止
     */
    stop();
}



/**
 * 服务端基类
 *
 * @author noear
 * @since 2.0
 */
export abstract class ServerBase<T extends ChannelAssistant<any>> implements Server,Listener {
    protected _processor: Processor = new ProcessorDefault();
    protected _sessions: Set<Session> = new Set<Session>();
    protected _listener: Listener = new SimpleListener();

    private _config: ServerConfig;
    private _assistant: T;
    protected _isStarted: boolean;

    constructor(config: ServerConfig, assistant: T) {
        this._config = config;
        this._assistant = assistant;
    }

    /**
     * 获取通道助理
     */
    getAssistant(): T {
        return this._assistant;
    }

    abstract getTitle(): string ;

    /**
     * 获取配置
     */
    getConfig(): ServerConfig {
        return this._config;
    }

    /**
     * 配置
     */
    config(configHandler: IoConsumer<ServerConfig>): Server {
        if (configHandler) {
            configHandler(this._config);
        }
        return this;
    }


    /**
     * 获取处理器
     */
    getProcessor(): Processor {
        return this._processor;
    }


    /**
     * 设置监听器
     */
    listen(listener: Listener): Server {
        if (listener) {
            this._listener = listener;
        }
        return this;
    }

    abstract start(): Server;

    prestop() {
        this.prestopDo();
    }

    stop() {
        this.stopDo();
    }

    onOpen(s: Session) {
        this._sessions.add(s);
        this._listener.onOpen(s);
    }

    onMessage(s: Session, m: Message) {
        this._listener.onMessage(s, m);
    }

    onClose(s: Session) {
        this._sessions.delete(s);
        this._listener.onClose(s);
    }

    onError(s: Session, e: any) {
        this._listener.onError(s, e);
    }

    protected prestopDo() {
        for (let s1 of this._sessions) {
            if (s1.isValid()) {
                RunUtils.runAndTry(() => s1.preclose());
            }
        }
    }

    protected stopDo() {
        for (let s1 of this._sessions) {
            if (s1.isValid()) {
                RunUtils.runAndTry(() => s1.close());
            }
        }
        this._sessions.clear();
    }
}
