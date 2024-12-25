import {useContext} from "react";
import ConversationPropertiesContext from "../context/ConversationPropertiesContext";


const useConversationProperties = () => useContext(ConversationPropertiesContext);
export default useConversationProperties;