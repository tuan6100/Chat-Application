import { Navigate, Outlet } from "react-router";
import { Stack, Box } from '@mui/material';
import SideBar from "./SideBar";
import useAuths from "../../hook/useAuth";
import useSelected from "../../hook/useSelected";
import useMediaQuery from "@mui/material/useMediaQuery";
import useSidebar from "../../hook/useSideBar";


const DashboardLayout = () => {

    const {isAuthenticated} = useAuths();
    const {hasSelected, isChatOpen} = useSelected();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const { isSidebarOpen } = useSidebar();
    const pageType = localStorage.getItem('pageType');

    if(!isAuthenticated){
        return <Navigate to='/auth/login'/>;
    }

  return (
      <Stack direction='row' sx={{ height: '100vh' }}>
          <SideBar sx={{ width: '300px', flexShrink: 0 }} />
          <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
              <Outlet />
          </Box>

      </Stack>
    );
};

export default DashboardLayout;
