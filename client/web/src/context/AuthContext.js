import React, { createContext, useState } from "react";
import {useNavigate} from "react-router";

const AuthContext = createContext(undefined);

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(() => {
        if (typeof window === "undefined") return false;
        return !!localStorage.getItem("accessToken") && !!localStorage.getItem("refreshToken");
    });

    const refreshToken = async () => {
        const storedRefreshToken = localStorage.getItem("refreshToken");
        if (!storedRefreshToken) {
            console.error("No refresh token found.");
            return null;
        }
        try {
            const response = await fetch('http://localhost:8000/api/auth/refresh-token', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ refreshToken: storedRefreshToken }),
            });
            if (!response.ok) {
                console.error("Failed to refresh token:", response.statusText);
                setIsAuthenticated(false);
                return null;
            }
            const data = await response.json();
            if (!data.accessToken) {
                console.error("No access token received in response.");
                setIsAuthenticated(false);
                return null;
            }
            localStorage.setItem("accessToken", data.accessToken);
            setIsAuthenticated(true);
            console.info("Token refreshed successfully.");
            return data.accessToken;
        } catch (error) {
            console.error("Error during token refresh:", error);
            setIsAuthenticated(false);
            return null;
        }
    };

    const navigate = useNavigate();

    const logout = async () => {
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
        } finally {
            if (typeof window !== "undefined") {
                localStorage.clear();
            }
            navigate("/auth/login");
        }
    };

    const authFetch = async (url, options = {}) => {
        const fullUrl = 'http://localhost:8000' + url;
        const accessToken = localStorage.getItem("accessToken");
        const headers = {
            ...options.headers,
            Authorization: `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
        };

        const sendRequest = async (retry = false) => {
            try {
                const response = await fetch(fullUrl, {
                    method: options.method || 'GET',
                    headers,
                    body: options.body || null,
                    credentials: options.credentials,
                });
                if ((response.status === 401 || response.status === 500) && !retry) {
                    console.warn("Access token expired, attempting to refresh...");
                    const newToken = await refreshToken();
                    if (!newToken) {
                        console.error("Unable to refresh token, logging out...");
                        await logout();
                        return Promise.reject("Failed to refresh token or re-authenticate.");
                    }
                    headers.Authorization = `Bearer ${newToken}`;
                    console.info("Retrying the request with new token...");
                    return sendRequest(true);
                }
                return response;
            } catch (error) {
                console.error("Error during authFetch:", error);
                if (!retry) {
                    await logout();
                }
                return Promise.reject(error);
            }
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
