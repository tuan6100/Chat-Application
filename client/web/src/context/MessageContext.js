import {createContext, useCallback, useEffect, useRef, useState} from "react";
import useAuth from "../hook/useAuth";


const MessageContext = createContext(undefined);

export const MessageProvider = ({ children }) => {

    const [oldMessagesMap, setOldMessagesMap] = useState(new Map());
    const [newMessagesMap, setNewMessagesMap] = useState(new Map());
    const [rawMessagesMap, setRawMessagesMap] = useState(new Map());
    const [finalMessagesMap, setFinalMessagesMap] = useState(new Map());
    const pendingTimeouts = useRef(new Map());
    const [typingUsers, setTypingUsers] = useState({});
    const [toggleNewMessage, setToggleNewMessage] = useState(null);
    const { authFetch } = useAuth();


    const addNewMessage = useCallback((chatId, newMessage) => {
        console.log("New message:", JSON.stringify(newMessage));
        setNewMessagesMap((prev) => {
            const updatedMap = new Map(prev);
            if (!updatedMap.has(chatId)) {
                updatedMap.set(chatId, []);
            }
            updatedMap.get(chatId).push(newMessage);
            return updatedMap;
        })
        handleServerResponse(chatId, newMessage);
    }, []);

    const handleServerResponse = (chatId, newMessage) => {
        if (newMessage === null) {
            return;
        }
        setNewMessagesMap((prevMessages) => {
            const updatedMessages = new Map(prevMessages);
            let isOk = false;
            if (updatedMessages.has(newMessage.randomId)) {
                const existingMessage = updatedMessages.get(newMessage.randomId);
                updatedMessages.set(newMessage.randomId, {
                    ...existingMessage,
                    ...newMessage,
                    status: 'sent',
                });
                setFinalMessagesMap((prev) => {
                    const updatedFinal = new Map(prev);
                    if (!updatedFinal.has(chatId)) {
                        updatedFinal.set(chatId, []);
                    }
                    updatedFinal.get(chatId).push(updatedMessages.get(newMessage.randomId));
                    return updatedFinal;
                });
                setToggleNewMessage(newMessage);
                clearTimeout(pendingTimeouts.current.get(newMessage.randomId));
                pendingTimeouts.current.delete(newMessage.randomId);
                isOk = true;
            } else {
                updatedMessages.set(newMessage.randomId, newMessage);
                const timeout = setTimeout(() => {
                    handleFailedMessage(chatId, newMessage.randomId, newMessage.senderId);
                }, 60 * 1000);
                pendingTimeouts.current.set(newMessage.randomId, timeout);
            }
            if (isOk) {
                mergeMessages(chatId, newMessage);
                handleSentMessage(chatId, newMessage.randomId, newMessage.senderId);
            }
            return new Map([...updatedMessages.entries()]);
        });
    };


    const mergeMessages = (chatId, newMessage) => {
        const oldMessages = oldMessagesMap.get(chatId) || getMessagesFromSession(chatId);
        const checkAndMerge = () => {
            const newMessages = finalMessagesMap.get(chatId) || [newMessage];
            newMessages.sort((a, b) => new Date(a.sentTime) - new Date(b.sentTime));
            console.log(
                "Merging messages: ",
                JSON.stringify(oldMessages),
                "\nto\n",
                JSON.stringify(newMessages)
            );
            if (!newMessages.some(msg => msg.randomId === newMessage.randomId)) {
                console.log("No new messages, waiting for updates...");
                setTimeout(checkAndMerge, 1000);
                return;
            }
            if (oldMessages.length === 0) {
                console.log("No old messages, returning new messages");
                updateChatDataInSession(chatId, newMessages);
                return;
            }
            const mergedMessages = [];
            let i = 0,
                j = 0;
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
            console.log("Merged messages:", JSON.stringify(mergedMessages));
            updateChatDataInSession(chatId, mergedMessages);
        };
        checkAndMerge();
    };


    const getChatDataFromSession = () => {
        return JSON.parse(sessionStorage.getItem("chatData")) || {};
    };

    const getMessagesFromSession = (chatId) => {
        const chatData = getChatDataFromSession();
        return chatData[chatId]?.messages || [];
    }

    const updateChatDataInSession = (chatId, messages) => {
        const chatData = getChatDataFromSession();
        const existingMessages = chatData[chatId]?.messages || [];
        const combinedMessages = [...existingMessages, ...messages];
        const messagesWithIds = combinedMessages.filter(msg => msg.randomId !== null || msg.messageId !== null);
        const uniqueMessages = Array.from(
            new Map(messagesWithIds.map(msg => [msg.randomId || msg.messageId, msg])).values()
        );
        combinedMessages.forEach(msg => {
            if (msg.randomId === null && msg.messageId === null) {
                uniqueMessages.push(msg);
            }
        });
        chatData[chatId] = {
            messages: uniqueMessages,
            lastAccessed: new Date().getTime(),
            accessCount: (chatData[chatId]?.accessCount || 0) + 1,
        };
        const chatIds = Object.keys(chatData);
        if (chatIds.length > 5) {
            chatIds.sort((a, b) => chatData[a].accessCount - chatData[b].accessCount);
            delete chatData[chatIds[0]];
        }
        sessionStorage.setItem("chatData", JSON.stringify(chatData));
    };

    useEffect(() => {
        const chatData = getChatDataFromSession();
        Object.keys(chatData).forEach(chatId => {
            chatData[chatId].messages.sort((a, b) => new Date(a.sentTime) - new Date(b.sentTime));
        });
    }, [finalMessagesMap, getChatDataFromSession]);


    const handleSentMessage = async (chatId, randomId, senderId) => {
        console.log("Verifying to server...");
        const request = {
            randomId,
            senderId,
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

    const handleFailedMessage = async (chatId, randomId, senderId) => {
        console.log("Verifying to server........");
        const status = "failed";
        const accountId = localStorage.getItem('accountId');
        setNewMessagesMap((prevMessages) => {
            const updatedMessages = new Map(prevMessages);
            if (updatedMessages.has(randomId)) {
                const message = updatedMessages.get(randomId);
                console.log("Failed message:", message.content);
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
            senderId,
            status,
        }
        const response = await authFetch(`/api/chat/${chatId}/message/verify`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(request),
        });
        if (response.ok) {
            setFinalMessagesMap((prev) => {
                const updatedFinal = new Map(prev);
                if (updatedFinal.has(chatId)) {
                    const messages = updatedFinal.get(chatId).filter(message => message.randomId !== randomId);
                    updatedFinal.set(chatId, messages);
                }
                return updatedFinal;
            });
            const chatData = JSON.parse(sessionStorage.getItem("chatData")) || {};
            if (chatData[chatId]) {
                chatData[chatId].messages = chatData[chatId].messages.filter(message => message.randomId !== randomId);
                sessionStorage.setItem("chatData", JSON.stringify(chatData));
            }
        }
    }


    const [unreadNotification, setUnreadNotification] = useState(0);


    return (
        <MessageContext.Provider value={{
            oldMessagesMap, setOldMessagesMap,
            newMessagesMap, setNewMessagesMap,
            rawMessagesMap, setRawMessagesMap,
            finalMessagesMap, setFinalMessagesMap,
            addNewMessage,
            typingUsers, setTypingUsers,
            toggleNewMessage, setToggleNewMessage,
            getMessagesFromSession,
            updateChatDataInSession,
            unreadNotification,
            setUnreadNotification
        }}>
            {children}
        </MessageContext.Provider>
    );

}

export default MessageContext;