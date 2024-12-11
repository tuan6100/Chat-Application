import React from "react";
import { AuthProvider } from "./context/AuthContext";
import Router from "./route";
import ThemeProvider from "./theme";
import {SettingProvider} from "./context/SettingContext";

function App() {
    return (
        <ThemeProvider>
            <AuthProvider>
                <SettingProvider>
                    <Router />
                </SettingProvider>
            </AuthProvider>
        </ThemeProvider>
    );
}

export default App;
