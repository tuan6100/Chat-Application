// AuthContext.js

import React, { createContext, useState, useEffect } from 'react';

const AuthContext = createContext(undefined);

export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem("accessToken");
        setIsAuthenticated(!!token);
    }, []);

    const refreshToken = async () => {
        const refreshToken = localStorage.getItem("refreshToken");

        if (!refreshToken) {
            return null;
        }

        try {
            const response = await fetch("http://localhost:8000/api/auth/refresh-token", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ refreshToken }),
            });

            if (response.ok) {
                const { accessToken } = await response.json();
                localStorage.setItem("accessToken", accessToken);
                setIsAuthenticated(true);
                return accessToken;
            }
            return null;
        } catch (error) {
            console.error("Error refreshing token:", error);
            return null;
        }
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, setIsAuthenticated, refreshToken }}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
