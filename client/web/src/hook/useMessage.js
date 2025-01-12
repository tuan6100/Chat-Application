import {useContext} from "react";
import MessageContext from "../context/MessageContext";


const useMessage = () => useContext(MessageContext);
export default useMessage;