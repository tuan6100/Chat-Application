import { useEffect, useState } from "react";
import {Avatar, Box, IconButton, Stack, Typography} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import {InsertDriveFile} from "@mui/icons-material";
import useMediaQuery from "@mui/material/useMediaQuery";
import IconList from "../Menu/IconList";
import Reactions from "../Reactions";
import {formatDate} from "./TextMessage";

const FileMessage = ({ message, scrollToMessage, highlightMessageId }) => {

    const theme = useTheme();
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));
    const [showDetails, setShowDetails] = useState(false);
    const isMobile = useMediaQuery('(max-width:600px)');
    const hasSeen = (message.viewerAvatars === undefined) || (message.viewerAvatars.length === 0);
    const isHighlighted = message.messageId === highlightMessageId;
    const [fileMetadata, setFileMetadata] = useState({ filename: "", size: 0 });


    useEffect(() => {
        const fetchFileMetadata = async () => {
            try {
                const filename = message.content.split('/').pop();
                const originalFilename = filename.includes('_')
                    ? filename.split('_').pop()
                    : filename;
                const response = await fetch(message.content, { method: 'HEAD' });
                const size = response.headers.get('content-length');
                setFileMetadata({ filename: originalFilename, size });
            } catch (error) {
                console.error("Error fetching file metadata:", error);
            }
        };
        fetchFileMetadata();
    }, [message.content]);


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

                    <Box
                        p={1}
                        sx={{
                            backgroundColor: '#424242',
                            borderRadius: '15px',
                            width: '200px',
                            marginTop: '-7px',
                            zIndex: 2,
                            transform: isHighlighted ? 'scale(1.2)' : 'none',
                            transition: 'transform 0.5s ease-in-out',
                            position: 'relative'
                        }}
                    >
                       <Stack direction='row'
                              justifyContent='flex-start'
                              spacing={2}
                              sx={{cursor: 'pointer'}}
                              onClick={() => window.open(message.content, '_blank')}
                       >
                           <InsertDriveFile />
                           <Stack
                               direction='column'
                               justifyContent={'center'}
                               alignItems={'center'}
                               sx={{ cursor: 'pointer' }}
                           >
                               <Typography variant='caption' color={theme.palette.text.primary}>
                                   {fileMetadata.filename}
                               </Typography>
                               <Typography variant='caption' color={theme.palette.text.secondary}>
                                   {fileMetadata.size && `${(fileMetadata.size / 1024).toFixed(2)} KB`}
                               </Typography>
                           </Stack>
                       </Stack>
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
export default FileMessage;
