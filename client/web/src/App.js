import React from "react";
import { AuthProvider } from "./context/AuthContext"; // Import AuthProvider
import Router from "./route"; // Import Router

const App = () => {
    return (
        <AuthProvider>
            <Router /> {/* Dùng Router bên trong AuthProvider để đảm bảo context hoạt động */}
        </AuthProvider>
    );
};

export default App;
