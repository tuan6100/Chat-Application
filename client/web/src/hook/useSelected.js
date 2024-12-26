import {useContext} from "react";
import SelectedContext from "../context/SelectedContext";


const useSelected = () => useContext(SelectedContext);

export default useSelected;