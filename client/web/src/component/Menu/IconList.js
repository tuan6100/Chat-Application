import {
    Stack,
    IconButton,
    Tooltip,
    Popover,
    MenuItem, MenuList, ListItemIcon, ListItemText,
    Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle,
    Button,
    Alert,
    Snackbar
} from "@mui/material";
import {
    MoreVert,
    Reply,
    AddReaction,
    EditNote,
    DeleteOutline,
    Forward,
    ContentCopy,
    Download
} from "@mui/icons-material";
import useMessage from "../../hook/useMessage";
import {useState} from "react";
import EmojiPicker from "emoji-picker-react";
import ChatList from "./ChatList";
import {useTheme} from "@mui/material/styles";
import useAuth from "../../hook/useAuth";

const IconList = ({ message }) => {

    const { setReplyMessage, setEditMessage, setReactMessage, setDeleteMessage } = useMessage();
    const [reactionAnchorEl, setReactionAnchorEl] = useState(null);
    const [menuAnchorEl, setMenuAnchorEl] = useState(null);
    const [chatListAnchorEl, setChatListAnchorEl] = useState(null);
    const [openWarningDialog, setOpenWarningDialog] = useState(false);
    const [openSnackbar, setOpenSnackbar] = useState(false);
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));
    const chatList = JSON.parse(localStorage.getItem('chatList')) || [];
    const theme = useTheme();
    const {authFetch} = useAuth()



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
        setReactMessage(new Object({
            messageId: message.messageId,
            accountId: localStorage.getItem('accountId'),
            reaction: reaction
        }));
        handleReactionClose();
    };


    const handleChatListOpen = (event) => {
        setChatListAnchorEl(event.currentTarget);
    }

    const handleChatListClose = () => {
        setChatListAnchorEl(null);
    }

    const handleEditMessage = () => {
        setEditMessage(message);
        handleMenuClose();
    }


    const handleDeleteMessage = () => {
        handleMenuClose();
        setOpenWarningDialog(true);
    }

    const handleConfirmDelete = () => {
        setDeleteMessage(message.messageId);
        setOpenWarningDialog(false);
        handleMenuClose();
    }

    const handleCancelDelete = () => {
        setOpenWarningDialog(false);
    }


    const handleCopyMessage = () => {
        navigator.clipboard.writeText(message.content).then(() => {
            setOpenSnackbar(true);
            handleMenuClose();
        });
    }

    const handleSnackbarClose = (event, reason) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpenSnackbar(false);
    };

    const handleFileDownload = async () => {
        try {
            const fileUrl = message.content;
            const response = await authFetch(`/api/file/download?fileUrl=${encodeURIComponent(fileUrl)}`);
            if (!response.ok) {
                throw new Error("Failed to download file");
            }
            response.headers.get("Content-Disposition");
            let filename = message.content.split('/').pop();
            filename = filename.includes('_')
                ? filename.split('_').pop()
                : filename;
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = filename;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        } catch (error) {
            console.error("Error downloading the file:", error);
        } finally {
            handleMenuClose();
        }
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
                        "&:hover": { color: theme.palette.primary.main },
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
                        "&:hover": { color: theme.palette.primary.main },
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
                    vertical: "top",
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
                    style={{backgroundColor: "transparent" }}
                    emojiContainerStyles={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fit, minmax(40px, 1fr))',
                        gap: '10px',
                    }}
                    renderEmoji={(emojiObject) => (
                        <div
                            key={emojiObject.unicode}
                            onClick={() => handleEmojiClick(emojiObject)}
                            style={{
                                borderRadius: '50%',
                                padding: '5px',
                                backgroundColor: message.reactions.some(reaction => reaction.reaction === emojiObject.emoji)
                                    ? 'rgba(255, 255, 0, 0.4)'
                                    : 'transparent',
                                cursor: 'pointer',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center',
                                transition: 'background-color 0.3s ease',
                            }}
                        >
                            <span style={{ fontSize: '24px' }}>{emojiObject.emoji}</span>
                        </div>
                    )}
                />
            </Popover>

            <Tooltip title="More">
                <IconButton
                    sx={{
                        cursor: "pointer",
                        "&:hover": { color: theme.palette.primary.main },
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
                    {isMine && message.type === 'TEXT' && (
                        <MenuItem onClick={handleEditMessage}>
                            <ListItemIcon> <EditNote /> </ListItemIcon>
                            <ListItemText> Edit </ListItemText>
                        </MenuItem>
                    )}

                    <MenuItem onClick={handleChatListOpen}>
                        <ListItemIcon> <Forward /> </ListItemIcon>
                        <ListItemText> Forward </ListItemText>
                    </MenuItem>
                    <Popover
                        open={Boolean(chatListAnchorEl)}
                        anchorEl={chatListAnchorEl}
                        onClose={handleChatListClose}
                        anchorOrigin={{
                            vertical: "top",
                            horizontal: !isMine ? "left" : "right",
                        }}
                        transformOrigin={{
                            vertical: "center",
                            horizontal: !isMine ? "left" : "right",
                        }}
                    >
                        <ChatList chatList={chatList} message={message} handleChatListClose={handleChatListClose} />
                    </Popover>

                    {message.type === 'TEXT' && (
                        <MenuItem onClick={handleCopyMessage}>
                            <ListItemIcon> <ContentCopy  /> </ListItemIcon>
                            <ListItemText> Copy all </ListItemText>
                        </MenuItem>

                    )}
                    {message.type !== 'TEXT' && (
                        <MenuItem onClick={handleFileDownload}>
                            <ListItemIcon> <Download /> </ListItemIcon>
                            <ListItemText> Download </ListItemText>
                        </MenuItem>
                    )}

                    {isMine && (
                        <MenuItem onClick={handleDeleteMessage}>
                            <ListItemIcon> <DeleteOutline  /> </ListItemIcon>
                            <ListItemText> Delete </ListItemText>
                        </MenuItem>
                    )}
                </MenuList>
            </Popover>

            <Snackbar
                open={openSnackbar}
                autoHideDuration={3000}
                onClose={handleSnackbarClose}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
            >
                <Alert onClose={handleSnackbarClose} severity="success" sx={{ width: '100%' }}>
                    Copied to clipboard!
                </Alert>
            </Snackbar>

            <Dialog
                open={openWarningDialog}
                keepMounted
                aria-describedby="alert-dialog-description"
                onClose={handleCancelDelete}
            >
                <DialogTitle>
                    <Alert severity="warning">
                        Warning
                    </Alert>
                </DialogTitle>
                <DialogContent>
                    <DialogContentText id="alert-dialog-description">
                         You can only restore deleted messages within one hour.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button
                        onClick={handleCancelDelete}
                        sx={{
                            backgroundColor: "transparent",
                            border: "1px solid",
                            borderRadius: 2,
                            textTransform: "none",
                            "&:hover": {
                                border: "1px solid",
                            },
                        }}
                    >
                        Cancel
                    </Button>
                    <Button
                        onClick={handleConfirmDelete}
                        sx={{
                            backgroundColor: "transparent",
                            border: "1px solid",
                            borderRadius: 2,
                            textTransform: "none",
                            "&:hover": {
                                border: "1px solid",
                            },
                        }}
                        autoFocus
                    >
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>

        </Stack>
    );
};

export default IconList;