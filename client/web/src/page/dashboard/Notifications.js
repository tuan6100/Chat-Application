import useAuth from "../../hook/useAuth";
import { useEffect, useState } from "react";
import {
    Avatar,
    Box,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    Typography,
    Divider,
    Badge,
    Paper,
    Button,
    Collapse, Stack, ListItemIcon,
} from "@mui/material";
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';

import useMessage from "../../hook/useMessage";
import useWebSocket from "../../hook/useWebSocket";
import {alpha} from "@mui/material/styles";
import { useTheme } from '@mui/material/styles';
import useSidebar from "../../hook/useSideBar";
import useMediaQuery from "@mui/material/useMediaQuery";

const Notifications = () => {

    const { authFetch } = useAuth();
    const {setUnreadNotification} = useMessage()
    const [notifications, setNotifications] = useState([]);
    const userId = localStorage.getItem("userId");
    const {subscribe, unsubscribe} = useWebSocket();
    const [hasClicked, setHasClicked] = useState(new Map());
    const theme = useTheme();
    const { isSidebarOpen, setIsSidebarOpen } = useSidebar();
    const isMobile = useMediaQuery("(max-width: 600px)");


    useEffect(() => {
        const getNotifications = async () => {
            try {
                const response = await authFetch(`/api/account/me/notifications`);
                const data = await response.json();
                setNotifications(data);
                const unreadCount = data.filter(n => !n.viewed).length;
                setUnreadNotification(unreadCount);
            } catch (error) {
                console.error('Error fetching notifications:', error);
            }
        };
        getNotifications();

        subscribe(`/client/notification/friend/${userId}`, (notification) => {
                const newNotification = JSON.parse(notification.body);
                setNotifications((prev) => [newNotification, ...prev]);
                setUnreadNotification((prev) => prev + 1);
        });
        return () => {
            unsubscribe(`/client/notification/friend/${userId}`);
        };
    }, [subscribe]);


    const handleMarkAsRead = async (id) => {
        const updatedNotifications = notifications.map(n =>
            n.id === id ? { ...n, viewed: true } : n
        );
        setNotifications(updatedNotifications);
        setUnreadNotification(updatedNotifications.filter(n => !n.viewed).length);
        await authFetch(`/api/notification/mark-as-read/${id}`, {
            method: 'PUT',
        });
    };

    const handleRemoveNotification = async (id) => {
        setNotifications((prev) => prev.filter((n) => n.id !== id));
        await authFetch(`/api/notification/delete/${id}`, {
            method: 'DELETE',
        });
    };

    const handleAcceptRequest = (accountId, notificationId) => {
        authFetch(`/api/account/me/accept?friendId=${accountId}`, {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST'
        }).then((response) => {
            if (response.ok) {
                console.info("Friend request accepted");
                setHasClicked(prev => new Map(prev).set(notificationId, true));
            } else {
                console.error("Failed to accept friend request");
            }
        }).catch((error) => {
            console.error("Error accepting friend request:", error);
        })
    }

    const handleRejectRequest = (accountId, notificationId) => {

        authFetch(`/api/account/me/reject?friendId=${accountId}`, {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST'
        }).then(async (response) => {
            if (response.ok) {
                console.info("Friend request rejected");
                setHasClicked(prev => new Map(prev).set(notificationId, true));
                const responseChats = await authFetch(`/api/account/me/chats`)
                const chatData = await responseChats.json();
                localStorage.setItem('chatList', JSON.stringify(chatData));
                const chatIdList = JSON.parse(localStorage.getItem("chatIdList"));
                const updatedChatIdList = chatIdList.filter(chatId => chatId !== accountId);
                localStorage.setItem("chatIdList", JSON.stringify(updatedChatIdList));
            } else {
                console.error("Failed to reject friend request");
            }
        }).catch((error) => {
            console.error("Error rejecting friend request:", error);
        })
    }


    return (
        <Box  sx={{
            position: "absolute",
            left: isSidebarOpen ? 100 : 0,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            height: "100vh",
            width: "100vw",
            backgroundColor: theme.palette.mode === 'light' ? "#F8FAFF" : theme.palette.background.paper,
            boxShadow: '0px 0px 2px rgba(0,0,0,0.25)',
            transition: "left 0.5s ease-in-out, width 0.5s ease-in-out",
            zIndex: 1
        }}>
            <Stack direction="column" spacing={2} alignItems="center">
                <Typography
                    variant="h4"
                    sx={{
                        fontWeight: "bold",
                        marginBottom: 3,
                        textAlign: "center",
                    }}
                >
                    Notifications
                </Typography>
                <>
                    {notifications.length === 0 ? (
                        <Box sx={{ textAlign: "center", paddingY: 5 }}>
                            <NotificationsNoneIcon sx={{ fontSize: 70, color: "gray" }} />
                            <Typography>No notifications</Typography>
                        </Box>
                    ) : (
                        notifications.map((notification) => (
                            <ListItem
                                key={notification.id}
                                sx={{
                                    borderRadius: 2,
                                    mb: 1,
                                    width: "100%",
                                    "&:hover": {
                                        backgroundColor: alpha(theme.palette.primary.light, 0.1),
                                    },
                                }}
                            >
                                <ListItemAvatar>
                                    <Avatar
                                        src={notification.avatar}
                                        sx={{ width: 50, height: 50 }}
                                    />
                                </ListItemAvatar>
                                <Stack direction="row" alignItems="flex-start" spacing={2}>
                                    <ListItemText
                                        primary={
                                            <Typography>{notification.content}</Typography>
                                        }
                                        secondary={
                                            <Typography>{notification.aboutTime}</Typography>
                                        }
                                    />
                                    {hasClicked.get(notification.id) ? (
                                        <Typography>You were friends</Typography>
                                    ) : (
                                        <Stack
                                            direction="column"
                                            alignItems="space-between"
                                            spacing={2}
                                        >
                                            <Button
                                                onClick={() =>
                                                    handleAcceptRequest(
                                                        notification.senderId,
                                                        notification.id
                                                    )
                                                }
                                                sx={{
                                                    backgroundColor: "transparent",
                                                    color: "green",
                                                    border: "1px solid green",
                                                    borderRadius: 2,
                                                    textTransform: "none",
                                                    width: { xs: "40%", sm: "250px" },
                                                    height: "50px",
                                                    "&:hover": {
                                                        backgroundColor: "rgba(0, 255, 0, 0.1)",
                                                    },
                                                }}
                                            >
                                                Accept
                                            </Button>
                                            <Button
                                                onClick={() =>
                                                    handleRejectRequest(
                                                        notification.senderId,
                                                        notification.id
                                                    )
                                                }
                                                sx={{
                                                    backgroundColor: "transparent",
                                                    color: "red",
                                                    border: "1px solid red",
                                                    borderRadius: 2,
                                                    textTransform: "none",
                                                    width: { xs: "40%", sm: "250px" },
                                                    height: "50px",
                                                    "&:hover": {
                                                        backgroundColor: "rgba(255, 0, 0, 0.1)",
                                                    },
                                                }}
                                            >
                                                Reject
                                            </Button>
                                        </Stack>
                                    )}
                                </Stack>
                            </ListItem>
                        ))
                    )}
                </>
            </Stack>
        </Box>
    );

};

export default Notifications;
