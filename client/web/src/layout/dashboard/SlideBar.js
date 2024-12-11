import React, { useContext, useState } from 'react';
import { AppBar, Avatar, Box, Divider, IconButton, Menu, MenuItem, Stack, Tooltip } from '@mui/material';
import { useTheme } from "@mui/material/styles";
import { ChatCircleDots, Gear, Phone, SignOut, User, Users } from "phosphor-react";
import useSettings from '../../hook/useSettings';
import CustomSwitch from '../../component/CustomSwitch';
import { useNavigate } from 'react-router';
import useAuth from "../../hook/useAuth";
import AuthContext from "../../context/AuthContext";
import { Menu as MenuIcon } from '@mui/icons-material';
import '../../css/SlideBar.css';

const Profile_Menu = [
    { title: "Profile", icon: <User /> },
    { title: "Settings", icon: <Gear /> },
    { title: "Logout", icon: <SignOut /> },
];

const Nav_Buttons = [
    { index: 0, icon: <ChatCircleDots />, tooltip: "Chat" },
    { index: 1, icon: <Users />, tooltip: "Group" },
    { index: 2, icon: <Phone />, tooltip: "Call" },
];

const getPath = (index) => {
    switch (index) {
        case 0: return '/chat';
        case 1: return '/group';
        case 2: return '/call';
        case 3: return '/settings';
        default: break;
    }
};

const SideBar = () => {
    const [anchorEl, setAnchorEl] = useState(null);
    const [isSidebarOpen, setIsSidebarOpen] = useState(true);
    const open = Boolean(anchorEl);
    const handleClick = (event) => { setAnchorEl(event.currentTarget); };
    const handleMenuClose = () => { setAnchorEl(null); };
    const avatar = localStorage.getItem('avatar');
    const theme = useTheme();
    const navigate = useNavigate();
    const [selected, setSelected] = useState(0);
    const { onToggleMode } = useSettings();
    const { logout } = useAuth();
    const { setIsAuthenticated } = useContext(AuthContext);

    return (
        <Box sx={{ display: 'flex', flexDirection: 'row' }} className="sidebar-container">
            <Box p={2} sx={{
                backgroundColor: theme.palette.background.paper,
                boxShadow: "0px 0px 2px rgba(0,0,0,0.25)",
                transition: "width 0.3s ease-in-out",
                width: isSidebarOpen ? 100 : 0,
                height: "100vh",
                display: "flex"
            }} className={`sidebar ${!isSidebarOpen ? 'sidebar-collapsed' : 'sidebar-expanded'}`}>
                <Stack direction="column" alignItems={"center"} justifyContent="space-between"
                       sx={{ width: "100%", height: "100%" }} spacing={3}>
                    <Stack alignItems={"center"} spacing={4}>
                        <Stack spacing={4}>
                            <Tooltip title="Profile Menu" placement="right">
                                <Avatar id='basic-button' sx={{ cursor: 'pointer' }}
                                        src={avatar}
                                        aria-controls={open ? 'basic-menu' : undefined}
                                        aria-haspopup="true"
                                        aria-expanded={open ? 'true' : undefined}
                                        onClick={handleClick} />
                            </Tooltip>
                            <Menu
                                id="basic-menu"
                                anchorEl={anchorEl}
                                open={open}
                                onClose={handleMenuClose}
                                MenuListProps={{ 'aria-labelledby': 'basic-button' }}
                                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                                transformOrigin={{ vertical: 'bottom', horizontal: 'left' }}
                            >
                                <Stack spacing={1} px={1}>
                                    {Profile_Menu.map((el, idx) => (
                                        <MenuItem key={idx} onClick={() => {
                                            handleMenuClose();
                                            if (el.title === "Logout") {
                                                logout(navigate, setIsAuthenticated);
                                            } else {
                                                const path = getPath(idx);
                                                if (path) navigate(path);
                                            }
                                        }}>
                                            <Stack direction="row" alignItems={"center"} justifyContent="space-between"
                                                   sx={{ width: 100 }}>
                                                <Tooltip title={el.title} placement="right">
                                                    <span>{el.title}</span>
                                                </Tooltip>
                                                {el.icon}
                                            </Stack>
                                        </MenuItem>
                                    ))}
                                </Stack>
                            </Menu>
                        </Stack>

                        <Stack sx={{ width: "max-content" }} direction="column" alignItems="center" spacing={3}>
                            {Nav_Buttons.map((el) => (
                                <Tooltip key={el.index} title={el.tooltip} placement="right">
                                    <IconButton
                                        onClick={() => { setSelected(el.index); navigate(getPath(el.index)); }}
                                        sx={{
                                            width: "max-content",
                                            color: selected === el.index
                                                ? "#fff"
                                                : theme.palette.mode === 'light'
                                                    ? "#000"
                                                    : theme.palette.text.primary,
                                            backgroundColor: selected === el.index ? theme.palette.primary.main : "transparent",
                                            borderRadius: 1.5,
                                        }}
                                    >
                                        {el.icon}
                                    </IconButton>
                                </Tooltip>
                            ))}
                            <Divider sx={{ width: "48px" }} />
                            <Tooltip title="Settings" placement="right">
                                <IconButton onClick={() => { setSelected(3); navigate(getPath(3)); }}
                                            sx={{ width: "max-content", color: theme.palette.text.primary }}>
                                    <Gear />
                                </IconButton>
                            </Tooltip>
                        </Stack>
                        <CustomSwitch onChange={onToggleMode} defaultChecked />
                    </Stack>
                </Stack>
            </Box>

            <Box sx={{ flexGrow: 1 }}>
                <AppBar position="relative">
                        <Stack direction="row" alignItems="center">
                            <IconButton color="inherit" onClick={() => setIsSidebarOpen(!isSidebarOpen)} className="appbar-icon">
                                <MenuIcon />
                            </IconButton>
                        </Stack>
                </AppBar>
            </Box>
        </Box>
    );
};

export default SideBar;