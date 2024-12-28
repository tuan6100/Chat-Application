import {createContext, useEffect, useState} from "react";

const SelectedContext = createContext(undefined);

export const SelectedProvider = ({ children }) => {
    const [isChatOpen, setChatOpen] = useState(true);
    const [hasSelected, setHasSelected] = useState(false);
    const [selectedUser, setSelectedUser] = useState(-1);
    const [selectedGroup, setSelectedGroup] = useState(-1);

    useEffect(() => {
        const selected = selectedUser !== -1 || selectedGroup !== -1;
        setHasSelected(selected);
    }, [selectedGroup, selectedUser]);

    return (
        <SelectedContext.Provider
            value={{ selectedUser, setSelectedUser,
                     hasSelected, setHasSelected,
                     selectedGroup, setSelectedGroup,
                     isChatOpen, setChatOpen
        }}
        >
            {children}
        </SelectedContext.Provider>
    );
}

export default SelectedContext;