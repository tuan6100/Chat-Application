import {Avatar, Box, IconButton, Stack, Tooltip, Typography, CardMedia, Popover} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import {useState} from "react";
import useMediaQuery from "@mui/material/useMediaQuery";
import IconList from "../Menu/IconList";
import Reactions from "../Reactions";
import {formatDate} from "./TextMessage";

const ImageMessage = ({ message, scrollToMessage, highlightMessageId }) => {

    const theme = useTheme();
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));
    const [showDetails, setShowDetails] = useState(false);
    const isMobile = useMediaQuery('(max-width:600px)');
    const hasSeen = (message.viewerAvatars === undefined) || (message.viewerAvatars.length === 0);
    const isHighlighted = message.messageId === highlightMessageId;
    const [anchorEl, setAnchorEl] = useState(null);

    const handleImageClick = (event) => {
        setAnchorEl(anchorEl ? null : event.currentTarget);
    };

    const open = Boolean(anchorEl);
    const id = open ? 'zoomed-image-popover' : undefined;

    if (!isMine && message.status === 'sending') {
        return null;
    }


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

                    {message.type === 'IMAGE_FORWARDED' && (
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
                            <CardMedia
                                component="img"
                                image={message.replyToMessageContent}
                                alt="Image"
                                sx={{
                                    width: 300,
                                    maxWidth: '100%',
                                    borderRadius: "15px",
                                    cursor: 'pointer'
                                }}
                            />
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
                        <CardMedia
                            component="img"
                            image={message.content}
                            alt="Image"
                            sx={{
                                width: 300,
                                maxWidth: '100%',
                                borderRadius: "15px",
                                cursor: 'pointer'
                            }}
                            onClick={handleImageClick}
                        />
                        <Popover
                            id={id}
                            open={open}
                            anchorEl={anchorEl}
                            onClose={handleImageClick}
                            anchorOrigin={{
                                vertical: 'center',
                                horizontal: 'center',
                            }}
                            transformOrigin={{
                                vertical: 'center',
                                horizontal: 'center',
                            }}
                        >
                            <CardMedia
                                component="img"
                                image={message.content}
                                alt="Zoomed Image"
                                sx={{
                                    width: 900,
                                    maxWidth: '100%',
                                    borderRadius: "15px"
                                }}
                            />
                        </Popover>
                        {message.reactions && message.reactions.length > 0 && (
                            <Reactions reactions={message.reactions}/>
                        )}
                    </Box>

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

export default ImageMessage;
