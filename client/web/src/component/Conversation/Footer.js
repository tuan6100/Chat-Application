import React, {useEffect, useRef, useState} from "react";
import {
    IconButton,
    InputBase,
    Stack,
    Paper, Tooltip, InputAdornment,
} from "@mui/material";
import {
    AttachFile,
    SettingsVoice
} from "@mui/icons-material";
import {
    Image,
    Sticker,
    SmileyBlank
} from "phosphor-react";
import {useTheme} from "@mui/material/styles";
import SendButton from "../SendButton";
import SockJS from "sockjs-client";
import {Client} from "@stomp/stompjs";
import useMessage from "../../hook/useMessage";

const Footer = ({chatId}) => {

    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
    const [message, setMessage] = useState("");
    const [recording, setRecording] = useState(false);
    const fileInputRef = useRef(null);
    const audioRecorderRef = useRef(null);
    const theme = useTheme();
    const [stompClient, setStompClient] = useState(null);
    const [mediaBlob, setMediaBlob] = useState(null);
    const { setRawMessagesMap } = useMessage();
    const [isTyping, setIsTyping] = useState(false);
    let typingTimeout = useRef(null);



    useEffect(() => {
        const socket = new SockJS(`${API_BASE_URL}/ws`);
        const stomp = new Client({
            webSocketFactory: () => socket,
            debug: (str) => console.log(str),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });
        stomp.activate();
        setStompClient(stomp);
        return () => {
            if (stompClient) {
                stompClient.deactivate();
            }
        };
    }, [chatId]);


    const publishRawMessage = (message) => {
        stompClient.publish({
            destination: `/client/chat/${chatId}`,
            body: JSON.stringify(message),
        });
    };

    const sendMessageToServer = (request) => {
        stompClient.publish({
            destination: `/chat/${chatId}/message/send`,
            body: JSON.stringify(request),
        });
    };

    const generateRandomId = () => {
        return crypto.getRandomValues(new Uint32Array(1))[0].toString(16);
    };

    const handleSendMessage = async () => {
        if (message.trim() !== "") {
            if (isTyping) {
                setIsTyping(false);
                stompClient.publish({
                    destination: `/client/chat/${chatId}/typing`,
                    body: JSON.stringify({
                        senderId: localStorage.getItem("accountId"),
                        typing: false,
                    }),
                });
                clearTimeout(typingTimeout.current);
            }
            const messageBody = {
                randomId: `${new Date().getTime()}-${localStorage.getItem("accountId")}-${chatId}-${generateRandomId()}`,
                senderId: localStorage.getItem("accountId"),
                content: message,
                sentTime: new Date().getTime(),
                type: "TEXT",
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
            publishRawMessage(messageBody);
            sendMessageToServer(messageBody);
            setMessage("");
        }
    };


    const handleTyping = () => {
        if (!isTyping) {
            setIsTyping(true);
            stompClient.publish({
                destination: `/client/chat/${chatId}/typing`,
                body: JSON.stringify({
                    senderId: localStorage.getItem("accountId"),
                    senderAvatar: localStorage.getItem("avatar"),
                    typing: true,
                }),
            });
        }
        clearTimeout(typingTimeout.current);
        typingTimeout.current = setTimeout(() => {
            setIsTyping(false);
            stompClient.publish({
                destination: `/client/chat/${chatId}/typing`,
                body: JSON.stringify({
                    senderId: localStorage.getItem("accountId"),
                    typing: false,
                }),
            });
        }, 2000);
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
        <Paper
            component="form"
            sx={{
                display: "flex",
                alignItems: "center",
                p: 1,
                backgroundColor: "transparent",
                borderTop: "1px solid #E0E0E0",
                boxShadow: "0px -1px 5px rgba(0, 0, 0, 0.1)",
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
                        <Image  />
                    </IconButton>
                </Tooltip>

                <Tooltip title={recording ? "Stop Recording" : "Voice Record"}>
                    <IconButton onClick={handleVoiceRecord}>
                        <SettingsVoice sx={{
                            color: recording ? theme.palette.error.main : theme.palette.primary.main
                        }} />
                    </IconButton>
                </Tooltip>

                <Tooltip title="Sticker">
                    <IconButton color="primary" >
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
                    }
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

            <SendButton handleSendMessage={handleSendMessage}/>

            <input
                type="file"
                ref={fileInputRef}
                style={{ display: "none" }}
                onChange={handleFileUpload}
            />
        </Paper>
    );
};

export default Footer;