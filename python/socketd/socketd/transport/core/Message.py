from abc import ABC, abstractmethod

from .Entity import Entity, Reply


class Message(Reply, ABC):
    @abstractmethod
    def is_request(self) -> bool:
        ...

    @abstractmethod
    def is_subscribe(self) -> bool:
        ...

    @abstractmethod
    def is_close(self) -> bool:
        ...

    @abstractmethod
    def get_sid(self) -> str:
        ...

    @abstractmethod
    def get_event(self) -> str:
        ...

    @abstractmethod
    def get_entity(self) -> Entity:
        ...


class MessageInternal(Message, ABC):
    @abstractmethod
    def get_flag(self) -> int: ...
