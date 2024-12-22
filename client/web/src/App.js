import React from "react";
import { AuthProvider } from "./context/AuthContext";
import Router from "./route";
import ThemeProvider from "./theme";
import {SettingProvider} from "./context/SettingContext";
import { SidebarProvider } from "./context/SidebarContext";
import ThemeSettings from "./component/Custom";
import useApplyTheme from "./hook/useApplyTheme";

function App() {
    useApplyTheme();
    return (
        <ThemeProvider>
            <AuthProvider>
                <SettingProvider>
                    <ThemeSettings>
                        <SidebarProvider>
                            <Router />
                        </SidebarProvider>
                    </ThemeSettings>
                </SettingProvider>
            </AuthProvider>
        </ThemeProvider>
    );
}

export default App;
