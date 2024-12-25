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
import {SelectedUserProvider} from "./context/SelectedUserContext";
import {FriendsProvider} from "./context/FriendsListContext";
import {ConversationPropertiesProvider} from "./context/ConversationPropertiesContext";

function App() {
    useApplyTheme();
    return (
        <ThemeProvider>
            <AuthProvider>
                <SettingProvider>
                    <ThemeSettings>
                        <SidebarProvider>
                            <SearchProvider>
                                <SelectedUserProvider>
                                    <FriendsProvider>
                                        <ConversationPropertiesProvider>
                                            <Router />
                                            <ToastContainer />
                                        </ConversationPropertiesProvider>
                                    </FriendsProvider>
                                </SelectedUserProvider>
                            </SearchProvider>
                        </SidebarProvider>
                    </ThemeSettings>
                </SettingProvider>
            </AuthProvider>
        </ThemeProvider>
    );
}

export default App;
