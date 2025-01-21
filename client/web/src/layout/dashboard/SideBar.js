import React, {useEffect, useState} from 'react';
import { Avatar, Box, Divider, IconButton, Menu, MenuItem, Stack, Tooltip, Badge } from '@mui/material';
import { useTheme } from "@mui/material/styles";
import { ChatCircleDots, Gear, Phone, SignOut, User, Users, UserCircleGear, Bell, BellSlash } from "phosphor-react";
import useSettings from '../../hook/useSettings';
import CustomSwitch from '../../component/CustomSwitch';
import { useNavigate } from 'react-router';
import useAuth from "../../hook/useAuth";
import '../../css/SideBar.css';
import CustomDrawer from "../../component/Custom/drawer";
import useSidebar  from "../../hook/useSideBar";
import useSelected from "../../hook/useSelected";
import useMessage from "../../hook/useMessage";
import useWebSocket from "../../hook/useWebSocket";


const SideBar = () => {
    const [unreadCount, setUnreadCount] = useState(0);
    const [anchorEl, setAnchorEl] = useState(null);
    const { isSidebarOpen } = useSidebar();
    const open = Boolean(anchorEl);
    const handleClick = (event) => { setAnchorEl(event.currentTarget); };
    const handleMenuClose = () => { setAnchorEl(null); };
    const avatar = localStorage.getItem('avatar');
    const theme = useTheme();
    const navigate = useNavigate();
    const [selected, setSelected] = useState(0);
    const { onToggleMode } = useSettings();
    const { setIsAuthenticated, logout } = useAuth();
    const [openCustomBar, setOpenCustomBar] = useState(false);
    const {enableNotification} = useSettings();
    const {setChatOpen} = useSelected();
    const {unreadNotification} = useMessage();

    const Profile_Menu = [
        { title: "Profile", icon: <User /> },
        { title: "Settings", icon: <Gear /> },
        { title: "Logout", icon: <SignOut /> },
    ];

    const Nav_Buttons = [
        { index: 0, icon: <ChatCircleDots />, tooltip: "Chat" },
        { index: 1, icon: <Users />, tooltip: "Group" },
        { index: 2, icon: <Phone />, tooltip: "Call" },
        { 
            index: 3, 
            icon: (
                <Badge 
                    badgeContent={unreadNotification > 9 ? '9+' : unreadNotification}
                    color="error"
                >
                    {enableNotification ? <Bell /> : <BellSlash />}
                </Badge>
            ),
            tooltip: "Notification"
        },
    ];

    const getPath = (index) => {
        switch (index) {
            case 0:
                // sessionStorage.removeItem("chatData");
                return '/me/chats';
            case 1:
                return '/me/groups';
            case 2:
                setChatOpen(false);
                return '/me/calls';
            case 3:
                setChatOpen(false);
                return '/me/notifications';
            default: break;
        }
    };

    useEffect(() => {
        if (openCustomBar) {
            document.body.style.overflow = "hidden";
        } else {
            document.body.style.overflow = "";
        }
    }, [openCustomBar]);



    return (
        <Box sx={{ display: 'flex', flexDirection: 'row' }} className="sidebar-container">
            <Box p={2} sx={{
                backgroundColor: theme.palette.background.paper,
                boxShadow: "0px 0px 2px rgba(0,0,0,0.25)",
                transition: "width 0.5s ease-in-out",
                width: 100,
                height: "100vh",
                display: "flex",
                position: "absolute"
            }} className={`sidebar ${!isSidebarOpen ? 'sidebar-collapsed' : 'sidebar-expanded'}`}>
                <Stack direction="column" alignItems={"center"} justifyContent="space-between"
                       sx={{ width: "100%", height: "100%" }} spacing={3}>
                    <Stack alignItems={"center"} spacing={4}>
                        <Stack spacing={4}>
                            <Tooltip title="Profile Menu" placement="right">
                                <Avatar id='basic-button' sx={{ cursor: 'pointer', width: 60, height: 60 }}
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
                                        className={`slide-in ${selected === el.index ? 'active' : ''}`}
                                        sx={{
                                            width: "max-content",
                                            color: selected === el.index
                                                ? "#fff"
                                                : theme.palette.mode === 'light'
                                                    ? "#000"
                                                    : theme.palette.text.primary,
                                            backgroundColor: selected === el.index ? theme.palette.primary.main : "transparent",
                                            borderRadius: 1.5,
                                            position: "relative",
                                            transition: "background-color 0.5s ease, transform 0.5s ease",
                                            transform: selected === el.index ? 'scale(1.05)' : 'scale(1)',
                                            '&:hover': {
                                                transform: 'scale(1.1)',
                                                backgroundColor: selected === el.index
                                                    ? theme.palette.primary.main
                                                    : "rgba(0, 0, 0, 0.1)",
                                            },
                                        }}
                                    >
                                        {el.icon}
                                    </IconButton>

                                </Tooltip>
                            ))}
                            <Divider sx={{ width: "48px" }} />
                            <Tooltip title="Custom" placement="right">
                                <IconButton
                                    onClick={() => setOpenCustomBar(!openCustomBar)}
                                    className={`slide-in ${openCustomBar ? 'active' : ''}`}
                                    sx={{
                                        width: "max-content",
                                        color: theme.palette.text.primary,
                                        position: "relative",
                                        backgroundColor: openCustomBar ? theme.palette.primary.main : "transparent",
                                        '&:hover': {
                                            backgroundColor: openCustomBar ? theme.palette.primary.main : "transparent",
                                        },
                                    }}
                                >
                                    <UserCircleGear />
                                </IconButton>
                            </Tooltip>
                            <CustomDrawer open={openCustomBar} onClose={() => setOpenCustomBar(false)} />
                        </Stack>
                        <CustomSwitch onChange={onToggleMode} defaultChecked />
                    </Stack>
                </Stack>
            </Box>
        </Box>
    );
};

export default SideBar;