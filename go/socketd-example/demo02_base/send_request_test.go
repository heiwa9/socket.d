package demo02_base

import (
	"fmt"
	"socketd"
	"socketd/transport/core/message"
	"testing"
	"time"
)

func TestDemo02SendAndRequest(t *testing.T) {
	SetLog()

	go func() {
		err := socketd.SocketD.Config(
			socketd.WithSchema("sd:ws"),
			socketd.WithHost("0.0.0.0"),
			socketd.WithPort(8602),
		).CreateServer().Listen(new(MyListner)).Start()
		if err != nil {
			t.Error(err)
		}
	}()

	time.Sleep(time.Second)

	clientSession, err := socketd.SocketD.Config(
		socketd.WithLink("sd:ws://127.0.0.1/?u=a&p=2"),
	).CreateClient().Open()
	if err != nil {
		t.Error(err)
	}
	request, err := clientSession.SendAndRequest("/demo", message.NewEntity(nil, []byte("hello world!")), 0)
	if err != nil {
		t.Error(err)
		return
	}
	msg, err := request.Await()
	if err != nil {
		fmt.Println("error:", err)
	} else {
		fmt.Println("msg:", msg)
	}
}
