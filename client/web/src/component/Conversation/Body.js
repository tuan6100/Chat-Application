import {Avatar, Box, Stack, Tooltip, Typography} from "@mui/material";
import {Reply, MoreVert} from "@mui/icons-material";
import TextMessage from "../Message/TextMessage";
import ImageMessage from "../Message/ImageMessage";
import VideoMessage from "../Message/VideoMessage";
import AudioMessage from "../Message/AudioMessage";
import FileMessage from "../Message/FileMessage";
import LinkMessage from "../Message/LinkMessage";
import useMessage from "../../hook/useMessage";
import TypingIndicator from "../Message/TypingIndicator";
import {useCallback, useEffect, useRef} from "react";
import useWebSocket from "../../hook/useWebSocket";

const Body = ({ chatId, messages }) => {

    const {typingUsers} = useMessage();
    const observerRef = useRef(null);
    const seenMessages = useRef(new Set());
    const {publish} = useWebSocket();


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
    }, [chatId, messages]);


    const scrollToMessage = (messageId) => {
        const targetElement = document.querySelector(`[data-message-id="${messageId}"]`);
        if (targetElement) {
            targetElement.scrollIntoView({ behavior: "smooth", block: "center" });
        }
    };


    return (
        <Box p={3}>
            <Stack spacing={3}>
                {messages.map((message, index) => {
                    const MessageComponent = (() => {
                        switch (message.type) {
                            case "IMAGE":
                                return ImageMessage;
                            case "VIDEO":
                                return VideoMessage;
                            case "AUDIO":
                                return AudioMessage;
                            case "LINK":
                                return LinkMessage;
                            case "FILE":
                                return FileMessage;
                            default:
                                return TextMessage;
                        }
                    })();
                    return (
                        <Box
                            key={`${message.messageId}-${index}`}
                            data-message-id={message.messageId}
                            sx={{ position: "relative" }}
                        >
                            {message.replyToMessageId && message.replyToMessageContent && (
                                <Box
                                    onClick={() => scrollToMessage(message.replyToMessageId)}
                                    sx={{
                                        position: "absolute",
                                        width: 'fit-content',
                                        backgroundColor: "rgba(240, 240, 240, 0.9)",
                                        border: "1px solid #ccc",
                                        borderRadius: "15px",
                                        padding: "8px",
                                        cursor: "pointer",
                                        zIndex: 1,
                                    }}
                                >
                                    <Typography variant="body2" color="text.secondary">
                                        {message.replyToMessageContent}
                                    </Typography>
                                </Box>
                            )}
                            <MessageComponent message={message} />
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