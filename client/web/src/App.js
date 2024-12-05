import React from "react";
import { AuthProvider } from "./context/AuthContext";
import Router from "./route";

const App = () => {
    return (
        <AuthProvider>
            <Router />
        </AuthProvider>
    );
};

export default App;
