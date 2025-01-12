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
    IconButton,
    Collapse,
} from "@mui/material";
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import GroupIcon from '@mui/icons-material/Group';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import MailIcon from '@mui/icons-material/Mail';
import DeleteIcon from '@mui/icons-material/Delete';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import useMessage from "../../hook/useMessage";

const Notifications = () => {

    const { authFetch } = useAuth();
    const {setUnreadNotification} = useMessage()
    const [notifications, setNotifications] = useState([]);
    const userId = localStorage.getItem("userId");

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

        const socket = new SockJS(`${process.env.REACT_APP_API_BASE_URL}/ws`);
        const stompClient = new Client({
            webSocketFactory: () => socket,
            reconnectDelay: 5000,
        });
        stompClient.onConnect = () => {
            console.log("Connected to WebSocket");
            stompClient.subscribe(`/client/notification/friend/${userId}`, (message) => {
                const newNotification = JSON.parse(message.body);
                setNotifications((prev) => [newNotification, ...prev]);
                setUnreadNotification((prev) => prev + 1);
            });
        };

        stompClient.activate();

        return () => {
            if (stompClient) {
                stompClient.deactivate();
            }
        };
    }, [authFetch, setUnreadNotification, userId]);

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

    const renderNotificationIcon = (type) => {
        switch (type) {
            case 'friend_request':
                return <PersonAddIcon sx={{ color: '#4caf50' }} />;
            case 'message':
                return <MailIcon sx={{ color: '#2196f3' }} />;
            case 'group_invite':
                return <GroupIcon sx={{ color: '#ff9800' }} />;
            default:
                return <NotificationsNoneIcon sx={{ color: 'gray' }} />;
        }
    };

    return (
        <Box sx={{ width: "100%", maxWidth: 700, margin: "auto", marginTop: 4 }}>
            <Typography variant="h4" sx={{ fontWeight: "bold", marginBottom: 3, textAlign: "center" }}>
                Notifications
            </Typography>

            <Paper elevation={3} sx={{ borderRadius: 3 }}>
                <List>
                    {notifications.length > 0 ? notifications.map((notification) => (
                        <Collapse key={notification.id} in={true} timeout="auto">
                            <ListItem
                                alignItems="flex-start"
                                sx={{
                                    paddingY: 2,
                                    cursor: "pointer",
                                    transition: "background 0.3s ease",
                                    "&:hover": { backgroundColor: "#f5f5f5" },
                                    backgroundColor: notification.viewed ? 'white' : '#e8f5e9'
                                }}
                                onClick={() => handleMarkAsRead(notification.id)}
                            >
                                <ListItemAvatar>
                                    <Badge
                                        color="secondary"
                                        variant={notification.viewed ? "standard" : "dot"}
                                        overlap="circular"
                                    >
                                        {renderNotificationIcon(notification.type)}
                                    </Badge>
                                </ListItemAvatar>

                                <ListItemText
                                    primary={
                                        <Typography sx={{ fontWeight: "bold" }}>
                                            {notification.name}
                                        </Typography>
                                    }
                                    secondary={
                                        <>
                                            <Typography variant="body2">
                                                {notification.content}
                                            </Typography>
                                            <Typography variant="caption" color="textSecondary">
                                                {notification.aboutTime}
                                            </Typography>
                                        </>
                                    }
                                />
                                <IconButton edge="end" onClick={(e) => {
                                    e.stopPropagation();
                                    handleRemoveNotification(notification.id);
                                }}>
                                    <DeleteIcon />
                                </IconButton>
                            </ListItem>
                            <Divider />
                        </Collapse>
                    )) : (
                        <Box sx={{ textAlign: "center", paddingY: 5 }}>
                            <NotificationsNoneIcon sx={{ fontSize: 70, color: "gray" }} />
                            <Typography>No notifications</Typography>
                        </Box>
                    )}
                </List>
            </Paper>
        </Box>
    );
};

export default Notifications;
