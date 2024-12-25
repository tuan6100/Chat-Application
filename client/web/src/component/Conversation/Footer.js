import React, { useState } from "react";
import {
    IconButton,
    InputBase,
    Stack,
    Paper,
} from "@mui/material";
import {
    Paperclip,
    ImageSquare,
    Microphone,
    Smiley,
    PaperPlaneTilt,
} from "phosphor-react";

const ChatFooter = () => {
    const [message, setMessage] = useState("");

    const handleSendMessage = () => {
        if (message.trim() !== "") {

            setMessage("");
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
            }}
        >
            <Stack direction="row" spacing={1} alignItems="center">
                {/* Gửi file */}
                <IconButton>
                    <Paperclip size={24} />
                </IconButton>

                {/* Gửi ảnh */}
                <IconButton>
                    <ImageSquare size={24} />
                </IconButton>

                {/* Ghi âm */}
                <IconButton>
                    <Microphone size={24} />
                </IconButton>

                {/* Sticker */}
                <IconButton>
                    <Smiley size={24} />
                </IconButton>
            </Stack>

            {/* Thanh nhập tin nhắn */}
            <InputBase
                sx={{ ml: 2, flex: 1 }}
                placeholder="Message..."
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                onKeyDown={(e) => {
                    if (e.key === "Enter") {
                        e.preventDefault();
                        handleSendMessage();
                    }
                }}
            />

            {/* Nút gửi tin nhắn */}
            <IconButton color="primary" onClick={handleSendMessage}>
                <PaperPlaneTilt size={28} weight="fill" />
            </IconButton>
        </Paper>
    );
};

export default ChatFooter;
