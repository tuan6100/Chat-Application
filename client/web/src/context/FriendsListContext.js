import {createContext, useState} from "react";

const FriendsListContext = createContext(undefined);

export const FriendsProvider = ({ children }) => {
    const [friendsList, setFriendsList] = useState([]);

    return (
        <FriendsListContext.Provider value={{ friendsList, setFriendsList }}>
            {children}
        </FriendsListContext.Provider>
    );
};

export default FriendsListContext;