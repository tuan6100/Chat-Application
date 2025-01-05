import { useEffect, useState } from "react";
import { useSearchParams } from "react-router";
import useSidebar from "../../hook/useSideBar";
import { alpha, useTheme } from "@mui/material/styles";
import useMediaQuery from "@mui/material/useMediaQuery";
import useSearchResult from "../../hook/useSearchResult";
import useAuth from "../../hook/useAuth";
import {
    Avatar,
    Badge,
    Box,
    IconButton,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    Stack,
    Typography
} from "@mui/material";
import Search from "../../component/SearchBar";
import { Menu as MenuIcon } from '@mui/icons-material';
import PrivateChat from "../../component/Conversation/Chat/PrivateChat";
import MessagePrompt from "../../component/MessagePrompt";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

const StyledBadge = (props) => {
    return (
        <Badge
            overlap="circular"
            anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            variant="dot"
            sx={{
                "& .MuiBadge-dot": {
                    backgroundColor: props.isOnline ? "#44b700" : "gray",
                    width: "15px",
                    height: "15px",
                    borderRadius: "50%",
                    border: "2px solid white"
                }
            }}
        >
            {props.children}
        </Badge>
    );
};

const Chats = () => {
    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
    const { isSidebarOpen, setIsSidebarOpen } = useSidebar();
    const theme = useTheme();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const { searchResults, startedSearch } = useSearchResult();
    const [friendsList, setFriendsList] = useState([]);
    const anyResult = searchResults.length > 0;
    const { authFetch } = useAuth();
    const [chatId, setChatId] = useState(0);
    const [friendId, setFriendId] = useState(0);
    const [page, setPage] = useState(null);
    const [searchParams, setSearchParams] = useSearchParams();
    const [newMessageMap, setNewMessageMap] = useState(new Map());

    useEffect(() => {
        const getFriendsList = async () => {
            try {
                const response = await authFetch(`/api/account/me/friends`);
                const data = await response.json();
                setFriendsList(data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        getFriendsList();
        const intervalId = setInterval(getFriendsList, 60000);
        return () => clearInterval(intervalId);
    }, [authFetch]);

    const handleUserClick = async (accountId) => {
        try {
            const response = await authFetch(`/api/account/me/relationship?accountId=${accountId}`);
            if (!response.ok) {
                console.error("Failed to fetch user data");
                return;
            }
            const data = await response.json();
            setChatId(data.chatId);
            setFriendId(accountId);
            setSearchParams({ chatId: data.chatId }, { replace: true });
            if (data.status === "FRIEND") {
                setPage('conversation');
            } else if (data.status === "NO_RELATIONSHIP" || data.status === "BLOCKED") {
                setPage('info');
            } else if (data.status === "WAITING FOR ACCEPTANCE" || data.status === "NEW FRIEND REQUEST") {
                setPage('waiting');
            }
        } catch (error) {
            console.error("Error fetching data:", error);
        }
    };

    const anyFriend = friendsList.length > 0;

    useEffect(() => {
        const getNewMessage = () => {
            const socket = new SockJS(`${API_BASE_URL}/ws`);
            const stompClient = Stomp.over(socket);
            stompClient.connect({}, () => {
                stompClient.subscribe(`/client/chat/*`, (response) => {
                    const newMessage = JSON.parse(response.body);
                    setNewMessageMap(prevMap => new Map(prevMap).set(newMessage.senderId, { content: newMessage.content, sentTime: newMessage.sentTime }));
                });
            });
        };
        getNewMessage();
    }, [API_BASE_URL]);

    return (
        <Box sx={{ display: 'flex', height: '100vh' }}>
            <Box className="chat-box" sx={{
                position: "absolute",
                left: isSidebarOpen ? 100 : 0,
                width: isMobile ? (isSidebarOpen ? 'calc(100% - 100px)' : '100%') : 400,
                maxWidth: isMobile ? '100%' : 400,
                backgroundColor: theme.palette.mode === 'light' ? "#F8FAFF" : theme.palette.background.paper,
                boxShadow: '0px 0px 2px rgba(0,0,0,0.25)',
                transition: "left 0.5s ease-in-out, width 0.5s ease-in-out",
                zIndex: 1
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

                    <Box sx={{ width: "100%", height: 50 }}>
                        <Search placeholder="Find your friends..." />
                    </Box>

                    <Box sx={{ flexGrow: 1, overflowY: 'auto', mt: 2 }}>
                        <List sx={{
                            width: '100%',
                            bgcolor: 'background.paper',
                            borderRadius: 2,
                            boxShadow: "0px 1px 3px rgba(0, 0, 0, 0.1)",
                        }}>
                            {(startedSearch ? searchResults : friendsList)
                                .filter(result => result.accountId !== Number(localStorage.getItem('accountId')))
                                .map((result) => (
                                    <ListItem
                                        key={result.accountId}
                                        sx={{
                                            borderRadius: 2,
                                            mb: 1,
                                            width: '100%',
                                            '&:hover': {
                                                backgroundColor: alpha(theme.palette.primary.light, 0.1),
                                            }
                                        }}
                                        button
                                        onClick={() => handleUserClick(result.accountId)}
                                    >
                                        <ListItemAvatar>
                                            <StyledBadge isOnline={result.isOnline}>
                                                <Avatar
                                                    src={result.avatar}
                                                    sx={{ width: 50, height: 50 }}
                                                />
                                            </StyledBadge>
                                        </ListItemAvatar>

                                        <ListItemText
                                            primary={
                                                <Stack direction="row" justifyContent="space-between">
                                                    <Typography fontWeight="bold">
                                                        {result.username}
                                                    </Typography>
                                                    <Typography variant="caption" color="text.secondary">
                                                        {(() => {
                                                            const sentTime = new Date(newMessageMap.get(result.accountId)?.sentTime);
                                                            const now = new Date();
                                                            const isLessThanOneDay = (now - sentTime) < 24 * 60 * 60 * 1000;
                                                            return isLessThanOneDay
                                                                ? sentTime.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true })
                                                                : sentTime.toLocaleDateString([], { month: 'short', day: 'numeric' });
                                                        })()}
                                                    </Typography>
                                                </Stack>
                                            }
                                            secondary={
                                                <Typography noWrap sx={{ maxWidth: '80%' }}>
                                                    {newMessageMap.get(result.accountId)?.content || "No recent messages"}
                                                </Typography>
                                            }
                                        />
                                    </ListItem>
                                ))}
                        </List>
                    </Box>
                </Stack>
            </Box>

            <Box sx={{
                position: "relative",
                left: isMobile ? 0 : (isSidebarOpen ? 500 : 400),
                width: isMobile ? '100%' : (isSidebarOpen ? 'calc(100% - 500px)' : 'calc(100% - 400px)'),
                transition: "left 0.5s ease-in-out, width 0.5s ease-in-out",
                zIndex: 2
            }}>
                {chatId === 0 && <MessagePrompt />}
                {page === 'conversation' && (
                    <PrivateChat chatId={chatId} friendId={friendId} />
                )}
            </Box>
        </Box>
    );
};

export default Chats;
