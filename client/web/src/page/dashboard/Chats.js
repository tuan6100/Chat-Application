import {useEffect, useRef, useState, useCallback} from "react";
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
    ListItemAvatar, ListItemIcon,
    ListItemText,
    Stack,
    Typography
} from "@mui/material";
import Search from "../../component/SearchBar";
import { Menu as MenuIcon, PersonAdd, Sms, ThumbUp, ThumbDown, Pending } from '@mui/icons-material';
import PrivateChat from "../Chat/PrivateChat";
import useMessage from "../../hook/useMessage";

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


    const { isSidebarOpen, setIsSidebarOpen } = useSidebar();
    const theme = useTheme();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const { searchResults, startedSearch } = useSearchResult();
    const anyResult = searchResults.length > 0;
    const { authFetch } = useAuth();
    const [UserId, setUserId] = useState(0);
    const [page, setPage] = useState(null);
    const isFetching = useRef(false);
    let [chatId, setChatId] = useState(0);
    const [friendId, setFriendId] = useState(0);
    const { oldMessagesMap, setOldMessagesMap,
            getMessagesFromSession, updateChatDataInSession, finalMessagesMap } = useMessage();
    const chatsList = JSON.parse(localStorage.getItem('chatList')) || [];


    const fetchChatList = useCallback(async () => {
        try {
            const response = await authFetch(`/api/account/me/chats`);
            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('chatList', JSON.stringify(data));
                setChatList(data);
            } else {
                console.error("Failed to fetch chat list");
            }
        } catch (error) {
            console.error("Error fetching chat list:", error);
        }
    }, [authFetch]);

    useEffect(() => {
        if (chatsList.length === 0) {
            fetchChatList();
        }
    }, [chatsList, fetchChatList]);

    class ChatElement {
        constructor(chatId = 0, friendId = 0, friendUsername = "", friendAvatar = "", friendIsOnline = false, friendLastOnlineTime = new Date(),
                    lastMessage = "", lastMessageSenderId = -1, lastMessageSenderName = "", lastMessageSentTime = new Date(), lastMessageHasSeen = true) {
            this.chatId = chatId;
            this.friendId = friendId;
            this.friendUsername = friendUsername;
            this.friendAvatar = friendAvatar;
            this.friendIsOnline = friendIsOnline;
            this.friendLastOnlineTime = friendLastOnlineTime;
            this.lastMessage = lastMessage;
            this.lastMessageSenderId = lastMessageSenderId;
            this.lastMessageSenderName = lastMessageSenderName;
            this.lastMessageSentTime = lastMessageSentTime;
            this.lastMessageHasSeen = lastMessageHasSeen;
        }
    }
    const [chatList, setChatList] = useState([]);
    const [friendList, setFriendList] = useState([]);


    useEffect(() => {
        const getFriendsList = async () => {
            try {
                const response = await authFetch(`/api/account/me/friends`);
                const data = await response.json();
                setFriendList(data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        getFriendsList();
        const intervalId = setInterval(getFriendsList, 60000);
        return () => clearInterval(intervalId);
    }, []);

    const anyFriend = friendList.length > 0;


    useEffect(() => {
        const fetchChat = async () => {
            try {
                const response = await authFetch(`/api/account/me/chats/private-chat`);
                const data = await response.json();
                const newChatList = data.map((chat) => {
                    const friend = friendList.find(friend => friend.accountId === chat.friendId);
                    if (!friend) {
                        console.log("Friend not found: ", chat);
                    }
                    const lastMessage = chat.lastestMessage || {};
                    return new ChatElement(
                        chat.chatId,
                        chat.friendId,
                        friend?.username || "",
                        friend?.avatar || "",
                        friend.isOnline || false,
                        friend.lastOnlineTime || new Date(),
                        lastMessage.content || null,
                        lastMessage.senderId || null,
                        (lastMessage.senderId === parseInt(localStorage.getItem('accountId'))) ? "You: " : "",
                        lastMessage.sentTime || null,
                        lastMessage.hasSeen || null
                    );
                });
                setChatList(newChatList);
                console.info("Fetched chat list: ", newChatList);
            } catch (error) {
                console.error("Error fetching data:", error);
            }
        };
        fetchChat();
    }, [friendList]);


    const fetchMessages = async (chatId) => {
        if (isFetching.current) {
            return;
        }
        if (getMessagesFromSession(chatId).length > 0) {
            return;
        }
        isFetching.current = true;
        try {
            const response = await authFetch(`/api/chat/${chatId}/messages?page=0`);
            if (!response.ok) {
                console.error("Failed to fetch messages");
                return;
            }
            const data = await response.json();
            if (data === null) {
                return;
            }
            console.info("Fetched messages for chat " + chatId + ": ", data);
            updateChatDataInSession(chatId, data);
            console.info("Cached messages for chat " + chatId + ": ", getMessagesFromSession(chatId));
            setOldMessagesMap((prev) => {
                const newMap = new Map(prev);
                newMap.set(chatId, data);
                return newMap;
            });
        } catch (error) {
            console.error("Error fetching messages:", error);
        } finally {
            isFetching.current = false;
        }
    }

    useEffect(() => {
        const fetchOldMessages = async () => {
            const fetchPromises = chatList
                .filter(chat => !oldMessagesMap.has(chat.chatId) && oldMessagesMap.get(chat.chatId) !== [])
                .map(chat => {
                    console.info("Fetching messages for chat: ", chat.chatId);
                    return fetchMessages(chat.chatId);
                });
            await Promise.all(fetchPromises);
        };
        fetchOldMessages();
    }, [chatList, fetchMessages, oldMessagesMap]);


    useEffect(() => {
        console.info("Final messages map has been updated ");
        setChatList((prevChatList) => {
            const updatedChatList = prevChatList.map((chat) => {
                const newMessages = finalMessagesMap.get(chat.chatId) || [];
                if (newMessages.length > 0) {
                    const lastMessage = newMessages[newMessages.length - 1];
                    return {
                        ...chat,
                        lastMessage: lastMessage.content,
                        lastMessageSenderId: lastMessage.senderId,
                        lastMessageSenderName: lastMessage.senderId === parseInt(localStorage.getItem('accountId')) ? "You: " :  "",
                        lastMessageSentTime: new Date(lastMessage.sentTime),
                        lastMessageHasSeen: lastMessage.viewerIds.includes(parseInt(localStorage.getItem('accountId'))) ||
                            lastMessage.senderId === parseInt(localStorage.getItem('accountId')) ||
                            lastMessage.content.includes("This message has been deleted"),
                    };
                }
                return chat;
            });
            return updatedChatList.sort(
                (a, b) => new Date(b.lastMessageSentTime).getTime() - new Date(a.lastMessageSentTime).getTime()
            );
        });
    }, [finalMessagesMap]);


    // const handleSearchResultClick = (accountId) => {
    // };

    const handleStartChat = (accountId) => {
        const chat = chatList.find(chat => chat.friendId === accountId);
        if (chat) {
            console.info("Chat already exists: ", chat);
            setChatId(chat.chatId);
            setFriendId(accountId);
        }
    }

    const handleFriendRequest = (accountId) => {
        authFetch(`/api/account/me/invite?friendId=${accountId}`, {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST'
        }).then((response) => {
            if (response.ok) {
                console.info("Friend request sent");
            } else {
                console.error("Failed to send friend request");
            }
        }).catch((error) => {
            console.error("Error sending friend request:", error);
        })
    }

    const handleAcceptRequest = (accountId) => {
        authFetch(`/api/account/me/accept?friendId=${accountId}`, {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST'
        }).then((response) => {
            if (response.ok) {
                console.info("Friend request accepted");
            } else {
                console.error("Failed to accept friend request");
            }
        }).catch((error) => {
            console.error("Error accepting friend request:", error);
        })
    }

    const handleRejectRequest = (accountId) => {
        authFetch(`/api/account/me/reject?friendId=${accountId}`, {
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST'
        }).then((response) => {
            if (response.ok) {
                console.info("Friend request rejected");
            } else {
                console.error("Failed to reject friend request");
            }
        }).catch((error) => {
            console.error("Error rejecting friend request:", error);
        })
    }


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
                        <Search placeholder="Find your friends..." endpoint="/api/account/search?username=" />
                    </Box>

                    <Box sx={{ flexGrow: 1, overflowY: 'auto', mt: 2 }}>
                        <List sx={{
                            width: '100%',
                            bgcolor: 'background.paper',
                            borderRadius: 2,
                            boxShadow: "0px 1px 3px rgba(0, 0, 0, 0.1)",
                        }}>
                            {startedSearch ? (
                                searchResults
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
                                            // onClick={() => }
                                        >
                                            <ListItemAvatar>
                                                <StyledBadge isOnline={result.isOnline}>
                                                    <Avatar
                                                        src={result.avatar}
                                                        sx={{ width: 50, height: 50 }}
                                                    />
                                                </StyledBadge>
                                            </ListItemAvatar>

                                            <ListItemText>
                                                <Stack direction="row" justifyContent="space-between">
                                                    <Typography fontWeight="bold">
                                                        {result.username}
                                                    </Typography>
                                                </Stack>
                                            </ListItemText>

                                            <ListItemIcon>
                                                {result.relationshipStatus === "FRIEND" ? (
                                                    <IconButton onClick={() => handleStartChat(result.accountId)} >
                                                        <Sms />
                                                    </IconButton>
                                                ) : (
                                                    result.relationshipStatus === "NO RELATIONSHIP" ? (
                                                        <IconButton onClick={() => handleFriendRequest(result.accountId)} >
                                                            <PersonAdd />
                                                        </IconButton>
                                                    ) : (
                                                        result.relationshipStatus === "PENDING" ? (
                                                            <IconButton >
                                                                <Pending />
                                                            </IconButton>
                                                        ) : (
                                                            <Stack direction="row" spacing={1}>
                                                                <IconButton onClick={() => handleAcceptRequest(result.accountId)} >
                                                                    <ThumbUp />
                                                                </IconButton>
                                                                <IconButton onClick={() => handleRejectRequest(result.accountId)} >
                                                                    <ThumbDown />
                                                                </IconButton>
                                                            </Stack>
                                                        )
                                                    )
                                                )}
                                            </ListItemIcon>
                                        </ListItem>

                                    ))) : (
                                chatList
                                    .sort((a, b) => new Date(b.lastMessageSentTime).getTime() - new Date(a.lastMessageSentTime).getTime())
                                    .map((item) => (
                                        <ListItem
                                            key={item.chatId}
                                            sx={{
                                                borderRadius: 2,
                                                mb: 1,
                                                width: '100%',
                                                '&:hover': {
                                                    backgroundColor: alpha(theme.palette.primary.light, 0.1),
                                                }
                                            }}
                                            button
                                            onClick={() => setChatId(item.chatId)}
                                        >
                                            <ListItemAvatar>
                                                <StyledBadge isOnline={item.friendIsOnline}>
                                                    <Avatar
                                                        src={item.friendAvatar}
                                                        sx={{ width: 50, height: 50 }}
                                                    />
                                                </StyledBadge>
                                            </ListItemAvatar>

                                            <ListItemText
                                                primary={
                                                    <Stack direction="row" justifyContent="space-between">
                                                        <Typography fontWeight="bold">
                                                            {item.friendUsername}
                                                        </Typography>
                                                        <Typography variant="caption" color="text.secondary">
                                                            {(() => {
                                                                const sentTime = new Date(item.lastMessageSentTime);
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
                                                    <Typography variant="subtitle2" color={(item.lastMessageHasSeen || item.lastMessage === 'This message has been deleted') ? '#808080' : '#ffffff'} noWrap sx={{ maxWidth: '80%' }}>
                                                        {item.lastMessageSenderName === null ? '' : item.lastMessageSenderName + item.lastMessage || "No recent messages"}
                                                    </Typography>
                                                }
                                            />
                                        </ListItem>

                                    ))
                            )}
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
                {chatId !== 0 && (
                    <PrivateChat
                        friendId={chatList.find(chat => chat.chatId === chatId)?.friendId || friendId}
                        chatId={chatId}
                    />
                )}
            </Box>
        </Box>
    );
};

export default Chats;