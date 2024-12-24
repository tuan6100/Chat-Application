import { Navigate, Outlet } from "react-router";
import { Stack } from '@mui/material';
import SideBar from "./SideBar";
import useAuths from "../../hook/useAuth";
import MessagePrompt from "../../component/MessagePrompt";
import useSelectedUser from "../../hook/useSelectedUser";
import useMediaQuery from "@mui/material/useMediaQuery";


const DashboardLayout = () => {

    const {isAuthenticated} = useAuths();
    const {selectedUser} = useSelectedUser();
    const isMobile = useMediaQuery("(max-width: 600px)");

    if(!isAuthenticated){
        return <Navigate to='/auth/login'/>;
    }

  return (
    <Stack direction='row'>
      <SideBar/>
        {!selectedUser && !isMobile && <MessagePrompt selectedUser={selectedUser} />}
      <Outlet />
    </Stack>
  );
};

export default DashboardLayout;
