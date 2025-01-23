import { styled, alpha } from '@mui/material/styles';
import { TextField, InputAdornment } from '@mui/material';
import { MagnifyingGlass } from 'phosphor-react';
import { useState } from 'react';
import useAuth from "../hook/useAuth";
import useSearchResult from "../hook/useSearchResult";

const CustomSearch = styled(TextField)(({ theme }) => ({
    width: "100%",
    height: 5,
    borderRadius: 15,
    '& .MuiOutlinedInput-root': {
        borderRadius: 15,
        backgroundColor: alpha(theme.palette.background.default, 1),
        '& fieldset': {
            border: "none",
        },
        '&:hover fieldset': {
            border: "1px solid #709CE6",
        },
        '&.Mui-focused fieldset': {
            border: "1px solid #709CE6",
        },
    }
}));

const SearchBar = ({placeholder = "", endpoint="" }) => {
    const [query, setQuery] = useState("");
    const {authFetch} = useAuth();
    const {setSearchResults, setStartedSearch} = useSearchResult();
    const handleSearch = async (e) => {
        const value = e.target.value;
        setQuery(value);
        if (value.length > 1) {
            setStartedSearch(true);
            try {
                const response = await authFetch(`${endpoint}${value}`);
                const data = await response.json();
                setSearchResults([]);
                setSearchResults(data);
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        } else {
            setSearchResults([]);
            setStartedSearch(false);
        }
    };

    return (
        <CustomSearch
            placeholder={placeholder}
            endpoint={endpoint}
            variant="outlined"
            value={query}
            onChange={handleSearch}
            InputProps={{
                startAdornment: (
                    <InputAdornment position="start">
                        <MagnifyingGlass size={20} color="#709CE6" />
                    </InputAdornment>
                ),
            }}
        />
    );
};

export default SearchBar;
