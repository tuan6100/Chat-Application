import React from "react";
import { AuthProvider } from "./context/AuthContext";
import Router from "./route";
import ThemeProvider from "./theme";
import {SettingProvider} from "./context/SettingContext";
import ThemeSettings from "./component/Setting";
import useApplyTheme from "./hook/useApplyTheme";

function App() {
    useApplyTheme();
    return (
        <ThemeProvider>
            <AuthProvider>
                <SettingProvider>
                    <ThemeSettings>
                        <Router />
                    </ThemeSettings>
                </SettingProvider>
            </AuthProvider>
        </ThemeProvider>
    );
}

export default App;
