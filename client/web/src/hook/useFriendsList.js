import {useContext} from "react";
import FriendsListContext from "../context/FriendsListContext";

const useFriendsList = () => useContext(FriendsListContext);
export default useFriendsList;