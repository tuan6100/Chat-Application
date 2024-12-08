import React, { createContext, useState } from "react";

const AuthContext = createContext(undefined);

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(() => {
        if (typeof window === "undefined") return false;
        return !!localStorage.getItem("accessToken") && !!localStorage.getItem("refreshToken");
    });

    const refreshToken = async () => {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) return null;
        try {
            const response = await fetch('/api/auth/refresh-token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ refreshToken }),
            });
            if (!response.ok) {
                console.error('Failed to refresh token.');
                setIsAuthenticated(false);
            }
            const data = await response.json();
            localStorage.setItem('accessToken', data.accessToken);
            setIsAuthenticated(true);
            return data.accessToken;
        } catch (error) {
            console.error("Error refreshing token:", error);
            return null;
        }
    };


    const logout = async () => {
        if (typeof window !== "undefined") {
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
        }
        try {
            const response = await fetch("http://localhost:8000/api/auth/logout", {
                method: "DELETE",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
            });
            if (response.ok) {
                setIsAuthenticated(false);
            } else {
                console.error("Error during logout:", response.status);
            }
        } catch (error) {
            console.error("Error logging out:", error);
        }
    };

    const authFetch = async (url, options = {}) => {
        const accessToken = localStorage.getItem("accessToken");
        const headers = {
            ...options.headers,
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
        };
        const sendRequest = async (retry = false) => {
            const response = await fetch(url, {
                method: options.method || 'GET',
                headers,
                body: options.body || null,
                credentials: options.credentials,
            });
            if (response.status === 401 && !retry) {
                try {
                    const newToken = await refreshToken();
                    if (!newToken) {
                        throw new Error("Unable to refresh token");
                    }
                    headers.Authorization = `Bearer ${newToken}`;
                    return sendRequest(true);
                } catch (error) {
                    console.error("Failed to refresh token or re-authenticate:", error);
                    await logout();
                    return Promise.reject(error);
                }
            }
            return response;
        };
        return sendRequest();
    };

    return (
        <AuthContext.Provider value={{
            isAuthenticated,
            setIsAuthenticated,
            authFetch,
            logout,
            refreshToken
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
