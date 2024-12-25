import React, { createContext, useState } from "react";
import {useNavigate} from "react-router";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const AuthContext = createContext(undefined);

export const AuthProvider = ({ children }) => {

    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

    const [isAuthenticated, setIsAuthenticated] = useState(() => {
        if (typeof window === "undefined") return false;
        return !!localStorage.getItem("accessToken") && !!localStorage.getItem("refreshToken");
    });

    const refreshToken = async (url) => {
        console.info("Trying to refresh token...");
        const storedRefreshToken = localStorage.getItem("refreshToken");
        if (!storedRefreshToken) {
            console.error("No refresh token found.");
            return null;
        }
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ refreshToken: storedRefreshToken }),
                credentials: 'include',
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
            const response = await authFetch(`/api/account/me/offline`, {
                method: "POST",
            });
            if (response.ok) {
                console.log("User marked offline successfully");
            } else {
                console.error("Failed to mark user offline");
            }
        } catch (error) {
            console.error("Error during logout:", error);
        } finally {
            setIsAuthenticated(false);
            localStorage.clear();
            navigate("/auth/login", { replace: true });
        }
    };


    const authFetch = async (url, options = {}) => {
        const accessToken = localStorage.getItem("accessToken");
        const headers = {
            ...options.headers,
            'Authorization': `Bearer ${accessToken}`,
        };
        const apiUrl = API_BASE_URL + url;
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 5000);
        const sendRequest = async (retry = false) => {
            try {
                const response = await fetch(apiUrl, {
                    method: options.method || 'GET',
                    headers,
                    body: options.body || null,
                    credentials: "include",
                    signal: controller.signal,
                });
                clearTimeout(timeoutId);
                if (response.ok) {
                    return response;
                }
                if (response.status === 401 && !retry) {
                    const newToken = await refreshToken(`${API_BASE_URL}/api/auth/refresh-token`);
                    if (!newToken) {
                        console.error("Unable to refresh token, logging out...");
                        await logout();
                        return Promise.reject("Failed to refresh token or re-authenticate.");
                    }
                    headers.Authorization = `Bearer ${newToken}`;
                    console.info("Retrying the request with new token...");
                    return sendRequest(true);
                }
                return Promise.reject(response);

            } catch (error) {
                clearTimeout(timeoutId);
                if (error.name === 'AbortError') {
                    toast.warn("Request timed out", {
                        position: toast.POSITION.TOP_CENTER,
                        autoClose: 5000,
                        hideProgressBar: false,
                        closeOnClick: true,
                        pauseOnHover: true,
                        draggable: true,
                        progress: undefined,
                    });
                }
                if (!retry) {
                    toast.warn("Your session has expired.<br />Please login again to continue", {
                        position: toast.POSITION.TOP_CENTER,
                        autoClose: 5000,
                        hideProgressBar: false,
                        closeOnClick: true,
                        pauseOnHover: true,
                        draggable: true,
                        progress: undefined,
                    });
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
