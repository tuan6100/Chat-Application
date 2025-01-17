import React, { createContext, useState, useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import useMessage from "../hook/useMessage";
import useAuth from "../hook/useAuth";

const WebSocketContext = createContext(undefined);
export const WebSocketProvider = ({ children }) => {
    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
    const stompClient = useRef(null);
    const subscriptions = useRef(new Map());
    const { addNewMessage, finalMessagesMap, setFinalMessagesMap, setTypingUsers } = useMessage();
    const chatIdList = JSON.parse(localStorage.getItem("chatIdList")) || [];
    const {isAuthenticated} = useAuth();
    const [isWebSocketActive, setIsWebSocketActive] = useState(true);

    useEffect(() => {
        setIsWebSocketActive(isAuthenticated);
    }, [isAuthenticated]);

    useEffect(() => {
        const connectWebSocket = () => {
            const socket = new SockJS(`${API_BASE_URL}/ws`);
            stompClient.current = new Client({
                webSocketFactory: () => socket,
                reconnectDelay: 1000,
                heartbeatIncoming: 10000,
                heartbeatOutgoing: 10000,
                onConnect: () => {
                    console.log("WebSocket connected");
                    resubscribeAll();
                },
                onDisconnect: () => {
                    console.log("WebSocket disconnected");
                },
                onStompError: (error) => {
                    console.error("STOMP error:", error);
                },
            });
            stompClient.current.activate();
        };
        if (isWebSocketActive) {
            if (!stompClient.current || !stompClient.current.connected) {
                connectWebSocket();
            }
        } else {
            if (stompClient.current && stompClient.current.connected) {
                stompClient.current.deactivate(() => {
                    console.log("WebSocket manually deactivated");
                });
            }
        }
        return () => {
            if (stompClient.current) {
                stompClient.current.deactivate(() => console.log("WebSocket disconnected on cleanup"));
            }
        };
    }, [isWebSocketActive, API_BASE_URL]);


    const subscribe = (destination, callback) => {
        if (stompClient.current && stompClient.current.connected) {
            if (!subscriptions.current.has(destination)) {
                console.log(`Subscribing to: ${destination}`);
                const subscription = stompClient.current.subscribe(destination, callback);
                subscriptions.current.set(destination, { subscription, callback });
            }
        } else {
            console.error(`WebSocket not connected. Cannot subscribe to: ${destination}`);
        }
    };


    const unsubscribe = (destination) => {
        const subscription = subscriptions.current.get(destination)?.subscription;
        if (subscription) {
            subscription.unsubscribe();
            subscriptions.current.delete(destination);
        }
    };

    const publish = (destination, body) => {
        if (stompClient.current && stompClient.current.connected) {
            stompClient.current.publish({ destination: destination, body: body });
        }
    }

    const resubscribeAll = () => {
        subscriptions.current.forEach((_, destination) => {
            if (stompClient.current && stompClient.current.connected) {
                console.log(`Resubscribing to: ${destination}`);
                const callback = subscriptions.current.get(destination).callback;
                const subscription = stompClient.current.subscribe(destination, callback);
                subscriptions.current.set(destination, subscription);
            }
        });
    };


    useEffect(() => {
        if (!isWebSocketActive) {
            return;
        }
        const interval = setInterval(() => {
            if (!stompClient.current || !stompClient.current.connected) {
                console.warn("WebSocket is not connected. Reconnecting...");
                try {
                    stompClient.current.activate();
                    console.log("Reconnecting WebSocket...");
                } catch (error) {
                    console.error("Error while reconnecting WebSocket:", error);
                }
            } else {
                try {
                    publish(`/chat/ping`, "Ping");
                    console.log("Ping");
                    subscribe(`/client/pong`, (message) => {
                        console.log(JSON.stringify(message));
                    });
                } catch (error) {
                    console.error("Failed to send ping. Reconnecting...");
                    stompClient.current.deactivate(() => {
                        console.log("WebSocket deactivated");
                        stompClient.current.activate();
                    });
                }
            }
        }, 10000);
        return () => clearInterval(interval);
    }, []);


    useEffect(() => {
        if (!chatIdList) return;
        if (stompClient.current && stompClient.current.connected && isWebSocketActive) {
            chatIdList.forEach((chatId) => {
                chatId = parseInt(chatId);
                subscribe(`/client/chat/${chatId}`, (message) => {
                    const data = JSON.parse(message.body);
                    addNewMessage(chatId, data);
                });
            });
        }
        return () => {
            chatIdList.forEach((chatId) => {
                unsubscribe(`/client/chat/${chatId}`);
            });
        };
    }, [stompClient.current, chatIdList, isWebSocketActive]);


    useEffect(() => {
        if (!chatIdList) return;
        if (stompClient.current && stompClient.current.connected && isWebSocketActive) {
            chatIdList.forEach((chatId) => {
                subscribe(`/client/chat/${chatId}/typing`, (message) => {
                    if (JSON.parse(message.body) === "pong") {
                        return;
                    }
                    const {senderId, senderAvatar, typing, chatId: incomingChatId} = JSON.parse(message.body);
                    console.log("Typing message received:", {senderId, senderAvatar, typing, incomingChatId});
                    if (senderId !== localStorage.getItem("accountId")) {
                        if (typing) {
                            setTypingUsers((prev) => ({
                                ...prev,
                                [incomingChatId]: {
                                    ...prev[incomingChatId],
                                    [senderId]: {senderAvatar, typing},
                                },
                            }));
                        } else {
                            setTypingUsers((prev) => {
                                const updated = {...prev};
                                if (updated[incomingChatId]) {
                                    delete updated[incomingChatId][senderId];
                                    if (Object.keys(updated[incomingChatId]).length === 0) {
                                        delete updated[incomingChatId];
                                    }
                                }
                                return updated;
                            });
                        }
                    }
                });
            });
        }
        return () => {
            chatIdList.forEach((chatId) => {
                unsubscribe(`/client/chat/${chatId}/typing`);
            });
        };
    }, [chatIdList, subscribe]);


    useEffect(() => {
        if (stompClient.current && stompClient.current.connected && isWebSocketActive) {
            chatIdList.forEach((chatId) => {
                subscribe(`/client/chat/${chatId}/message/mark-seen`, (message) => {
                    const data = JSON.parse(message.body);
                    if (data === "pong") {
                        return;
                    }
                    setFinalMessagesMap((prev) => {
                        const updatedFinal = new Map(prev);
                        if (updatedFinal.has(chatId)) {
                            const messages = updatedFinal.get(chatId);
                            const messageIndex = messages.findIndex(msg => msg.messageId === data.messageId);
                            if (messageIndex !== -1) {
                                messages[messageIndex] = { ...messages[messageIndex], ...data };
                            } else {
                                messages.push(data);
                            }
                            updatedFinal.set(chatId, messages);
                        } else {
                            updatedFinal.set(chatId, [data]);
                        }
                        return updatedFinal;
                    });
                });
            });
        }

        return () => {
            chatIdList.forEach((chatId) => {
                unsubscribe(`/client/chat/${chatId}/message/mark-seen`);
            });
        };
    }, [chatIdList, subscribe]);


    return (
        <WebSocketContext.Provider value={{ subscribe, unsubscribe, publish, isWebSocketActive, setIsWebSocketActive }}>
            {children}
        </WebSocketContext.Provider>
    );
};

export default WebSocketContext;