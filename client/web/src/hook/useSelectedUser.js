import {useContext} from "react";
import SelectedUserContext from "../context/SelectedUserContext";


const useSelectedUser = () => useContext(SelectedUserContext);

export default useSelectedUser;