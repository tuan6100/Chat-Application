import {Avatar, Box, Stack, Tooltip, Typography} from "@mui/material";
import TextMessage from "../Message/TextMessage";
import ImageMessage from "../Message/ImageMessage";
import VideoMessage from "../Message/VideoMessage";
import AudioMessage from "../Message/AudioMessage";
import FileMessage from "../Message/FileMessage";
import CallMessage from "../Message/CallMessage";
import useMessage from "../../hook/useMessage";
import TypingIndicator from "../Message/TypingIndicator";
import {useCallback, useEffect, useRef, useState} from "react";
import useWebSocket from "../../hook/useWebSocket";
import useSearchResult from "../../hook/useSearchResult";

const Body = ({ chatId, messages, fetchMessages, page, hasMore, isOnline, jumpToMessage }) => {

    const {typingUsers, reactMessage, setReactMessage, deleteMessage, setDeleteMessage, restoreMessage, setRestoreMessage} = useMessage();
    const observerRef = useRef(null);
    const seenMessages = useRef(new Set());
    const {publish} = useWebSocket();
    const [highlightMessageId, setHighlightMessageId] = useState(-1);
    const { searchResults } = useSearchResult();


    const markAsSeen = useCallback((message) => {
        const currentUserId = parseInt(localStorage.getItem("accountId"), 10);
        console.log(`message.senderId: ${message.senderId}, currentUserId: ${currentUserId}`);
        if (message.viewerIds.includes(currentUserId) || message.senderId === currentUserId) {
            return;
        }
        if (!seenMessages.current.has(message.messageId)) {
            seenMessages.current.add(message.messageId);
            console.log(`Message ${message.messageId} marked as seen by user ${currentUserId}`);
        }
    }, []);



    useEffect(() => {
        if (!isOnline) {
            return;
        }
        const observerCallback = (entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    const messageId = entry.target.dataset.messageId;
                    if (messageId) {
                        const message = messages.find((msg) => msg.messageId === messageId);
                        if (message) {
                            markAsSeen(message);
                        }
                    }
                }
            });
        };
        observerRef.current = new IntersectionObserver(observerCallback, {
            root: null,
            threshold: 0.5,
        });
        const messageElements = document.querySelectorAll("[data-message-id]");
        messageElements.forEach((el) => observerRef.current.observe(el));
        return () => {
            observerRef.current.disconnect();
        };
    }, [markAsSeen, messages]);


    useEffect(() => {
        messages.forEach((message) => {
            if (!seenMessages.current.has(message.messageId)) {
                if (message.senderId === parseInt(localStorage.getItem("accountId"), 10)) {
                    return;
                }
                const body = JSON.stringify({
                    messageId: message.messageId,
                    viewerId: localStorage.getItem("accountId"),
                });
                publish(`/chat/${chatId}/message/mark-seen`, body);
                seenMessages.current.add(message.messageId);
            }
        });
    }, [chatId, messages, isOnline]);


    const scrollToMessage = async (messageId) => {
        let targetElement = document.querySelector(`[data-message-id="${messageId}"]`);
        while (!targetElement && hasMore) {
            await fetchMessages(page);
            targetElement = document.querySelector(`[data-message-id="${messageId}"]`);
        }
        if (targetElement) {
            setHighlightMessageId(messageId);
            targetElement.scrollIntoView({ behavior: "smooth", block: "center" });
            targetElement.classList.add("highlight");
            setTimeout(() => {
                setHighlightMessageId(-1);
                targetElement.classList.remove("highlight");
            }, 600);
        } else {
            console.error(`Message with ID ${messageId} not found.`);
        }
    };

    useEffect(() => {
        if (jumpToMessage !== -1) {
            scrollToMessage(jumpToMessage);
        }
    }, [jumpToMessage]);


    useEffect(() => {
        if (reactMessage) {
            const message = messages.find((msg) => msg.messageId === reactMessage.messageId);
            const existingReactionIndex = message.reactions.findIndex(
                (reaction) => reaction.accountId === reactMessage.accountId && reaction.reaction === reactMessage.reaction
            );
            if (existingReactionIndex !== -1) {
                message.reactions.splice(existingReactionIndex, 1);
            } else {
                const accountReactionIndex = message.reactions.findIndex(
                    (reaction) => reaction.accountId === reactMessage.accountId
                );
                if (accountReactionIndex !== -1) {
                    message.reactions[accountReactionIndex].reaction = reactMessage.reaction;
                } else {
                    message.reactions.push({
                        accountId: reactMessage.accountId,
                        reaction: reactMessage.reaction
                    });
                }
            }
            const body = JSON.stringify(reactMessage);
            publish(`/chat/${chatId}/message/update`, body);
            console.log(`React message ${reactMessage.messageId} with ${reactMessage.reaction}`);
            setReactMessage(null);
        }
    }, [reactMessage, chatId]);


    useEffect(() => {
        if (deleteMessage) {
            const body = JSON.stringify(deleteMessage);
            publish(`/chat/${chatId}/message/delete`, body);
            console.log(`Delete message ${deleteMessage}`);
            setDeleteMessage(null);
        }
    }, [deleteMessage]);

    useEffect(() => {
        if (restoreMessage) {
            const body = JSON.stringify(restoreMessage);
            publish(`/chat/${chatId}/message/restore`, body);
            console.log(`Restore message ${restoreMessage}`);
            setRestoreMessage(null);
        }
    }, [restoreMessage]);



    return (
        <Box p={3}>
            <Stack spacing={4}>
                {messages.map((message, index) => {
                    const MessageComponent = (() => {
                        switch (message.type) {
                            case "TEXT":
                            case "TEXT_FORWARDED":
                                return TextMessage;
                            case "IMAGE":
                            case "IMAGE_FORWARDED":
                                return ImageMessage;
                            case "VIDEO":
                            case "VIDEO_FORWARDED":
                                return VideoMessage;
                            case "AUDIO":
                            case "AUDIO_FORWARDED":
                                return AudioMessage;
                            case "FILE":
                            case "FILE_FORWARDED":
                                return FileMessage;
                            case "CALL":
                            case "VIDEO_CALL":
                                return CallMessage;
                            default:
                                return TextMessage;
                        }
                    })();
                    return (
                        <Box
                            key={`${message.messageId}-${index}`}
                            data-message-id={message.messageId}
                            sx={{position: "relative"}}

                        >
                            <MessageComponent message={message} scrollToMessage={scrollToMessage} highlightMessageId={highlightMessageId} />
                        </Box>
                    );
                })}

                {Object.entries(typingUsers[chatId] || {}).map(([senderId, { senderAvatar, typing }]) =>
                        typing && (
                            <Stack key={senderId} direction="row" spacing={1} alignItems="flex-end">
                                <Avatar sx={{ width: 30, height: 30 }} src={senderAvatar} />
                                <Box
                                    p={1}
                                    sx={{
                                        display: "flex",
                                        justifyContent: "start",
                                        mt: 1,
                                        border: "1px solid",
                                        borderRadius: 20,
                                        borderColor: "grey.500",
                                        backgroundColor: "transparent",
                                        p: 1,
                                    }}
                                >
                                    <TypingIndicator />
                                </Box>
                            </Stack>
                        )
                )}
            </Stack>
        </Box>
    );

};


export default Body;