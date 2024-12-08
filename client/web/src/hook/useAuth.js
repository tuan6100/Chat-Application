import {useContext} from "react";
import AuthContext from "../context/AuthContext";

const useAuths = () => useContext(AuthContext);

export default useAuths;