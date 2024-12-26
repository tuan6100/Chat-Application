import {createContext, useEffect, useState} from "react";

const SelectedContext = createContext(undefined);

export const SelectedProvider = ({ children }) => {
    const [hasSelected, setSelected] = useState(false);
    const [selectedUser, setSelectedUser] = useState(-1);
    const [selectedGroup, setSelectedGroup] = useState(-1);

    useEffect(() => {
        const selected = selectedUser !== -1 || selectedGroup !== -1;
        setSelected(selected);
    }, [selectedGroup, selectedUser]);

    return (
        <SelectedContext.Provider
            value={{ selectedUser, setSelectedUser,
                     hasSelected, setSelected,
                     selectedGroup, setSelectedGroup }}
        >
            {children}
        </SelectedContext.Provider>
    );
}

export default SelectedContext;