package ws

import (
	"encoding/binary"
	"fmt"
	"log/slog"
	"net"

	"socketd/transport/core"
	"socketd/transport/core/constant"
	"socketd/transport/core/message"

	"github.com/gorilla/websocket"
)

type ChannelAssistant struct {
	Config core.Config
}

func NewChannelAssistant(cfg core.Config) *ChannelAssistant {
	return &ChannelAssistant{
		Config: cfg,
	}
}

func (c *ChannelAssistant) IsValid(conn *websocket.Conn) bool {
	// TODO: check connection
	return true
}

func (c *ChannelAssistant) Close(conn *websocket.Conn) (err error) {
	return conn.Close()
}

func (c *ChannelAssistant) GetLocalAddress(conn *websocket.Conn) net.Addr {
	return conn.LocalAddr()
}

func (c *ChannelAssistant) GetRemoteAddress(conn *websocket.Conn) net.Addr {
	return conn.RemoteAddr()
}

func (c *ChannelAssistant) Write(conn *websocket.Conn, frame *message.Frame) (err error) {
	var bbs = c.Config.GetCodec().Encode(frame)
	_, err = conn.NetConn().Write(bbs)
	return
}

func (c *ChannelAssistant) Read(conn *websocket.Conn) (*message.Frame, error) {
	// 读缓冲
	buf := make([]byte, c.Config.GetReadBufferSize())
	_, err := conn.NetConn().Read(buf[:4])
	if err != nil {
		slog.Warn(fmt.Sprintf("connect read error %s", err))
		return nil, err
	}
	ml := binary.BigEndian.Uint32(buf[:4])
	if ml > uint32(constant.MAX_SIZE_FRAME) {
		slog.Warn(fmt.Sprintf("message length %d is too large", ml))
		return nil, fmt.Errorf("message length %d is too large", ml)
	}
	conn.NetConn().Read(buf[4:ml])

	return c.Config.GetCodec().Decode(buf[:ml]), nil
}
