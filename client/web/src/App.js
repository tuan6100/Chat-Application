import React from "react";
import { AuthProvider } from "./context/AuthContext";
import Router from "./route";
import ThemeProvider from "./theme";
import {SettingProvider} from "./context/SettingContext";
import { SidebarProvider } from "./context/SidebarContext";
import ThemeSettings from "./component/Custom";
import useApplyTheme from "./hook/useApplyTheme";
import {SearchProvider} from "./context/SearchResultContext";
import { ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {SelectedProvider} from "./context/SelectedContext";
import {ConversationPropertiesProvider} from "./context/ConversationPropertiesContext";
import {MessageProvider} from "./context/MessageContext";
import {WebSocketProvider} from "./context/WebSocketContext";


function App() {
    useApplyTheme();
    return (
        <ThemeProvider>
            <AuthProvider>
                <SettingProvider>
                    <ThemeSettings>
                        <SidebarProvider>
                            <SearchProvider>
                                <SelectedProvider>
                                    <ConversationPropertiesProvider>
                                        <MessageProvider>
                                            <WebSocketProvider>
                                                <Router />
                                                <ToastContainer />
                                            </WebSocketProvider>
                                        </MessageProvider>
                                    </ConversationPropertiesProvider>
                                </SelectedProvider>
                            </SearchProvider>
                        </SidebarProvider>
                    </ThemeSettings>
                </SettingProvider>
            </AuthProvider>
        </ThemeProvider>
    );
}

export default App;
