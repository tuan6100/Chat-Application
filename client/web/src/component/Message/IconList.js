import { Stack, IconButton, Tooltip, Popover, Box, MenuItem, MenuList, ListItemIcon, ListItemText } from "@mui/material";
import { MoreVert, Reply, AddReaction, EditNote, DeleteOutline, Shortcut, ContentCopy } from "@mui/icons-material";
import useMessage from "../../hook/useMessage";
import {useEffect, useState} from "react";
import EmojiPicker from "emoji-picker-react";

const IconList = ({ message }) => {

    const { replyMessage, setReplyMessage } = useMessage();
    const [reactionAnchorEl, setReactionAnchorEl] = useState(null);
    const [menuAnchorEl, setMenuAnchorEl] = useState(null);
    const [selectedReaction, setSelectedReaction] = useState(null);
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));


    const handleReactionOpen = (event) => {
        setReactionAnchorEl(event.currentTarget);
    };

    const handleReactionClose = () => {
        setReactionAnchorEl(null);
    };


    const handleMenuOpen = (event) => {
        setMenuAnchorEl(event.currentTarget);
    };

    const handleMenuClose = () => {
        setMenuAnchorEl(null);
    };


    const handleEmojiClick = (emojiData) => {
        const reaction = emojiData.emoji;
        setSelectedReaction(reaction);
        handleReactionClose();
        console.log(`Reaction "${reaction}" sent for message:`, message);
    };

    return (
        <Stack
            spacing={1}
            direction={isMine ? "row-reverse" : "row"}
            className="message-actions"
            sx={{
                position: "relative",
                opacity: 0,
                transition: "opacity 0.3s",
                "&:hover": { opacity: 1 },
            }}
        >
            <Tooltip title="Reply">
                <IconButton
                    sx={{
                        cursor: "pointer",
                        color: "gray",
                        "&:hover": { color: "black" },
                        transform: isMine ? "scaleX(-1)" : "none",
                    }}
                    onClick={() => setReplyMessage(message)}
                >
                    <Reply />
                </IconButton>
            </Tooltip>

            <Tooltip title="React">
                <IconButton
                    sx={{
                        cursor: "pointer",
                        color: "gray",
                        "&:hover": { color: "black" },
                    }}
                    onClick={handleReactionOpen}
                >
                    <AddReaction />
                </IconButton>
            </Tooltip>

            <Popover
                open={Boolean(reactionAnchorEl)}
                anchorEl={reactionAnchorEl}
                onClose={handleReactionClose}
                anchorOrigin={{
                    vertical: "top",
                    horizontal: !isMine ? "left" : "right",
                }}
                transformOrigin={{
                    vertical: "center",
                    horizontal: !isMine ? "left" : "right",
                }}
            >
                <EmojiPicker
                    onEmojiClick={handleEmojiClick}
                    lazyLoadEmojis
                    theme="dark"
                    emojiStyle="facebook"
                    width={300}
                    height={300}
                    searchDisabled={true}
                    reactionsDefaultOpen={true}
                />
            </Popover>

            <Tooltip title="More">
                <IconButton
                    sx={{
                        cursor: "pointer",
                        color: "gray",
                        "&:hover": { color: "black" },
                    }}
                    onClick={handleMenuOpen}
                >
                    <MoreVert />
                </IconButton>
            </Tooltip>
            <Popover
                open={Boolean(menuAnchorEl)}
                anchorEl={menuAnchorEl}
                onClose={handleMenuClose}
                anchorOrigin={{
                    vertical: "top",
                    horizontal: !isMine ? "left" : "right",
                }}
                transformOrigin={{
                    vertical: "center",
                    horizontal: !isMine ? "left" : "right",
                }}
            >
                <MenuList>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon> <EditNote /> </ListItemIcon>
                        <ListItemText> Edit </ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon> <Shortcut /> </ListItemIcon>
                        <ListItemText> Forward </ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon> <ContentCopy  /> </ListItemIcon>
                        <ListItemText> Copy all </ListItemText>
                    </MenuItem>
                    <MenuItem onClick={handleMenuClose}>
                        <ListItemIcon> <DeleteOutline  /> </ListItemIcon>
                        <ListItemText> Unsend </ListItemText>
                    </MenuItem>
                </MenuList>
            </Popover>
        </Stack>
    );
};

export default IconList;