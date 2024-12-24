import {useContext} from "react";
import SearchResultContext from "../context/SearchResultContext";

const useSearchResult = () => useContext(SearchResultContext);

export default useSearchResult;