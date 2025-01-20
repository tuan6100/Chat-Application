import { useEffect, useState } from "react";
import {Avatar, Box, IconButton, Stack, Tooltip, Typography} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import useMediaQuery from "@mui/material/useMediaQuery";
import useMessagese from "../../hook/useMessage";
import IconList from "../Menu/IconList";
import Reactions from "../Reactions";
import {ClockClockwise} from "phosphor-react"

const TextMessage = ({ message, scrollToMessage, highlightMessageId }) => {
    const theme = useTheme();
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));
    const [showDetails, setShowDetails] = useState(false);
    const isMobile = useMediaQuery('(max-width:600px)');
    const hasSeen = (message.viewerAvatars === undefined) || (message.viewerAvatars.length === 0);
    const isHighlighted = message.messageId === highlightMessageId;
    const [linkPreview, setLinkPreview] = useState(null);
    const { setRestoreMessage } = useMessagese();

    if (!isMine && message.status === 'sending') {
        return null;
    }


    const formatDate = (dateString) => {
        const date = new Date(dateString);
        const now = new Date();
        const isToday = date.toDateString() === now.toDateString();
        const options = { hour: 'numeric', minute: 'numeric', hour12: true };
        if (isToday) {
            return date.toLocaleTimeString('en-US', options);
        } else {
            const monthDay = date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }).toUpperCase();
            return `${monthDay} AT ${date.toLocaleTimeString('en-US', options)}`;
        }
    };


    const isLink = (text) => {
        const urlPattern = new RegExp('https?://[^\s/$.?#].[^\s]*', 'i');
        return urlPattern.test(text);
    };


    return (
        <Stack direction='row' spacing={3} justifyContent={!isMine ? 'start' : 'end'} sx={{ width: '100%' }}>
            <Stack
                direction='row'
                spacing={1}
                alignItems="flex-end"
                justifyContent="flex-end"
                sx={{ "&:hover .message-actions": { opacity: 1 } }}
            >
                {!isMine && <Avatar sx={{ width: 30, height: 30 }} src={message.senderAvatar} />}

                {message.status === 'deleted' ? (
                    <>
                        {isMine && (
                            <Tooltip  title={"Restore"} >
                                <IconButton color="primary" onClick={() => {setRestoreMessage(message.messageId)}}>
                                    <ClockClockwise  />
                                </IconButton>
                            </Tooltip>
                        )}
                        <Box
                            sx={{
                                border: '1px dashed',
                                borderColor: 'rgba(51,45,45,0.5)',
                                backgroundColor: 'transparent',
                                padding: '8px',
                                borderRadius: '8px',
                            }}
                        >
                            <Typography variant="body2" color="text.secondary">
                                {isMine ? 'You' : message.senderUsername} deleted this message
                            </Typography>
                        </Box>
                    </>

                ) : (
                    <>
                        {isMine && <IconList message={message} />}
                        <Stack direction='column'  alignItems={!isMine ? 'flex-start' : 'flex-end'}>
                            {showDetails && (
                                <Typography variant='caption' color={theme.palette.text.secondary} sx={{ mb: 0.5 }}>
                                    {formatDate(message.sentTime)}
                                </Typography>
                            )}

                            {message.type === 'TEXT_FORWARDED' && (
                                <Typography variant='caption' color={theme.palette.text.secondary} sx={{ mb: 0.5 }}>
                                    {isMine ? 'You' : message.senderUsername} forwarded a message:
                                </Typography>
                            )}

                            {message.replyToMessageId && message.replyToMessageContent && (
                                <Box
                                    onClick={() => scrollToMessage(message.replyToMessageId)}
                                    sx={{
                                        position: "relative",
                                        width: 'fit-content',
                                        backgroundColor: "rgba(45,43,43,0.9)",
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

                            <Box
                                p={1}
                                sx={{
                                    backgroundColor: !isMine ? '#424242' : theme.palette.primary.main,
                                    borderRadius: "15px",
                                    width: 'fit-content',
                                    marginTop: '-7px',
                                    zIndex: 2,
                                    transform: isHighlighted ? 'scale(1.2)' : 'none',
                                    transition: 'transform 0.5s ease-in-out',
                                    position: 'relative'
                                }}
                            >
                                <Typography
                                    variant='body2'
                                    color={!isMine ? theme.palette.text : '#fff'}
                                    sx={{
                                        display: 'inline-block',
                                        whiteSpace: 'pre-wrap',
                                        wordBreak: 'break-all',
                                        maxWidth: '50vw',
                                        left: showDetails ? 0 : 'auto',
                                        textDecoration: isLink(message.content) ? 'underline' : 'none',
                                    }}
                                    onClick={() => setShowDetails(!showDetails)}
                                >
                                    {isLink(message.content) ? (
                                        <a href={message.content} target="_blank" rel="noopener noreferrer" style={{ color: 'inherit', textDecoration: 'inherit' }}>
                                            {message.content}
                                        </a>
                                    ) : (
                                        message.content
                                    )}
                                </Typography>
                                {message.reactions && message.reactions.length > 0 && (
                                    <Reactions reactions={message.reactions} />
                                )}
                            </Box>

                            {showDetails && !hasSeen && (
                                <Stack
                                    direction='row'
                                    spacing={1}
                                    sx={{ mt: 0.5, animation: 'toggle-in 0.5s ease-in-out' }}
                                >
                                    {message.viewerAvatars.map((avatar, index) => (
                                        <Avatar key={index} sx={{ width: 20, height: 20 }} src={avatar} />
                                    ))}
                                </Stack>
                            )}
                            {showDetails && hasSeen && isMine && (
                                <Typography
                                    variant='caption'
                                    color={theme.palette.text.secondary}
                                    sx={{ mt: 0.5, animation: 'toggle-in 0.5s ease-in-out' }}
                                >
                                    {message.status || 'sent'}
                                </Typography>
                            )}
                        </Stack>
                        {!isMine && <IconList message={message} />}
                    </>
                )}
            </Stack>
        </Stack>
    );
}

export default TextMessage;