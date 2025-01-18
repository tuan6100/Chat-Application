import {useCallback, useEffect, useRef, useState} from "react";
import useAuth from "../../hook/useAuth";
import useConversationProperties from "../../hook/useConversationProperties";
import Header from "../../component/Conversation/Header";
import {Avatar, Box, CircularProgress, Stack, Typography} from "@mui/material";
import Body from "../../component/Conversation/Body";
import Footer from "../../component/Conversation/Footer";
import useMediaQuery from "@mui/material/useMediaQuery";
import useMessage from "../../hook/useMessage";
import {useTheme} from "@mui/material/styles";
import useWebSocket from "../../hook/useWebSocket";


const PrivateChat = ({ friendId, chatId }) => {
    const { authFetch } = useAuth();
    const {
        avatar, name, isOnline, lastOnlineTime,
        setAvatar, setName, setIsOnline, setLastOnlineTime
    } = useConversationProperties();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const messagesEndRef = useRef(null);
    const scrollRef = useRef(null);
    const isFetching = useRef(false);
    const isAutoScrolling = useRef(false);
    const {
        getMessagesFromSession,
        updateChatDataInSession,
        rawMessagesMap,
        finalMessagesMap,
        typingUsers, setTypingUsers,
        toggleNewMessage, setToggleNewMessage
    } = useMessage();
    const [messageList, setMessageList] = useState([]);
    const theme = useTheme();
    const accountId = parseInt(localStorage.getItem("accountId"));
    const [isScrollAtBottom, setIsScrollAtBottom] = useState(true);
    const [isLoading, setIsLoading] = useState(false);
    const { subscribe, unsubscribe } = useWebSocket();



    useEffect(() => {
        const sessionMessages = getMessagesFromSession(chatId);
        sessionMessages.sort((a, b) => new Date(a.sentTime).getTime() - new Date(b.sentTime).getTime());
        if (sessionMessages.length === 0) {
            fetchMessages(1);
        } else {
            setMessageList(sessionMessages);
        }
    }, [chatId]);


    useEffect(() => {
        if (friendId === 0) return;
        const getFriendInfo = async () => {
            try {
                const response = await authFetch(`/api/account/${friendId}/info`);
                if (!response.ok) {
                    const error = await response.json();
                    console.error("Error fetching friend info:", error.message);
                    return;
                }
                const data = await response.json();
                setAvatar(data.avatar);
                setName(data.username);
                setIsOnline(data.isOnline);
                setLastOnlineTime(data.lastOnlineTime);
            } catch (error) {
                console.error("Error fetching friend info:", error);
            }
        };
        getFriendInfo();
        const intervalId = setInterval(getFriendInfo, 300000);
        return () => clearInterval(intervalId);
    }, [authFetch, friendId, setAvatar, setName, setIsOnline, setLastOnlineTime]);


    const scrollToBottom = () => {
        if (isAutoScrolling.current && messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    };

    useEffect(() => {
        if (isScrollAtBottom && messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [messageList, typingUsers]);

    const checkIfScrollAtBottom = () => {
        if (!scrollRef.current) return;
        const { scrollTop, scrollHeight, clientHeight } = scrollRef.current;
        setIsScrollAtBottom(scrollTop + clientHeight >= scrollHeight);
    };

    useEffect(() => {
        const scrollElement = scrollRef.current;
        if (scrollElement) {
            scrollElement.addEventListener("scroll", checkIfScrollAtBottom);
            return () => {
                scrollElement.removeEventListener("scroll", checkIfScrollAtBottom);
            };
        }
    }, []);

    useEffect(() => {
        if (isScrollAtBottom) {
            scrollToBottom();
        }
    }, [messageList, typingUsers, isScrollAtBottom]);

    const handleNewMessageClick = () => {
        scrollToBottom();
        isAutoScrolling.current = true;
        setToggleNewMessage(() => null);
    };

    const handleScrollUp = () => {
        if (!scrollRef.current || !hasMore || isFetching.current) return;
        const scrollElement = scrollRef.current;
        const previousScrollHeight = scrollElement.scrollHeight;
        if (scrollElement.scrollTop === 0) {
            fetchMessages(page).then(() => {
                const newScrollHeight = scrollElement.scrollHeight;
                scrollElement.scrollTop = newScrollHeight - previousScrollHeight;
            });
        }
    };

    const fetchMessages = useCallback(async (page) => {
        if (isFetching.current || !hasMore) return;
        isFetching.current = true;
        setIsLoading(true);
        const scrollElement = scrollRef.current;
        const previousScrollHeight = scrollElement ? scrollElement.scrollHeight : 0;
        try {
            const response = await authFetch(`/api/chat/${chatId}/messages?page=${page}`);
            if (!response.ok) {
                const error = await response.json();
                console.error("Error fetching messages:", error.message);
                setHasMore(false);
                return;
            }
            const data = await response.json();
            if (data.length > 0) {
                setMessageList((prev) => [...data, ...prev]);
                const sessionMessages = getMessagesFromSession(chatId) || [];
                updateChatDataInSession(chatId, [...data, ...sessionMessages]);
                if (scrollElement) {
                    const newScrollHeight = scrollElement.scrollHeight;
                    scrollElement.scrollTop = newScrollHeight - previousScrollHeight;
                }
                setPage(page + 1);
            } else {
                setHasMore(false);
            }
        } catch (error) {
            console.error("Error fetching messages:", error);
        } finally {
            isFetching.current = false;
            setIsLoading(false);
        }
    }, [authFetch, chatId, hasMore]);



    useEffect(() => {
        const newMessages = finalMessagesMap.get(chatId) || [];
        const rawMessages = rawMessagesMap.get(chatId) || [];
        if (newMessages.length > 0 || rawMessages.length > 0) {
            setMessageList((prevMessages) => {
                const updatedMessages = [...prevMessages];
                newMessages.forEach((newMessage) => {
                    const index = updatedMessages.findIndex(
                        (msg) => msg.randomId === newMessage.randomId
                    );
                    if (index !== -1) {
                        updatedMessages[index] = newMessage;
                    } else {
                        updatedMessages.push(newMessage);
                    }
                });
                rawMessages.forEach((rawMessage) => {
                    const index = updatedMessages.findIndex(
                        (msg) => msg.randomId === rawMessage.randomId
                    );
                    if (index === -1) {
                        updatedMessages.push(rawMessage);
                    }
                });
                return updatedMessages;
            });
        }
    }, [finalMessagesMap, rawMessagesMap, chatId]);


    return (
        <Stack height="100%" maxHeight="100vh" width="auto">
            <Header
                name={name}
                avatar={avatar}
                isOnline={isOnline}
                lastOnlineTime={lastOnlineTime}
            />

            <Box
                ref={scrollRef}
                onScroll={handleScrollUp}
                width="100%"
                sx={{ flexGrow: 1, height: "100%", overflowY: "auto" }}
            >
                {isLoading && (
                    <Stack
                        alignItems="center"
                        justifyContent="center"
                        sx={{ py: 2 }}
                    >
                        <CircularProgress size={24} />
                    </Stack>
                )}
                <Body chatId={chatId} messages={messageList} fetchMessages={fetchMessages} page={page} hasMore={hasMore}/>
                <div ref={messagesEndRef} />
            </Box>

            {toggleNewMessage !== null && toggleNewMessage.senderId !== accountId && !toggleNewMessage.viewerIds.includes(accountId) && !isScrollAtBottom && (
                <Box
                    sx={{
                        position: "absolute",
                        bottom: isMobile ? "70px" : "100px",
                        left: "50%",
                        transform: "translateX(-50%)",
                        border: `1px solid ${theme.palette.primary.main}`,
                        backgroundColor: "transparent",
                        color: "#fff",
                        borderRadius: "24px",
                        padding: "8px 16px",
                        boxShadow: "0px 4px 6px rgba(0,0,0,0.2)",
                        animation: "float-in 0.5s ease-in-out",
                        cursor: "pointer",
                    }}
                    onClick={handleNewMessageClick}
                >
                    <Stack direction="row" alignItems="center" spacing={1}>
                        <Avatar sx={{ width: 15, height: 15 }} src={toggleNewMessage.senderAvatar} />
                        <Typography>{toggleNewMessage.content}</Typography>
                    </Stack>
                </Box>
            )}

            <Box sx={{ flexShrink: 0 }}>
                <Footer chatId={chatId} />
            </Box>
        </Stack>
    );
};

export default PrivateChat;