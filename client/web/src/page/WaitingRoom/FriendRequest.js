import {Avatar, Button, Container, Stack, Typography} from "@mui/material";
import useAuth from "../../hook/useAuth";
import {useEffect, useState} from "react";
import useConversationProperties from "../../hook/useConversationProperties";
import Body from "../../component/Conversation/Body";
import Footer from "../../component/Conversation/Footer";
import {useNavigate} from "react-router";


const FriendRequest = (friendId) => {

    const { authFetch } = useAuth();
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const { avatar, name, isOnline, lastOnlineTime, setAvatar, setName, setIsOnline, setLastOnlineTime } = useConversationProperties();
    const [spamMessages, setSpamMessages] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const getFriendInfo = async () => {
            if (!avatar || !name || !isOnline || !lastOnlineTime) {
                try {
                    const response = await authFetch(`/api/account/${friendId}`);
                    if (!response.ok) {
                        const error = await response.json();
                        console.error("Error during get friend info:", error.message);
                        return;
                    }
                    const data = await response.json();
                    setAvatar(data.avatar);
                    setName(data.username);
                    setIsOnline(data.isOnline);
                    setLastOnlineTime(data.lastOnlineTime);
                } catch (error) {
                    console.error("Error fetching data:", error);
                }
            }
        };
        getFriendInfo();
        const intervalId = setInterval(getFriendInfo, 60000);
        return () => clearInterval(intervalId);
    }, [authFetch, avatar, name, isOnline, lastOnlineTime, setAvatar, setName, setIsOnline, setLastOnlineTime, friendId]);

    const handleInvite = async () => {
        try {
            const response = await authFetch(`/api/account/me/invite?friendId=${friendId}`, {
                method: "POST",
            });
            if (!response.ok) {
                const error = await response.json();
                setError(error.message);
                return;
            }
            setSuccess("Friend request sent. Please wait for the user to accept.");
        } catch (error) {
            console.error("Error sending friend request:", error);
        }
    }


    useEffect(() => {
        const fetchSpamMessages = async () => {
            try {
                const response = await authFetch(`/api/account/me/spam-chat?userId=${friendId}`);
                if (!response.ok) {
                    const error = await response.json();
                    console.error("Error during get messages:", error.message);
                    return [];
                }
                const data = await response.json();
                setSpamMessages(data);
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        };
        fetchSpamMessages();
    }, [authFetch, setSpamMessages]);


    return (
        <>
            <Stack justifyContent="center" alignItems="center" spacing={3} sx={{ mt: 3 }}>
                <Avatar src={avatar} sx={{ width: 150, height: 150 }} />
                <Typography variant="h5">{name}</Typography>
                {error === '' ? <Typography color="red">{error}</Typography> : <Typography>{success}</Typography>}
                <Button variant="contained" color="primary" onClick={() => navigate(`/user/profile/${friendId}`)}>View Profile</Button>
            </Stack>

            <Body messages={spamMessages}/>

            { error === '' && success === '' &&
                <Stack direction='row' spacing={3} justifyContent="space-between" sx={{ mt: 3 }}>
                    <Button
                        sx={{
                            backgroundColor: "transparent",
                            border: "1px solid",
                            borderRadius: 2,
                            textTransform: "none",
                            width: { xs: "30%", sm: "250px" },
                            height: "50px",
                            "&:hover": {
                                border: "1px solid cyan",
                            },
                        }}
                        onClick={handleInvite}
                    >
                        <Typography color="cyan">
                            Send Friend Request
                        </Typography>
                    </Button>

                    <Button
                        sx={{
                            backgroundColor: "transparent",
                            border: "1px solid",
                            borderRadius: 2,
                            textTransform: "none",
                            width: { xs: "30%", sm: "250px" },
                            height: "50px",
                            "&:hover": {
                                border: "1px solid red",
                            },
                        }}
                        onClick={handleInvite}
                    >
                        <Typography color="red">
                            Block
                        </Typography>
                    </Button>
                </Stack>
            }

            {success !== '' &&
                <Stack justifyContent="center" alignItems="center" spacing={3} sx={{ mt: 3 }}>
                    <Typography>{success}</Typography>
                    <Footer />
                </Stack>
            }
        </>
    )
}

export default FriendRequest;