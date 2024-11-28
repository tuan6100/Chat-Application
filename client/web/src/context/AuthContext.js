import React, { createContext, useState, useEffect } from "react";

const AuthContext = createContext(undefined);

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(() => {
        if (typeof window === "undefined") return false;
        return !!localStorage.getItem("accessToken") && !!localStorage.getItem("refreshToken");
    });

    const refreshToken = async () => {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) return false;

        try {
            const response = await fetch("http://localhost:8000/api/auth/refresh-token", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ refreshToken }),
            });

            if (response.ok) {
                const { accessToken } = await response.json();
                localStorage.setItem("accessToken", accessToken);
                setIsAuthenticated(true);
                return true;
            }

            setIsAuthenticated(false);
            return false;
        } catch (error) {
            console.error("Error refreshing token:", error);
            setIsAuthenticated(false);
            return false;
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

    useEffect(() => {
        if (isAuthenticated) {
            const accessToken = localStorage.getItem("accessToken");
        }
    }, [isAuthenticated]);

    return (
        <AuthContext.Provider value={{
            isAuthenticated,
            setIsAuthenticated,
            logout,
            refreshToken
        }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
