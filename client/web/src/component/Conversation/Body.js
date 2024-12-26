import { Box, Paper, Typography, Button, Stack, CircularProgress } from "@mui/material";
import { useEffect, useState } from "react";
import useAuth from "../../hook/useAuth";
import useSelected from "../../hook/useSelected";

const ConversationBody = () => {
    const { authFetch } = useAuth();
    const { selectedUser } = useSelected();
    const [relationship, setRelationship] = useState(null);
    const [loading, setLoading] = useState(true);

    const fetchRelationship = async () => {
        setLoading(true);
        try {
            const response = await authFetch(`/api/account/me/relationship?accountId=${selectedUser}`);
            if (response.ok) {
                const data = await response.json();
                setRelationship(data);
                console.log("Relationship data:", data);
            } else {
                console.error("Failed to fetch relationship status");
            }
        } catch (error) {
            console.error("Error fetching data:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (selectedUser) {
            fetchRelationship();
        }
        // eslint-disable-next-line
    }, [selectedUser]);

    const handleSendFriendRequest = async () => {
        try {
            const response = await authFetch(`/api/account/me/send-friend-request?friendId=${selectedUser}`, {
                method: "POST",
            });
            if (response.ok) {
                alert("Friend request sent!");
                fetchRelationship();
            }
        } catch (error) {
            console.error("Error sending friend request:", error);
        }
    };

    const handleBlockUser = async () => {
        try {
            const response = await authFetch(`/api/account/me/block?friendId=${selectedUser}`, {
                method: "POST",
            });
            if (response.ok) {
                alert("User blocked!");
                fetchRelationship();
            }
        } catch (error) {
            console.error("Error blocking user:", error);
        }
    };

    const handleAcceptRequest = async () => {
        try {
            const response = await authFetch(`/api/account/me/accept?friendId=${selectedUser}`, {
                method: "POST",
            });
            if (response.ok) {
                alert("Friend request accepted!");
                fetchRelationship();
            }
        } catch (error) {
            console.error("Error accepting request:", error);
        }
    };

    const handleRejectRequest = async () => {
        try {
            const response = await authFetch(`/api/account/me/reject?accountId=${selectedUser}`, {
                method: "POST",
            });
            if (response.ok) {
                alert("Friend request rejected!");
                fetchRelationship();
            }
        } catch (error) {
            console.error("Error rejecting request:", error);
        }
    };

    const handleUnblockUser = async () => {
        try {
            const response = await authFetch(`/api/account/me/unblock?friendId=${selectedUser}`, {
                method: "POST",
            });
            if (response.ok) {
                alert("User unblocked!");
                fetchRelationship();
            }
        } catch (error) {
            console.error("Error unblocking user:", error);
        }
    }

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <CircularProgress />
            </Box>
        );
    }

    if (!relationship) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <Typography variant="h6">Select a conversation</Typography>
            </Box>
        );
    }

    switch (relationship.status) {
        case "FRIEND":
            return (
                <Box display="flex" flexDirection="column" height="100vh">
                    <Box flex={1} overflow="auto" p={2}>
                        {Array.isArray(relationship.messages) ? (
                            relationship.messages.map((msg, index) => (
                                <Paper
                                    key={index}
                                    sx={{
                                        p: 2,
                                        m: 1,
                                        maxWidth: "75%",
                                        alignSelf: msg.isSender ? "flex-end" : "flex-start",
                                        backgroundColor: msg.isSender ? "#DCF8C6" : "#F0F0F0",
                                    }}
                                >
                                    <Typography>{msg.content}</Typography>
                                </Paper>
                            ))
                        ) : (
                            <Typography>No messages</Typography>
                        )}
                    </Box>
                </Box>
            );

        case "NO_RELATIONSHIP":
            return (
                <Box display="flex" flexDirection="column" height="100vh">
                    <Box flex={1} display="flex" flexDirection="column" alignItems="center" justifyContent="center">
                        <Typography variant="h5">{relationship.username}</Typography>
                        <Typography variant="subtitle1">{relationship.email}</Typography>
                        <Stack direction="row" spacing={2} mt={2}>
                            <Button variant="contained" color="primary" onClick={handleSendFriendRequest}>
                                Send Friend Request
                            </Button>
                            <Button variant="outlined" color="error" onClick={handleBlockUser}>
                                Block
                            </Button>
                        </Stack>
                    </Box>
                </Box>
            );

        case "WAITING TO ACCEPT":
            return (
                <Box display="flex" flexDirection="column" height="100vh">
                    <Box flex={1} display="flex" justifyContent="center" alignItems="center">
                        <Typography variant="h6">Waiting for friend acceptance...</Typography>
                    </Box>
                </Box>
            );

        case "WAITING RESPONSE":
            return (
                <Box display="flex" flexDirection="column" height="100vh">
                    <Box flex={1} display="flex" flexDirection="column" alignItems="center" justifyContent="center">
                        <Typography variant="h6">Sent you a friend request</Typography>
                        <Stack direction="row" spacing={2} mt={2}>
                            <Button variant="contained" color="success" onClick={handleAcceptRequest}>
                                Accept
                            </Button>
                            <Button variant="outlined" color="error" onClick={handleRejectRequest}>
                                Reject
                            </Button>
                        </Stack>
                    </Box>
                </Box>
            );

        case "BLOCKED":
            return (
                <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                    <Typography variant="h6" color="error">
                        You have been blocked
                    </Typography>
                    <Button variant="contained" color="primary" onClick={handleUnblockUser}>
                        Unblock
                    </Button>
                </Box>
            );

        case "BLOCKED BY USER":
            return (
                <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                    <Typography variant="h6" color="error">
                        Not found
                    </Typography>
                </Box>
            );

        default:
            return (
                <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                    <Typography variant="h6">Unexpected relationship status</Typography>
                </Box>
            );
    }
};

export default ConversationBody;
