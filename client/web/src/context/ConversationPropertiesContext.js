import { createContext, useEffect, useState, useCallback } from "react";
import useAuth from "../hook/useAuth";

const ConversationPropertiesContext = createContext(undefined);

export const ConversationPropertiesProvider = ({ children }) => {

    const [avatar, setAvatar] = useState(null);
    const [name, setName] = useState(null);
    const [isOnline, setIsOnline] = useState(false);
    const [lastOnlineTime, setLastOnlineTime] = useState(null);

    const { authFetch } = useAuth();

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

    useEffect(() => {
        const handleVisibilityChange = () => {
            if (document.visibilityState === 'visible') {
                markUserOnline();
            } else {
                markUserOffline();
            }
        };
        document.addEventListener('visibilitychange', handleVisibilityChange);
        markUserOnline();
        return () => {
            markUserOffline();
            document.removeEventListener('visibilitychange', handleVisibilityChange);
        };
    }, [markUserOnline, markUserOffline]);

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