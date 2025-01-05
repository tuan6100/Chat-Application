import { useState, useEffect, useRef, useCallback } from "react";
import useAuth from "../../../hook/useAuth";
import useConversationProperties from "../../../hook/useConversationProperties";
import Header from "../Header";
import { Box, Stack } from "@mui/material";
import Body from "../Body";
import Footer from "../Footer";
import useMediaQuery from "@mui/material/useMediaQuery";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import ScrollBar from "../../ScrollBar";

const PrivateChat = ({ chatId, friendId }) => {

    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
    const { authFetch } = useAuth();
    const { avatar, name, isOnline, lastOnlineTime, setAvatar, setName, setIsOnline, setLastOnlineTime } = useConversationProperties();
    const isMobile = useMediaQuery("(max-width: 600px)");

    const [messages, setMessages] = useState([]);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);
    const messagesEndRef = useRef(null);
    const scrollRef = useRef(null);
    const isFetching = useRef(false);
    const stompClients = useRef({});

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    useEffect(() => {
        const getFriendInfo = async () => {
            try {
                const response = await authFetch(`/api/account/${friendId}/info`);
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
        };
        getFriendInfo();
        const intervalId = setInterval(getFriendInfo, 300000);
        return () => clearInterval(intervalId);
    }, [authFetch, friendId, setAvatar, setName, setIsOnline, setLastOnlineTime]);

    const fetchMessages = useCallback(async (currentPage) => {
        if (isFetching.current || !hasMore) return;
        isFetching.current = true;
        try {
            const response = await authFetch(`/api/chat/${chatId}/messages?page=${currentPage}`);
            if (!response.ok) {
                const error = await response.json();
                console.error("Error during get messages:", error.message);
                setHasMore(false);
                return;
            }
            const data = await response.json();
            if (data.length > 0) {
                setMessages((prevMessages) => [...data, ...prevMessages]);
                setPage(currentPage + 1);
            } else {
                setHasMore(false);
            }
        } catch (error) {
            console.error("Error fetching messages:", error);
        } finally {
            isFetching.current = false;
        }
    }, [authFetch, chatId, hasMore]);

    useEffect(() => {
        fetchMessages(0);
    }, [fetchMessages]);

    const subscribeToChat = (chatId) => {
        if (stompClients.current[chatId]) {
            return;
        }
        const socket = new SockJS(`${API_BASE_URL}/ws`);
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, () => {
            stompClient.subscribe(`/client/chat/${chatId}`, (message) => {
                if (message.body) {
                    const newMessage = JSON.parse(message.body);
                    setMessages((prevMessages) => {
                        const messageExists = prevMessages.some(msg => (
                            (msg.messageId === newMessage.messageId) ||
                            (msg.senderId === newMessage.senderId && msg.sentTime === newMessage.sentTime)
                        ));
                        return messageExists ? prevMessages : [...prevMessages, newMessage];
                    });
                }
            });
        });

        stompClients.current[chatId] = stompClient;
    };

    useEffect(() => {
        setMessages([]);
        setPage(0);
        setHasMore(true);
        subscribeToChat(chatId);
        fetchMessages(0);
        return () => {
            if (stompClients.current[chatId]) {
                stompClients.current[chatId].disconnect();
                delete stompClients.current[chatId];
            }
        };
    }, [chatId, API_BASE_URL]);

    const handleScroll = () => {
        if (scrollRef.current) {
            const { scrollTop, scrollHeight, clientHeight } = scrollRef.current;
            if (scrollTop === 0 && hasMore && !isFetching.current) {
                fetchMessages(page);
            }
        }
    };

    const addNewMessage = (newMessage) => {
        setMessages((prevMessages) => [...prevMessages, newMessage]);
    };

    return (
        <Stack height="100%" maxHeight="100vh" width="auto">
            <Header
                name={name}
                avatar={avatar}
                isOnline={isOnline}
                lastOnlineTime={lastOnlineTime}
            />
            {!isMobile && (
                <ScrollBar>
                    <Box
                        ref={scrollRef}
                        onScroll={handleScroll}
                        width="100%"
                        sx={{ flexGrow: 1, height: "100%", overflowY: "auto" }}
                    >
                        <Body messages={messages} />
                        <div ref={messagesEndRef} />
                    </Box>
                </ScrollBar>
            )}
            <Box sx={{ flexShrink: 0 }}>
                <Footer chatId={chatId} addNewMessage={addNewMessage} />
            </Box>
        </Stack>
    );
};

export default PrivateChat;