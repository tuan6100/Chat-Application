import React from "react";
import { AuthProvider } from "./context/AuthContext";
import Router from "./route";
// import ThemeProvider from "./theme";


function App() {
    return (
            <AuthProvider>
                <Router />
            </AuthProvider>
    );
}

export default App;
