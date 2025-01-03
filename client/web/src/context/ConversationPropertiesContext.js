import { createContext, useEffect, useState, useCallback, useRef } from "react";
import useAuth from "../hook/useAuth";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const ConversationPropertiesContext = createContext(undefined);

export const ConversationPropertiesProvider = ({ children }) => {
    const [avatar, setAvatar] = useState(null);
    const [name, setName] = useState(null);
    const [isOnline, setIsOnline] = useState(false);
    const [lastOnlineTime, setLastOnlineTime] = useState(null);
    const { authFetch, isAuthenticated } = useAuth();
    const clientRef = useRef(null);
    const pollingRef = useRef(null);
    const onlineTimeoutRef = useRef(null);
    const isOnlineCheckStarted = useRef(false);
    const accountId = localStorage.getItem('accountId');

    const markUserOnline = useCallback(async () => {
        if (!isAuthenticated) return;
        try {
            const response = await fetch(`/api/account/me/online?accountId=${accountId}`, {
                method: "POST",
                credentials: "include",
            });
            if (response.ok) {
                isOnlineCheckStarted.current = true;
            }
        } catch (error) {
            console.error("Error marking user online:", error);
        }
    }, [authFetch, isAuthenticated, accountId]);

    const markUserOffline = useCallback(async () => {
        if (!isAuthenticated || !isOnlineCheckStarted.current) return;
        try {
            const response = await fetch(`/api/account/me/offline?accountId=${accountId}`, {
                method: "POST",
                credentials: "include",
            });
            if (response.ok) {
                setIsOnline(false);
            }
        } catch (error) {
            console.error("Error marking user offline:", error);
        }
    }, [authFetch, isAuthenticated, accountId]);

    const connectWebSocket = useCallback(() => {
        if (!isAuthenticated) return;

        if (clientRef.current) {
            clientRef.current.deactivate(() => {
                console.log("Deactivated existing WebSocket client.");
                activateWebSocket();
            });
        } else {
            activateWebSocket();
        }
    }, [isAuthenticated]);

    const activateWebSocket = () => {
        const socket = new SockJS(`${process.env.REACT_APP_API_BASE_URL}/ws`);
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
            heartbeatIncoming: 10000,
            heartbeatOutgoing: 10000,
            onConnect: () => {
                console.log("Connected to WebSocket");
                stompClient.subscribe('/client/online-status', (message) => {
                    const data = JSON.parse(message.body);
                    if (data.accountId === accountId) {
                        setIsOnline(data.isOnline);
                        setLastOnlineTime(data.lastOnlineTime);
                    }
                });

                setInterval(() => {
                    if (stompClient.connected) {
                        stompClient.publish({ destination: '/client/ping' });
                    }
                }, 30000);
            },
            onDisconnect: () => {
                console.log("Disconnected from WebSocket");
            },
            onStompError: (error) => {
                console.error("STOMP Error:", error);
            }
        });

        stompClient.activate();
        clientRef.current = stompClient;
    };

    const reconnectWebSocket = () => {
        if (clientRef.current && !clientRef.current.connected) {
            console.log("Reconnecting WebSocket...");
            clientRef.current.activate();
        }
    };


    const startPolling = () => {
        if (pollingRef.current) {
            clearInterval(pollingRef.current);
        }
        pollingRef.current = setInterval(() => {
            reconnectWebSocket();
        }, 10000);
    };

    const clearPolling = () => {
        if (pollingRef.current) {
            clearInterval(pollingRef.current);
            pollingRef.current = null;
        }
    };

    useEffect(() => {
        if (!isAuthenticated) return;

        const startOnlineCheck = () => {
            markUserOnline();
            connectWebSocket();
            startPolling();
        };

        onlineTimeoutRef.current = setTimeout(() => {
            startOnlineCheck();
            isOnlineCheckStarted.current = true;
        }, 5000);

        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') {
                markUserOnline();
                reconnectWebSocket();
            } else {
                markUserOffline();
            }
        };

        const handleBeforeUnload = () => {
            markUserOffline();
        };

        const handlePageHide = () => {
            markUserOffline();
        };

        const handlePageShow = () => {
            markUserOnline();
            reconnectWebSocket();
        };

        document.addEventListener('visibilitychange', handleVisibilityChange);
        window.addEventListener('beforeunload', handleBeforeUnload);
        window.addEventListener('pagehide', handlePageHide);
        window.addEventListener('pageshow', handlePageShow);

        return () => {
            if (isOnlineCheckStarted.current) {
                markUserOffline();
            }
            document.removeEventListener('visibilitychange', handleVisibilityChange);
            window.removeEventListener('beforeunload', handleBeforeUnload);
            window.removeEventListener('pagehide', handlePageHide);
            window.removeEventListener('pageshow', handlePageShow);

            clearPolling();

            if (clientRef.current) {
                clientRef.current.deactivate();
            }
            if (onlineTimeoutRef.current) {
                clearTimeout(onlineTimeoutRef.current);
            }
        };
    }, [markUserOnline, markUserOffline, connectWebSocket, isAuthenticated]);

    return (
        <ConversationPropertiesContext.Provider
            value={{
                avatar, setAvatar,
                name, setName,
                isOnline, setIsOnline,
                lastOnlineTime, setLastOnlineTime,
                markUserOnline, markUserOffline
            }}
        >
            {children}
        </ConversationPropertiesContext.Provider>
    );
};

export default ConversationPropertiesContext;
