import {createContext, useState} from "react";

const SearchResultContext = createContext(undefined);

export const SearchProvider = ({ children }) => {

    const [startedSearch, setStartedSearch] = useState(false);
    const [searchResults, setSearchResults] = useState([]);

    return (
        <SearchResultContext.Provider value={{startedSearch, setStartedSearch, searchResults, setSearchResults }}>
            {children}
        </SearchResultContext.Provider>
    );
};

export default SearchResultContext;