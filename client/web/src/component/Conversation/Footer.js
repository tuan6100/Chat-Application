import React, {useEffect, useRef, useState} from "react";
import {
    IconButton,
    InputBase,
    Stack,
    Paper, Tooltip, InputAdornment, Typography, Box,
} from "@mui/material";
import {
    AttachFile,
    SettingsVoice
} from "@mui/icons-material";
import {
    Image,
    Sticker,
    SmileyBlank,
    XCircle
} from "phosphor-react";
import {useTheme} from "@mui/material/styles";
import SendButton from "../SendButton";
import useMessage from "../../hook/useMessage";
import useWebSocket from "../../hook/useWebSocket";

export const generateRandomId = () => {
    return crypto.getRandomValues(new Uint32Array(1))[0].toString(16);
};

const Footer = ({chatId}) => {

    const [message, setMessage] = useState("");
    const [recording, setRecording] = useState(false);
    const fileInputRef = useRef(null);
    const audioRecorderRef = useRef(null);
    const theme = useTheme();
    const [mediaBlob, setMediaBlob] = useState(null);
    const { setRawMessagesMap, replyMessage, setReplyMessage, editMessage, setEditMessage } = useMessage();
    const { publish } = useWebSocket();
    const {  } = useMessage();
    const [isTyping, setIsTyping] = useState(false);
    let typingTimeout = useRef(null);


    const handleSendMessage = () => {
        let messageBody = {};
        if (editMessage === null) {
            if (message.trim() !== "") {
                if (isTyping) {
                    setIsTyping(false);
                    clearTimeout(typingTimeout.current);
                    const body = JSON.stringify({
                        senderId: localStorage.getItem("accountId"),
                        typing: false,
                        chatId: chatId,
                    });
                    publish(`/client/chat/${chatId}/typing`, body);
                }
                messageBody = {
                    randomId: `${new Date().getTime()}-${localStorage.getItem("accountId")}-${chatId}-${generateRandomId()}`,
                    senderId: localStorage.getItem("accountId"),
                    content: message,
                    sentTime: new Date().getTime(),
                    type: "TEXT",
                    replyToMessageId: Object.keys(replyMessage).length === 0 ? null : replyMessage.messageId,
                    replyToMessageContent: Object.keys(replyMessage).length === 0 ? null : replyMessage.content,
                    status: "sending",
                };
                setRawMessagesMap((prev) => {
                    const updatedRawMap = new Map(prev);
                    if (!updatedRawMap.has(chatId)) {
                        updatedRawMap.set(chatId, []);
                    }
                    updatedRawMap.get(chatId).push(messageBody);
                    return updatedRawMap;
                });
                setTimeout(() => {
                    publish(`/client/chat/${chatId}`, JSON.stringify(messageBody));
                    publish(`/chat/${chatId}/message/send`, JSON.stringify(messageBody));
                }, 500);
                setReplyMessage({});
            }
        } else {
            setMessage(editMessage.content);
            messageBody = {
                messageId: editMessage.messageId,
                accountId: null,
                content: message,
                reaction: null,
            }
            console.info("Editing message:", messageBody);
            publish(`/chat/${chatId}/message/update`, JSON.stringify(messageBody));
            setEditMessage(null);
        }
        setMessage("");
    };

    useEffect(() => {
        if (editMessage !== null) {
            setMessage(editMessage.content);
        }
    }, [editMessage]);

    const handleTyping = () => {
        if (!isTyping) {
            setIsTyping(true);
            console.log("Start typing...");
            publish(
                `/client/chat/${chatId}/typing`,
                JSON.stringify({
                    senderId: localStorage.getItem("accountId"),
                    senderAvatar: localStorage.getItem("avatar"),
                    typing: true,
                    chatId: chatId,
                })
            );
        }
        clearTimeout(typingTimeout.current);
        typingTimeout.current = setTimeout(() => {
            setIsTyping(false);
            console.log("Stop typing...");
            publish(
                `/client/chat/${chatId}/typing`,
                JSON.stringify({
                    senderId: localStorage.getItem("accountId"),
                    senderAvatar: localStorage.getItem("avatar"),
                    typing: false,
                    chatId: chatId,
                })
            );
        }, 1000);
    };


    const handleKeyDown = (e) => {
        handleTyping();
        if (e.ctrlKey && e.key === "Enter") {
            e.preventDefault();
            handleSendMessage();
        } else if (e.key === "Enter" && !e.ctrlKey) {
            e.preventDefault();
            setMessage((prev) => prev + "\n");
        }
    };


    const handleImageUpload = async (e) => {

    }

    const handleFileUpload = (event) => {
        const file = event.target.files[0];
        // if (file) {
        //     sendMessage(file, file.type.startsWith("image") ? "img" : "file");
        // }
    };


    const handleFileSelect = () => {
        fileInputRef.current.click();
    };

    const handleVoiceRecord = async () => {
        if (!recording) {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
                const mediaRecorder = new MediaRecorder(stream);
                audioRecorderRef.current = mediaRecorder;

                const chunks = [];
                mediaRecorder.ondataavailable = (event) => {
                    chunks.push(event.data);
                };

                mediaRecorder.onstop = () => {
                    const blob = new Blob(chunks, { type: "audio/webm" });
                    setMediaBlob(blob);
                    // sendMessage(blob, "audio");
                };

                mediaRecorder.start();
                setRecording(true);
            } catch (error) {
                console.error("Error accessing microphone:", error);
            }
        } else {
            audioRecorderRef.current.stop();
            setRecording(false);
        }
    };



    return (
        <Stack
            spacing={1}
            sx={{
                width: '100%',
                position: 'relative',
                backgroundColor: "transparent",
                borderTop: "1px solid #E0E0E0",
                boxShadow: "0px -1px 5px rgba(0, 0, 0, 0.1)",
            }}
        >
            <Paper
                elevation={1}
                sx={{
                    p: 1,
                    display: "flex",
                    flexDirection: "column",
                    gap: 1,
                    backgroundColor: "transparent",
                    border: "1px solid #3F3C3CFF",
                    borderRadius: 1,
                    width: '100%',
                }}
            >
                {replyMessage && Object.keys(replyMessage).length > 0 && (
                    <Box
                        sx={{
                            mb: 1,
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                            borderRadius: 1,
                            backgroundColor: "#f9f9f9",
                            p: 1,
                            border: "1px solid #E0E0E0",
                            width: '97.5%',
                        }}
                    >
                        <Stack spacing={0.5}>
                            <Typography fontWeight="bold" color="primary">
                                Replying to {replyMessage.senderId.toString() === localStorage.getItem('accountId') ? 'yourself' : replyMessage.senderUsername}
                            </Typography>
                            <Typography color="text.secondary">
                                {replyMessage.content}
                            </Typography>
                        </Stack>
                        <IconButton color="primary" onClick={() => setReplyMessage({})}>
                            <XCircle />
                        </IconButton>
                    </Box>
                )}

                {editMessage !== null && (
                    <Box
                        sx={{
                            mb: 1,
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                            borderRadius: 1,
                            backgroundColor: "#f9f9f9",
                            p: 1,
                            border: "1px solid #E0E0E0",
                            width: '97.5%',
                        }}
                    >
                        <Stack spacing={0.5}>
                            <Typography fontWeight="bold" color="primary">
                                Editing message
                            </Typography>
                            <Typography color="text.secondary">
                                {editMessage.content}
                            </Typography>
                        </Stack>
                        <IconButton color="primary" onClick={() => {
                            setEditMessage(null);
                            setMessage("");
                        }}>
                            <XCircle />
                        </IconButton>
                    </Box>
                )}

                <Box
                    component="form"
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        width: '100%',
                    }}
                >
                    <Stack direction="row" spacing={1} alignItems="center">
                        <Tooltip title="File">
                            <IconButton color="primary" onClick={handleFileSelect}>
                                <AttachFile size={24} />
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Image">
                            <IconButton color="primary" onClick={handleFileSelect}>
                                <Image />
                            </IconButton>
                        </Tooltip>

                        <Tooltip title={recording ? "Stop Recording" : "Voice Record"}>
                            <IconButton onClick={handleVoiceRecord}>
                                <SettingsVoice
                                    sx={{
                                        color: recording
                                            ? theme.palette.error.main
                                            : theme.palette.primary.main,
                                    }}
                                />
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Sticker">
                            <IconButton color="primary">
                                <Sticker size={24} />
                            </IconButton>
                        </Tooltip>
                    </Stack>

                    <InputBase
                        sx={{
                            ml: 2,
                            flex: 1,
                            borderRadius: 4,
                            pl: 2,
                            pr: 0,
                            border: "1px solid #E0E0E0",
                            transition: "all 0.3s",
                            '&:hover': {
                                borderColor: theme.palette.primary.main,
                            },
                            '&:focus-within': {
                                borderColor: theme.palette.primary.main,
                                boxShadow: `0px 0px 5px ${theme.palette.primary.light}`,
                            },
                        }}
                        placeholder="Message..."
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        onKeyDown={handleKeyDown}
                        multiline
                        endAdornment={
                            <InputAdornment position="end">
                                <Tooltip title="Emoji">
                                    <IconButton color="primary">
                                        <SmileyBlank size={28} />
                                    </IconButton>
                                </Tooltip>
                            </InputAdornment>
                        }
                    />

                    <SendButton handleSendMessage={handleSendMessage} />

                    <input
                        type="file"
                        ref={fileInputRef}
                        style={{ display: "none" }}
                        onChange={handleFileUpload}
                    />
                </Box>
            </Paper>
        </Stack>
    );
}

export default Footer;