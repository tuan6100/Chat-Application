import {createContext, useState} from "react";

const SelectedContext = createContext(undefined);

export const SelectedProvider = ({ children }) => {
    const [isChatOpen, setChatOpen] = useState(true);
    const [selected, setSelected] = useState(false);


    return (
        <SelectedContext.Provider
            value={{ selected, setSelected,
                     isChatOpen, setChatOpen
        }}
        >
            {children}
        </SelectedContext.Provider>
    );
}

export default SelectedContext;