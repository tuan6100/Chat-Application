import { Navigate, Outlet } from "react-router";
import { Stack, Box } from '@mui/material';
import SideBar from "./SideBar";
import useAuths from "../../hook/useAuth";
import MessagePrompt from "../../component/MessagePrompt";
import useSelected from "../../hook/useSelected";
import useMediaQuery from "@mui/material/useMediaQuery";
import Conversation from "../../component/Conversation";
import useSidebar from "../../hook/useSideBar";


const DashboardLayout = () => {

    const {isAuthenticated} = useAuths();
    const {hasSelected} = useSelected();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const { isSidebarOpen } = useSidebar();

    if(!isAuthenticated){
        return <Navigate to='/auth/login'/>;
    }

  return (
      <Stack direction='row' sx={{ height: '100vh' }}>
          <SideBar sx={{ width: '300px', flexShrink: 0 }} />

          <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
              <Outlet />
              {!hasSelected && !isMobile && <MessagePrompt />}
          </Box>

          {hasSelected && !isMobile && (
              <Box sx={{
                  position: "absolute",
                  left: isSidebarOpen ? 500 : 400,
                  width: isSidebarOpen ? 'calc(100% - 500px)' : 'calc(100% - 400px)',
                  transition: "left 0.5s ease-in-out, width 0.5s ease-in-out"
              }}>
                  <Conversation />
              </Box>
          )}
      </Stack>
    );
};

export default DashboardLayout;
