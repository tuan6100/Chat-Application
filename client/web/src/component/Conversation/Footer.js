import React, { useState } from "react";
import {
    IconButton,
    InputBase,
    Stack,
    Paper, Tooltip, InputAdornment,
} from "@mui/material";
import {
    AttachFile, Mood,
    SettingsVoice
} from "@mui/icons-material";
import {
    Image,
    Sticker,
} from "phosphor-react";
import {useTheme} from "@mui/material/styles";
import SendButton from "../SendButton";

const ConversationFooter = () => {

    const [message, setMessage] = useState("");
    const theme = useTheme()

    const handleSendMessage = () => {
        if (message.trim() !== "") {
            console.log("Message sent:", message);
            setMessage("");
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter" && e.ctrlKey) {
            e.preventDefault();
            handleSendMessage();
        } else if (e.key === "Enter" && !e.ctrlKey) {
            setMessage((prev) => prev + "\n");
        }
    };

    return (
        <Paper
            component="form"
            sx={{
                display: "flex",
                alignItems: "center",
                p: 1,
                backgroundColor: "white",
                borderTop: "1px solid #E0E0E0",
                boxShadow: "0px -1px 5px rgba(0, 0, 0, 0.1)",
                width: '100%',
            }}
        >
            <Stack direction="row" spacing={1} alignItems="center">

                <Tooltip title="File" >
                    <IconButton>
                        <AttachFile
                            size={24}
                            sx={{
                                color: theme.palette.primary.main
                            }}
                        />
                    </IconButton>
                </Tooltip>


                <Tooltip title="Image">
                    <IconButton color="primary">
                        <Image size={24} sx={{ borderRadius: '50%' }} />
                    </IconButton>
                </Tooltip>


                <Tooltip title="Voice Record">
                    <IconButton>
                        <SettingsVoice
                            size={24}
                            sx={{
                                color: theme.palette.primary.main
                            }}
                        />
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
                            <IconButton>
                                <Mood
                                    sx={{
                                        color: theme.palette.primary.main,
                                        fontSize: 28
                                    }}
                                />
                            </IconButton>
                        </Tooltip>
                    </InputAdornment>
                }
            />

            <SendButton handleSendMessage={handleSendMessage} />


        </Paper>
    );
};

export default ConversationFooter;