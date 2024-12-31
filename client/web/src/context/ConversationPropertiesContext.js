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
            const response = await authFetch(`/api/account/me/online?accountId=${accountId}`, {
                method: "POST",
            });
            if (response.ok) {
                setIsOnline(true);
                isOnlineCheckStarted.current = true;
            }
        } catch (error) {
            console.error("Error marking user online:", error);
        }
    }, [authFetch, isAuthenticated, accountId]);

    const markUserOffline = useCallback(async () => {
        if (!isAuthenticated || !isOnlineCheckStarted.current) return;
        try {
            const response = await authFetch(`/api/account/me/offline?accountId=${accountId}`, {
                method: "POST",
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
            clientRef.current.deactivate();
        }
        const socket = new SockJS(`${process.env.REACT_APP_API_BASE_URL}/ws`);
        const stompClient = new Client({
            webSocketFactory: () => socket,
            onConnect: () => {
                console.log("Connected to WebSocket");
                stompClient.subscribe('/client/online-status', (message) => {
                    const data = JSON.parse(message.body);
                    if (data.accountId === localStorage.getItem('accountId')) {
                        setIsOnline(data.isOnline);
                        setLastOnlineTime(data.lastOnlineTime);
                    }
                });
                setInterval(() => {
                    stompClient.publish({ destination: '/client/ping' });
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
    }, [isAuthenticated]);

    const startPolling = () => {
        if (pollingRef.current) {
            clearInterval(pollingRef.current);
        }
        pollingRef.current = setInterval(() => {
            markUserOnline();
        }, 60000);
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
        }, 60000);

        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') {
                markUserOnline();
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
            window.addEventListener('pagehide', handlePageHide);
            window.addEventListener('pageshow', handlePageShow);
            clearPolling();
            if (clientRef.current) {
                clientRef.current.deactivate();
            }
            if (onlineTimeoutRef.current) {
                clearTimeout(onlineTimeoutRef.current);
            }
        };
        // eslint-disable-next-line
    }, [markUserOnline, markUserOffline, connectWebSocket, isAuthenticated]);

    useEffect(() => {
        if (!isAuthenticated && clientRef.current) {
            clientRef.current.deactivate();
        }
    }, [isAuthenticated]);

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