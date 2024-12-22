import {createContext, useState} from "react";

const SearchResultContext = createContext(undefined);

export const SearchProvider = ({ children }) => {
    const [searchResults, setSearchResults] = useState([]);

    return (
        <SearchResultContext.Provider value={{ searchResults, setSearchResults }}>
            {children}
        </SearchResultContext.Provider>
    );
};

export default SearchResultContext;