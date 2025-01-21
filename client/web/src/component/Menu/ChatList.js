import { useState } from "react";
import {
    Stack,
    Tooltip,
    IconButton,
    MenuItem,
    ListItemIcon,
    ListItemText,
    Avatar,
    Checkbox,
    TextField,
} from "@mui/material";
import { Send, Close } from "@mui/icons-material";
import { useTheme } from "@mui/material/styles";
import {generateRandomId} from "../Conversation/Footer";
import useWebSocket from "../../hook/useWebSocket";

const ChatList = ({ chatList, message, handleChatListClose }) => {

    const [selectedChats, setSelectedChats] = useState([]);
    const [searchValue, setSearchValue] = useState("");
    const theme = useTheme();
    const { publish } = useWebSocket();

    const handleChatSelect = (chatId) => {
        setSelectedChats((prevSelected) =>
            prevSelected.includes(chatId)
                ? prevSelected.filter((id) => id !== chatId)
                : [...prevSelected, chatId]
        );
    };

    const handleForwardMessages = (selectedChats, message) => {
        selectedChats.forEach((chatId) => {
            const messageBody = {
                randomId: `${new Date().getTime()}-${localStorage.getItem("accountId")}-${chatId}-${generateRandomId()}`,
                senderId: localStorage.getItem("accountId"),
                content: message.content,
                sentTime: new Date().getTime(),
                type: message.type + "_FORWARDED",
                status: "sending",
            };
            setTimeout(() => {
                console.log(`Sending message to chat ${chatId}:`, messageBody);
                publish(`/client/chat/${chatId}`, JSON.stringify(messageBody));
                publish(`/chat/${chatId}/message/send`, JSON.stringify(messageBody));
            }, 500);
        });
        handleChatListClose();
    };


    const filteredChats = chatList.filter((chat) =>
        chat.chatName.toLowerCase().includes(searchValue.toLowerCase())
    );

    const handleSearchChange = (event) => {
        setSearchValue(event.target.value);
    };


    return (
        <Stack
            direction="column"
            spacing={2}
            sx={{
                p: 2,
                border: "1px solid #ccc",
                borderRadius: 2,
                backgroundColor: "#fff",
                maxWidth: 400,
                boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
            }}
        >

            <Stack direction="row" spacing={1} justifyContent="space-between" alignItems="center">
                <Tooltip title="Forward to...">
                    <IconButton
                        onClick={() => handleForwardMessages(selectedChats, message)}
                        sx={{
                            cursor: "pointer",
                            "&:hover": { color: theme.palette.primary.main },
                        }}
                    >
                        <Send />
                    </IconButton>
                </Tooltip>
                <Tooltip title="Close">
                    <IconButton
                        onClick={handleChatListClose}
                        sx={{
                            cursor: "pointer",
                            "&:hover": { color: theme.palette.primary.main },
                        }}
                    >
                        <Close />
                    </IconButton>
                </Tooltip>
            </Stack>

            <TextField
                value={searchValue}
                onChange={handleSearchChange}
                label="Search chats"
                placeholder="Type to search..."
                variant="outlined"
                size="small"
                sx={{
                    borderRadius: 2,
                    "& .MuiOutlinedInput-root": {
                        borderRadius: 2,
                    },
                }}
            />

            <Stack spacing={1} sx={{ maxHeight: "300px", overflowY: "auto" }}>
                {filteredChats.map((chat) => (
                    <MenuItem
                        key={chat.chatId}
                        onClick={() => handleChatSelect(chat.chatId)}
                        sx={{
                            display: "flex",
                            alignItems: "center",
                            "&:hover": {
                                backgroundColor: "#f0f0f0",
                            },
                        }}
                    >
                        <ListItemIcon>
                            <Checkbox checked={selectedChats.includes(chat.chatId)} />
                        </ListItemIcon>
                        <ListItemIcon>
                            <Avatar src={chat.chatAvatar} />
                        </ListItemIcon>
                        <ListItemText primary={chat.chatName} />
                    </MenuItem>
                ))}
            </Stack>
        </Stack>
    );
};

export default ChatList;
