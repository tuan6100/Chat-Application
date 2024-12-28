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
    Button,
    Collapse,
} from "@mui/material";
import NotificationsNoneIcon from '@mui/icons-material/NotificationsNone';
import DeleteIcon from '@mui/icons-material/Delete';

const Notifications = () => {
    const { authFetch } = useAuth();
    const [notifications, setNotifications] = useState([]);

    useEffect(() => {
        const getNotifications = async () => {
            try {
                const response = await authFetch(`/api/account/me/notifications`);
                const data = await response.json();
                setNotifications(data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        getNotifications();
    }, [authFetch]);

    const handleRemoveNotification = (id) => {
        setNotifications((prevNotifications) =>
            prevNotifications.filter((notification) => notification.id !== id)
        );
    };

    const handleClearAllNotifications = () => {
        setNotifications([]);
    };

    const anyNotification = notifications.length > 0;

    return (
        <Box sx={{ width: "100%", maxWidth: 600, margin: "auto", marginTop: 4 }}>
            <Typography variant="h4" sx={{ fontWeight: "bold", marginBottom: "1.5rem", textAlign: "center" }}>
                Notifications
            </Typography>

            <Paper elevation={3} sx={{ borderRadius: 2 }}>
                <List>
                    {anyNotification ? notifications.map((notification) => (
                        <Collapse key={notification.id} in={true} timeout="auto">
                            <ListItem alignItems="flex-start" sx={{ paddingY: 2 }}>
                                <ListItemAvatar>
                                    <Badge
                                        color="secondary"
                                        variant={notification.viewed ? "standard" : "dot"}
                                        overlap="circular"
                                    >
                                        <Avatar src={notification.avatar} alt={notification.name}>
                                            {notification.name[0]}  {/* Avatar fallback */}
                                        </Avatar>
                                    </Badge>
                                </ListItemAvatar>

                                <ListItemText
                                    primary={
                                        <Typography sx={{ fontWeight: "bold", fontSize: "1rem" }}>
                                            {notification.name}
                                        </Typography>
                                    }
                                    secondary={
                                        <Box>
                                            <Typography variant="body2" color="textSecondary">
                                                {notification.content}
                                            </Typography>
                                            <Typography variant="caption" color="textSecondary">
                                                {notification.aboutTime}
                                            </Typography>
                                        </Box>
                                    }
                                />
                                <IconButton edge="end" aria-label="delete" onClick={() => handleRemoveNotification(notification.id)}>
                                    <DeleteIcon />
                                </IconButton>
                            </ListItem>
                            <Divider component="li" />
                        </Collapse>
                    )) : (
                        <Box sx={{ textAlign: "center", paddingY: 4 }}>
                            <NotificationsNoneIcon sx={{ fontSize: 64, color: "gray" }} />
                            <Typography variant="h6" sx={{ color: "gray" }}>
                                No notifications
                            </Typography>
                        </Box>
                    )}
                </List>
                {anyNotification && (
                    <Box sx={{ textAlign: "center", paddingY: 2 }}>
                        <Button variant="contained" color="secondary" onClick={handleClearAllNotifications}>
                            Clear All Notifications
                        </Button>
                    </Box>
                )}
            </Paper>
        </Box>
    );
};

export default Notifications;