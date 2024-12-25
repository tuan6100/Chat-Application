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
    const { authFetch } = useAuth();
    const clientRef = useRef(null);
    const pollingRef = useRef(null);

    const markUserOnline = useCallback(async () => {
        try {
            const response = await authFetch(`/api/account/me/online`, {
                method: "POST",
            });
            if (response.ok) {
                setIsOnline(true);
            }
        } catch (error) {
            console.error("Error marking user online:", error);
        }
    }, [authFetch]);

    const markUserOffline = useCallback(async () => {
        try {
            const response = await authFetch(`/api/account/me/offline`, {
                method: "POST",
            });
            if (response.ok) {
                setIsOnline(false);
            }
        } catch (error) {
            console.error("Error marking user offline:", error);
        }
    }, [authFetch]);


    const connectWebSocket = useCallback(() => {
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
    }, []);


    useEffect(() => {
        const startPolling = () => {
            if (pollingRef.current) {
                clearInterval(pollingRef.current);
            }
            pollingRef.current = setInterval(() => {
                markUserOnline();
            }, 60000);
        };
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
        markUserOnline();
        connectWebSocket();
        startPolling();
        document.addEventListener('visibilitychange', handleVisibilityChange);
        window.addEventListener('beforeunload', handleBeforeUnload);

        return () => {
            markUserOffline();
            document.removeEventListener('visibilitychange', handleVisibilityChange);
            window.removeEventListener('beforeunload', handleBeforeUnload);
            if (pollingRef.current) {
                clearInterval(pollingRef.current);
            }
            if (clientRef.current) {
                clientRef.current.deactivate();
            }
        };
    }, [markUserOnline, markUserOffline, connectWebSocket]);


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
