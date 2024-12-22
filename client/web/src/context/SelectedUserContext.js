import {createContext, useState} from "react";

const SelectedUserContext = createContext(undefined);

export const SelectedUserProvider = ({ children }) => {
    const [selectedUser, setSelectedUser] = useState(false);

    return (
        <SelectedUserContext.Provider value={{ selectedUser, setSelectedUser }}>
            {children}
        </SelectedUserContext.Provider>
    );
}

export default SelectedUserContext;