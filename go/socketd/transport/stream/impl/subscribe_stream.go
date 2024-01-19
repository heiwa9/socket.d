package impl

import (
	"socketd/transport/core/constant"
	"socketd/transport/core/message"
	"socketd/transport/stream"
	"time"
)

type SubscribeStream struct {
	*StreamBase
	doOnReply func(message *message.Message)
	replyChan chan *message.Message
	done      bool
}

func NewSubscribeStream(sid string, timeout time.Duration) *SubscribeStream {
	return &SubscribeStream{StreamBase: NewStreamBase(sid, constant.DEMANDS_MULTIPLE, timeout), replyChan: make(chan *message.Message)}
}

func (s *SubscribeStream) IsDone() bool {
	return s.done
}

func (s *SubscribeStream) OnReply(frame *message.Frame) {
	s.done = frame.IsEnd()
	s.replyChan <- frame.Message
}

func (s *SubscribeStream) ThenReply(onReply func(message *message.Message)) stream.SubscribeStream {
	s.doOnReply = onReply
	return s
}
