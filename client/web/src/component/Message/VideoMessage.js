import { useEffect, useState } from "react";
import {Avatar, Box, IconButton, Stack, Typography, Card, CardContent, CardMedia} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import {Play, Pause} from 'phosphor-react';
import useMediaQuery from "@mui/material/useMediaQuery";
import IconList from "../Menu/IconList";
import Reactions from "../Reactions";
import {formatDate} from "./TextMessage";

const VideoMessage = ({ message, scrollToMessage, highlightMessageId }) => {

    const theme = useTheme();
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));
    const [showDetails, setShowDetails] = useState(false);
    const isMobile = useMediaQuery('(max-width:600px)');
    const hasSeen = (message.viewerAvatars === undefined) || (message.viewerAvatars.length === 0);
    const isHighlighted = message.messageId === highlightMessageId;

    return (
        <Stack direction='row' spacing={3} justifyContent={!isMine ? 'start' : 'end'} sx={{width: '100%'}}>
            <Stack
                direction='row'
                spacing={1}
                alignItems="flex-end"
                justifyContent="flex-end"
                sx={{"&:hover .message-actions": {opacity: 1}}}
            >
                {!isMine && <Avatar sx={{width: 30, height: 30}} src={message.senderAvatar}/>}

                {isMine && <IconList message={message}/>}
                <Stack direction='column' alignItems={!isMine ? 'flex-start' : 'flex-end'}>
                    {showDetails && (
                        <Typography variant='caption' color={theme.palette.text.secondary} sx={{mb: 0.5}}>
                            {formatDate(message.sentTime)}
                        </Typography>
                    )}

                    {message.type === 'FILE_FORWARDED' && (
                        <Typography variant='caption' color={theme.palette.text.secondary} sx={{mb: 0.5}}>
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

                    <Card raised={true} sx={{ width: 200, backgroundColor: '#424242', borderRadius: '15px', marginTop: '-7px', zIndex: 2, transform: isHighlighted ? 'scale(1.2)' : 'none', transition: 'transform 0.5s ease-in-out', position: 'relative' }}>
                        <CardMedia
                            component="video"
                            controls
                            src={message.content}
                            sx={{ width: '100%', height: '100%' }}
                        />
                    </Card>
                    {message.reactions && message.reactions.length > 0 && (
                        <Reactions reactions={message.reactions} />
                    )}

                    {showDetails && !hasSeen && (
                        <Stack
                            direction='row'
                            spacing={1}
                            sx={{mt: 0.5, animation: 'toggle-in 0.5s ease-in-out'}}
                        >
                            {message.viewerAvatars.map((avatar, index) => (
                                <Avatar key={index} sx={{width: 20, height: 20}} src={avatar}/>
                            ))}
                        </Stack>
                    )}
                    {showDetails && hasSeen && isMine && (
                        <Typography
                            variant='caption'
                            color={theme.palette.text.secondary}
                            sx={{mt: 0.5, animation: 'toggle-in 0.5s ease-in-out'}}
                        >
                            {message.status || 'sent'}
                        </Typography>
                    )}
                </Stack>
                {!isMine && <IconList message={message}/>}
            </Stack>
        </Stack>
    );
}
export default VideoMessage;
