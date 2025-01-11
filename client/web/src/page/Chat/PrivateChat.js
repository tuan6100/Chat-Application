import { useCallback, useEffect, useRef, useState } from "react";
import useAuth from "../../hook/useAuth";
import useConversationProperties from "../../hook/useConversationProperties";
import Header from "../../component/Conversation/Header";
import { Box, Stack } from "@mui/material";
import Body from "../../component/Conversation/Body";
import Footer from "../../component/Conversation/Footer";
import useMediaQuery from "@mui/material/useMediaQuery";
// import SockJS from "sockjs-client";
// import { Stomp } from "@stomp/stompjs";
import ScrollBar from "../../component/ScrollBar";

const PrivateChat = ({ friendId, oldMessages, newMessage }) => {

    const { authFetch } = useAuth();
    const { avatar, name, isOnline, lastOnlineTime, setAvatar, setName, setIsOnline, setLastOnlineTime } = useConversationProperties();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const pendingTimeouts = useRef(new Map());
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const messagesEndRef = useRef(null);
    const scrollRef = useRef(null);
    const isFetching = useRef(false);
    const chatId = parseInt(localStorage.getItem('chatId'));
    const [newMessageSortedMap, setNewMessagesSortedMap] = useState(new Map());
    const [oldMessageList, setOldMessageList] = useState([]);

    useEffect(() => {
        setOldMessageList(oldMessages)
        console.log("oldMessages", oldMessageList)
    }, [oldMessages]);

    useEffect(() => {
        if (newMessage && newMessage.randomId) {
            setNewMessagesSortedMap(prevMap => {
                const newMap = new Map(prevMap || []);
                newMap.set(newMessage.randomId, newMessage);
                return newMap;
            });
            console.log("newMessage", newMessage);
            console.log("newMessageSortedMap", newMessageSortedMap);
        } else {
            console.error("newMessage does not have a valid randomId", newMessage);
        }
    }, [newMessage]);


    const getChatDataFromSession = () => {
        return JSON.parse(sessionStorage.getItem("chatData")) || {};
    };

    const updateChatDataInSession = (chatId, messages) => {
        const chatData = getChatDataFromSession();
        chatData[chatId] = {
            messages,
            lastAccessed: new Date().getTime(),
            accessCount: (chatData[chatId]?.accessCount || 0) + 1
        };
        const chatIds = Object.keys(chatData);
        if (chatIds.length > 5) {
            chatIds.sort((a, b) => chatData[a].accessCount - chatData[b].accessCount);
            delete chatData[chatIds[0]];
        }
        sessionStorage.setItem("chatData", JSON.stringify(chatData));
    };

    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [oldMessageList]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [oldMessageList]);


    useEffect(() => {
        if (friendId === 0) {
            return ;
        }
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


    const fetchMessages = useCallback(async (page) => {
        if (isFetching.current || !hasMore) return;
        isFetching.current = true;
        try {
            const response = await authFetch(`/api/chat/${chatId}/messages?page=${page}`);
            if (!response.ok) {
                const error = await response.json();
                console.error("Error during get messages:", error.message);
                setHasMore(false);
                return;
            }
            const data = await response.json();
            if (data.length > 0) {
                setOldMessageList(data);
                setPage(page + 1);
                updateChatDataInSession(chatId, [...data, ...oldMessages]);
            } else {
                setHasMore(false);
            }
        } catch (error) {
            console.error("Error fetching messages:", error);
        } finally {
            isFetching.current = false;
        }
    }, [authFetch, chatId, hasMore, oldMessages]);

    const handleScroll = () => {
        if (scrollRef.current) {
            const { scrollTop } = scrollRef.current;
            if (scrollTop === 0 && hasMore && !isFetching.current) {
                fetchMessages(1);
            }
        }
    };


    const handleServerResponse = (newMessage) => {
        if (newMessage === null) {
            return;
        }
        let shouldSendMessage = false;
        setNewMessagesSortedMap((prevMessages) => {
            const updatedMessages = new Map(prevMessages);
            if (updatedMessages.has(newMessage.randomId)) {
                newMessage.status = 'sent';
                updatedMessages.set(newMessage.randomId, newMessage);
                clearTimeout(pendingTimeouts.current.get(newMessage.randomId));
                pendingTimeouts.current.delete(newMessage.randomId);
            } else {
                updatedMessages.set(newMessage.randomId, newMessage);
                shouldSendMessage = true;
                const timeout = setTimeout(() => {
                    handleFailedMessage(newMessage.randomId);
                }, 5 * 60 * 1000);
                pendingTimeouts.current.set(newMessage.randomId, timeout);
            }
            return new Map([...updatedMessages.entries()].sort((a, b) => a[1].timeSent - b[1].timeSent));
        });
        if (shouldSendMessage) {
            handleSentMessage(newMessage.randomId);
        }
    };


    useEffect(() => {
        handleServerResponse(newMessage);
    }, [newMessage]);

    const mergeMessages = (oldMessages, newMessages) => {
        if (oldMessages.length === 0) {
            return newMessages;
        }
        if (newMessages.length === 0) {
            return oldMessages;
        }
        const mergedMessages = [];
        let i = 0, j = 0;
        while (i < oldMessages.length && j < newMessages.length) {
            if (new Date(oldMessages[i].sentTime) <= new Date(newMessages[j].sentTime)) {
                mergedMessages.push(oldMessages[i]);
                i++;
            } else {
                mergedMessages.push(newMessages[j]);
                j++;
            }
        }
        while (i < oldMessages.length) {
            mergedMessages.push(oldMessages[i]);
            i++;
        }
        while (j < newMessages.length) {
            mergedMessages.push(newMessages[j]);
            j++;
        }
        return mergedMessages;
    };

    useEffect(() => {
        const chatData = getChatDataFromSession();
        chatData[chatId] = mergeMessages(oldMessageList, newMessageSortedMap);
        console.log("Merged Messages", chatData[chatId])
        sessionStorage.setItem("chatData", JSON.stringify(chatData));
    }, [oldMessageList, newMessageSortedMap]);

    const handleSentMessage = async (randomId) => {
        const request = {
            randomId,
            status: "sent"
        }
        await authFetch(`/api/chat/${chatId}/message/verify`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(request),
        });
    }

    const handleFailedMessage = async (randomId) => {
        const status = "failed";
        const accountId = localStorage.getItem('accountId');
        setNewMessagesSortedMap((prevMessages) => {
            const updatedMessages = new Map(prevMessages);
            if (updatedMessages.has(randomId)) {
                const message = updatedMessages.get(randomId);
                if (message.senderId.toString() === accountId) {
                    const failedMessage = {
                        ...message,
                        status
                    };
                    updatedMessages.set(randomId, failedMessage);
                }
            }
            return updatedMessages;
        });
        const request = {
            randomId,
            status,
        }
        await authFetch(`/api/chat/${chatId}/message/verify`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(request),
        });
    }


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
                        <Body chatId={chatId} />
                        <div ref={messagesEndRef} />
                    </Box>
                </ScrollBar>
            )}
            <Box sx={{ flexShrink: 0 }}>
                <Footer chatId={chatId} />
            </Box>
        </Stack>

    );
};

export default PrivateChat;