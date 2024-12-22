import {useTheme} from "@mui/material/styles";
import {Box, IconButton, Stack, Typography} from "@mui/material";
import {MagnifyingGlass} from "phosphor-react";
import {Search, SearchIconWrapper, StyledInputBase} from "../../component/Search";
import { Menu as MenuIcon } from '@mui/icons-material';
import useMediaQuery from "@mui/material/useMediaQuery";
import useSidebar from "../../hook/useSideBar";
import '../../css/SideBar.css';

const Chats = () => {

    const { isSidebarOpen, setIsSidebarOpen } = useSidebar();
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

    return (
        <Box className="chat-box" sx={{
            position: "absolute",
            left: isSidebarOpen ? 100 : 0,
            width: isMobile ? (isSidebarOpen ? 'calc(100% - 100px)' : '100%') : 300,
            maxWidth: isMobile ? '100%' : 300,
            backgroundColor: theme.palette.mode === 'light' ? "#F8FAFF" : theme.palette.background.paper,
            boxShadow: '0px 0px 2px rgba(0,0,0,0.25)',
            transition: "left 0.5s ease-in-out, width 0.5s ease-in-out"
        }}>
            <Stack p={3} spacing={2} sx={{ height: "100vh" }}>
                <Stack direction="row" alignItems='center' justifyContent='space-between'>
                    <Typography variant='h5'>
                        Chats
                    </Typography>
                    <IconButton onClick={() => setIsSidebarOpen(!isSidebarOpen)}>
                        <MenuIcon />
                    </IconButton>
                </Stack>

                <Stack sx={{width: "100%"}}>
                    <Search>
                        <SearchIconWrapper>
                            <MagnifyingGlass color="#709CE6"/>
                        </SearchIconWrapper>
                        <StyledInputBase placeholder='Search...' inputProps={{"aria-label": "search"}}/>
                    </Search>
                </Stack>
            </Stack>
        </Box>
    )
}

export default Chats;