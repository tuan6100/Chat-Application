import { createContext, useEffect, useState, useCallback, useRef } from "react";
import useAuth from "../hook/useAuth";


const ConversationPropertiesContext = createContext(undefined);

export const ConversationPropertiesProvider = ({ children }) => {

    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
    const [avatar, setAvatar] = useState(null);
    const [name, setName] = useState(null);
    const [isOnline, setIsOnline] = useState(false);
    const [lastOnlineTime, setLastOnlineTime] = useState(null);
    const {isAuthenticated } = useAuth();
    const clientRef = useRef(null);
    const pollingRef = useRef(null);
    const onlineTimeoutRef = useRef(null);
    const isOnlineCheckStarted = useRef(false);
    const accountId = localStorage.getItem('accountId');


    const markUserOnline = useCallback(async () => {
        if (!isAuthenticated) return;
        try {
            const response = await fetch(`${API_BASE_URL}/api/account/me/online?accountId=${accountId}`, {
                method: "POST",
                credentials: "include",
            });
            if (response.ok) {
                isOnlineCheckStarted.current = true;
            }
        } catch (error) {
            console.error("Error marking user online:", error);
        }
    }, [isAuthenticated, accountId]);

    const markUserOffline = useCallback(async () => {
        if (!isAuthenticated || !isOnlineCheckStarted.current) return;
        try {
            const response = await fetch(`${API_BASE_URL}/api/account/me/offline?accountId=${accountId}`, {
                method: "POST",
                credentials: "include",
            });
            if (response.ok) {
                setIsOnline(false);
            }
        } catch (error) {
            console.error("Error marking user offline:", error);
        }
    }, [isAuthenticated, accountId]);


    const markUserOfflineSync = () => {
        if (isAuthenticated && isOnlineCheckStarted.current) {
            const url = `${process.env.REACT_APP_API_BASE_URL}/api/account/me/offline?accountId=${accountId}`;
            navigator.sendBeacon(url);
        }
    };

    useEffect(() => {
        if (!isAuthenticated) return;
        const startOnlineCheck = () => {
            markUserOnline();
        };
        onlineTimeoutRef.current = setTimeout(() => {
            startOnlineCheck();
            isOnlineCheckStarted.current = true;
        }, 5000);
        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') {
                markUserOnline();
            } else {
                markUserOffline();
            }
        };
        const handleBeforeUnload = () => {
            markUserOfflineSync();
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
            window.removeEventListener('pagehide', handlePageHide);
            window.removeEventListener('pageshow', handlePageShow);
            if (onlineTimeoutRef.current) {
                clearTimeout(onlineTimeoutRef.current);
            }
        };
    }, [markUserOnline, markUserOffline, isAuthenticated]);


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
