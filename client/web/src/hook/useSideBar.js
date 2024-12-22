import {useContext} from "react";
import SidebarContext from "../context/SidebarContext";

const useSidebar = () => useContext(SidebarContext);

export default useSidebar;