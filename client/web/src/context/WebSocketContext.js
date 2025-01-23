import React, {createContext, useState, useEffect, useRef, useCallback} from "react";
import { Client } from "@stomp/stompjs";
import useMessage from "../hook/useMessage";
import useAuth from "../hook/useAuth";

const WebSocketContext = createContext(undefined);
export const WebSocketProvider = ({ children }) => {

    const WS_BASE_URL = process.env.REACT_APP_WS_BASE_URL;
    const stompClient = useRef(null);
    const subscriptions = useRef(new Map());
    const { addNewMessage, finalMessagesMap, setFinalMessagesMap,
        setTypingUsers, updateChatDataInSession, setToggleNewMessage }
        = useMessage();
    const chatList = JSON.parse(localStorage.getItem('chatList')) || [];
    const chatIdList = chatList.map(chat => chat.chatId);
    const {isAuthenticated} = useAuth();
    const [isRefreshed, setIsRefreshed] = useState(false);


    useEffect(() => {
        const handleBeforeUnload = () => {
            sessionStorage.setItem('isRefreshed', 'true');
        };
        const checkRefresh = () => {
            const refreshed = sessionStorage.getItem('isRefreshed') === 'true';
            setIsRefreshed(refreshed);
            if (refreshed) {
                console.log('The tab was refreshed: ' + refreshed);
                setIsRefreshed(refreshed);
                sessionStorage.removeItem('isRefreshed');
                chatIdList.forEach((chatId) => {
                    publish(`/client/chat/${chatId}/message/send`, {});
                    publish(`/chat/${chatId}/message/send`, {});
                });
            } else {
                console.log('The tab was not refreshed');
            }
        };
        window.addEventListener('beforeunload', handleBeforeUnload);
        window.addEventListener('load', checkRefresh);
        return () => {
            window.removeEventListener('beforeunload', handleBeforeUnload);
            window.removeEventListener('load', checkRefresh);
        };
    }, []);


    useEffect(() => {
        const connectWebSocket = () => {
            const socketUrl = `${WS_BASE_URL}/ws`;
            const socket = new WebSocket(socketUrl);
            stompClient.current = new Client({
                webSocketFactory: () => socket,
                reconnectDelay: 1000,
                heartbeatIncoming: 10000,
                heartbeatOutgoing: 10000,
                onConnect: () => {
                    console.log("WebSocket connected");
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
        if (isAuthenticated) {
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
    }, [isAuthenticated, WS_BASE_URL]);


    const subscribe = useCallback((destination, callback) => {
        if (stompClient.current && stompClient.current.connected) {
            if (!subscriptions.current.has(destination)) {
                console.log(`Subscribing to: ${destination}`);
                const subscription = stompClient.current.subscribe(destination, callback);
                subscriptions.current.set(destination, { subscription, callback });
            }
        } else {
            console.error(`WebSocket not connected. Cannot subscribe to: ${destination}`);
        }
    }, []);


    const unsubscribe = useCallback((destination) => {
        const subscription = subscriptions.current.get(destination)?.subscription;
        if (subscription) {
            subscription.unsubscribe();
            subscriptions.current.delete(destination);
        }
    }, []);

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
        if (!isAuthenticated) {
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
        }, 60000);
        return () => clearInterval(interval);
    }, []);


    useEffect(() => {
        if (!chatIdList) return;
        if (stompClient.current && stompClient.current.connected ) {
            chatIdList.forEach((chatId) => {
                chatId = parseInt(chatId);
                subscribe(`/client/chat/${chatId}/message/send`, (message) => {
                    const data = JSON.parse(message.body);
                    addNewMessage(chatId, data);
                });
            });
        }
        return () => {
            chatIdList.forEach((chatId) => {
                unsubscribe(`/client/chat/${chatId}/message/send`);
            });
        };
    }, [chatIdList, subscribe]);


    useEffect(() => {
        if (!chatIdList) return;
        if (stompClient.current && stompClient.current.connected) {
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
                                    [senderId]: {senderAvatar, typing, chatId},
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
        if (stompClient.current && stompClient.current.connected ) {
            chatIdList.forEach((chatId) => {
                subscribe(`/client/chat/${chatId}/message/mark-seen`, (message) => {
                    const data = JSON.parse(message.body);
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
                    console.log("Marked as seen for message: ", data);
                });
            });
        }
        return () => {
            chatIdList.forEach((chatId) => {
                unsubscribe(`/client/chat/${chatId}/message/mark-seen`);
            });
        };
    }, [chatIdList, subscribe]);


    useEffect(() => {
        if (stompClient.current && stompClient.current.connected ) {
            chatIdList.forEach((chatId) => {
                subscribe(`/client/chat/${chatId}/message/update`, (message) => {
                    const data = JSON.parse(message.body);
                    setFinalMessagesMap((prev) => {
                        const updatedFinal = new Map(prev);
                        if (updatedFinal.has(chatId)) {
                            const messages = [...updatedFinal.get(chatId)];
                            const messageIndex = messages.findIndex(msg => msg.messageId === data.messageId);
                            if (messageIndex !== -1) {
                                console.log("Message found, updating...");
                                messages[messageIndex] = { ...messages[messageIndex], ...data };
                            } else {
                                console.log("Message not found, adding...");
                                messages.push(data);
                            }
                            updatedFinal.set(chatId, messages);
                        } else {
                            updatedFinal.set(chatId, [data]);
                        }
                        return updatedFinal;
                    });
                    console.log("Message edited: ", data);
                });
            });
        }
        return () => {
            chatIdList.forEach((chatId) => {
                unsubscribe(`/client/chat/${chatId}/message/update`);
            });
        };
    }, [chatIdList, subscribe]);


    useEffect(() => {
        if (stompClient.current && stompClient.current.connected) {
            chatIdList.forEach((chatId) => {
                subscribe(`/client/chat/${chatId}/message/delete`, (message) => {
                    const data = JSON.parse(message.body);
                    setFinalMessagesMap((prev) => {
                        const updatedFinal = new Map(prev);
                        if (updatedFinal.has(chatId)) {
                            const messages = [...updatedFinal.get(chatId)];
                            const messageIndex = messages.findIndex(msg => msg.messageId === data.messageId);
                            if (messageIndex !== -1) {
                                console.log("Message found, deleting...");
                                messages[messageIndex] = { ...messages[messageIndex], ...data };
                                console.log("Message deleted: ", messages[messageIndex]);
                                messages.forEach((msg, index) => {
                                    if (msg.replyToMessageId === data.messageId) {
                                        messages[index] = { ...msg, replyToMessageId: null, replyToMessageContent: null };
                                        console.log("Updated message replying to deleted message: ", messages[index]);
                                    }
                                });
                                updatedFinal.set(chatId, messages);
                                setToggleNewMessage(null);
                            }
                        }
                        return updatedFinal;
                    });
                });
            });
        }
        return () => {
            chatIdList.forEach((chatId) => {
                unsubscribe(`/client/chat/${chatId}/message/delete`);
            });
        };
    }, [chatIdList, subscribe]);


    useEffect(() => {
        if (stompClient.current && stompClient.current.connected) {
            chatIdList.forEach((chatId) => {
                subscribe(`/client/chat/${chatId}/message/restore`, (message) => {
                    const data = JSON.parse(message.body);
                    setFinalMessagesMap((prev) => {
                        const updatedFinal = new Map(prev);
                        if (updatedFinal.has(chatId)) {
                            const messages = [...updatedFinal.get(chatId)];
                            const messageIndex = messages.findIndex(msg => msg.messageId === data.messageId);
                            if (messageIndex !== -1) {
                                console.log("Message found, restoring...");
                                messages[messageIndex] = { ...messages[messageIndex], ...data };
                                console.log("Message restored: ", messages[messageIndex]);
                                messages.forEach((msg, index) => {
                                    if (msg.replyToMessageId === data.messageId) {
                                        messages[index] = { ...msg, replyToMessageId: message.messageId, replyToMessageContent: message.content };
                                        console.log("Updated message replying to deleted message: ", messages[index]);
                                    }
                                });
                                updatedFinal.set(chatId, messages);
                                setToggleNewMessage(null);
                            } else {
                                console.log("Message not found, cannot restore.");
                            }
                        } else {
                            console.log("Chat ID not found in final messages map.");
                        }
                        return updatedFinal;
                    });
                });
            });
        }
        return () => {
            chatIdList.forEach((chatId) => {
                unsubscribe(`/client/chat/${chatId}/message/restore`);
            });
        };
    }, [chatIdList, subscribe]);


    useEffect(() => {
        chatIdList.forEach((chatId) => {
            updateChatDataInSession(chatId, finalMessagesMap.get(chatId) || []);
        })
    }, [chatIdList, finalMessagesMap]);


    return (
        <WebSocketContext.Provider value={{ subscribe, unsubscribe, publish}}>
            {children}
        </WebSocketContext.Provider>
    );
};

export default WebSocketContext;