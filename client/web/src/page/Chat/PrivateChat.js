import { useCallback, useEffect, useRef, useState } from "react";
import useAuth from "../../hook/useAuth";
import useConversationProperties from "../../hook/useConversationProperties";
import Header from "../../component/Conversation/Header";
import { Box, Stack } from "@mui/material";
import Body from "../../component/Conversation/Body";
import Footer from "../../component/Conversation/Footer";
import useMediaQuery from "@mui/material/useMediaQuery";
import ScrollBar from "../../component/ScrollBar";
import useMessage from "../../hook/useMessage";
import SockJS from "sockjs-client";
import {Stomp} from "@stomp/stompjs";

// const useSessionStorage = (key) => {
//     const [storedValue, setStoredValue] = useState(() => {
//         try {
//             const item = sessionStorage.getItem(key);
//             return item ? JSON.parse(item) : {};
//         } catch (error) {
//             console.error(error);
//             return {};
//         }
//     });
//
//     useEffect(() => {
//         const handleStorageChange = () => {
//             try {
//                 const item = sessionStorage.getItem(key);
//                 setStoredValue(item ? JSON.parse(item) : {});
//             } catch (error) {
//                 console.error(error);
//             }
//         };
//
//         window.addEventListener("storage", handleStorageChange);
//         return () => {
//             window.removeEventListener("storage", handleStorageChange);
//         };
//     }, [key]);
//
//     return storedValue;
// };


const PrivateChat = ({ friendId, chatId }) => {

    const { authFetch } = useAuth();
    const { avatar, name, isOnline, lastOnlineTime, setAvatar, setName, setIsOnline, setLastOnlineTime } = useConversationProperties();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const [page, setPage] = useState(1);
    const [hasMore, setHasMore] = useState(true);
    const messagesEndRef = useRef(null);
    const scrollRef = useRef(null);
    const scrollBarRef = useRef(null);
    const isFetching = useRef(false);
    const isAutoScrolling = useRef(false);
    // const chatData = useSessionStorage("chatData");
    const { getMessagesFromSession, updateChatDataInSession, rawMessagesMap, finalMessagesMap, setTypingUsers, typingUsers } = useMessage();
    const [messageList, setMessageList] = useState([]);
    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
    let currentPage = 0;


    useEffect(() => {
        if (getMessagesFromSession(chatId).length === 0) {
            fetchMessages(currentPage++)
        } else {
            setMessageList(getMessagesFromSession(chatId));
            console.log("Message list:", getMessagesFromSession(chatId));
        }
    }, [chatId]);


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


    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [messageList, typingUsers]);

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messageList, typingUsers]);

    const handleScrollUp = () => {
        if (!scrollRef.current || !hasMore || isFetching.current) {
            console.log('Cannot fetch: No ref, no more data, or already fetching');
            return;
        }

        const scrollElement = scrollRef.current; // Không cần xử lý phức tạp
        console.log('Scroll position:', scrollElement.scrollTop);

        if (scrollElement.scrollTop === 0) {
            console.log('Fetching more messages...');
            fetchMessages(page);
        }
    };



    useEffect(() => {
        const handleScroll = () => {
            const scrollElement = scrollBarRef.current?.el;
            console.log('Scroll event triggered');
            if (scrollElement.scrollTop === 0 && hasMore && !isFetching.current) {
                console.log('Conditions met for handleScrollUp');
                handleScrollUp();
            }
        };
        const scrollElement = scrollBarRef.current?.el;
        if (scrollElement) {
            console.log('Adding scroll event listener');
            scrollElement.addEventListener('scroll', handleScroll);
        }

        return () => {
            if (scrollElement) {
                console.log('Removing scroll event listener');
                scrollElement.removeEventListener('scroll', handleScroll);
            }
        };
    }, [hasMore, isFetching, handleScrollUp]);


    const fetchMessages = useCallback(async (page) => {
        console.log('Fetching messages for page:', page);
        if (isFetching.current || !hasMore) return;

        isFetching.current = true;
        try {
            const response = await authFetch(`/api/chat/${chatId}/messages?page=${page}`);
            console.log('Fetch response:', response);

            if (!response.ok) {
                const error = await response.json();
                console.error("Error during get messages:", error.message);
                setHasMore(false);
                return;
            }

            const data = await response.json();
            console.log('Fetched messages:', data);

            if (data.length > 0) {
                setMessageList((prev) => [...data, ...prev]);
                updateChatDataInSession(chatId, [...data, ...messageList]);
                setPage(page + 1);
            } else {
                setHasMore(false);
            }
        } catch (error) {
            console.error("Error fetching messages:", error);
        } finally {
            isFetching.current = false;
        }
    }, [authFetch, chatId, hasMore, messageList, page]);



    useEffect(() => {
        const newMessages = finalMessagesMap.get(chatId) || [];
        console.log("New messages:", JSON.stringify(newMessages));
        const rawMessages = rawMessagesMap.get(chatId) || [];
        if (newMessages.length > 0 || rawMessages.length > 0) {
            setMessageList((prevMessages) => {
                const updatedMessages = [...prevMessages];
                newMessages.forEach((newMessage) => {
                    const index = updatedMessages.findIndex(msg => msg.randomId === newMessage.randomId);
                    if (index !== -1) {
                        updatedMessages[index] = newMessage;
                    } else {
                        updatedMessages.push(newMessage);
                    }
                });
                rawMessages.forEach((rawMessage) => {
                    const index = updatedMessages.findIndex(msg => msg.randomId === rawMessage.randomId);
                    if (index === -1) {
                        updatedMessages.push(rawMessage);
                    }
                });
                return updatedMessages;
            });
        }
    }, [finalMessagesMap, rawMessagesMap, chatId]);


    useEffect(() => {
        const socket = new SockJS(`${API_BASE_URL}/ws`);
        const stompClient = Stomp.over(socket);
        stompClient.connect({}, () => {
            stompClient.subscribe(`/client/chat/${chatId}/typing`, (message) => {
                const { senderId, senderAvatar, typing } = JSON.parse(message.body);
                if (senderId !== localStorage.getItem("accountId")) {
                    if (typing) {
                        setTypingUsers((prev) => ({
                            ...prev,
                            [senderId]: { senderAvatar, typing },
                        }));
                    } else {
                        setTypingUsers((prev) => {
                            const updated = { ...prev };
                            delete updated[senderId];
                            return updated;
                        });
                    }
                }
            });
        }, (error) => {
            console.error("STOMP connection error:", error);
        });
        return () => {
            stompClient.disconnect();
        };
    }, [chatId]);




    return (
        <Stack height="100%" maxHeight="100vh" width="auto">
            <Header
                name={name}
                avatar={avatar}
                isOnline={isOnline}
                lastOnlineTime={lastOnlineTime}
            />

            <ScrollBar onScroll={handleScrollUp} ref={scrollRef}>
                <Box
                    width="100%"
                    sx={{ flexGrow: 1, height: "100%" }}
                >
                    <Body messages={messageList} />
                    <div ref={messagesEndRef} />
                </Box>
            </ScrollBar>

            <Box sx={{ flexShrink: 0 }}>
                <Footer chatId={chatId} />
            </Box>
        </Stack>

    );
};

export default PrivateChat;