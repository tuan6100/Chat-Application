import { useState } from "react";
import { Avatar, Box, Stack, Typography } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import useMediaQuery from "@mui/material/useMediaQuery";


const TextMessage = ({ messageKey, message }) => {
    const theme = useTheme();
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));
    const [showDetails, setShowDetails] = useState(false);
    const isMobile = useMediaQuery('(max-width:600px)');
    const hasSeen = (message.viewerAvatars === undefined) || (message.viewerAvatars.length === 0);


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

    if (!isMine && message.status === 'sending') {
        return null;
    }

    return (
        <Stack direction='row' justifyContent={!isMine ? 'start' : 'end'} sx={{ width: '100%' }} key={messageKey}>
            <Stack direction='row' spacing={1} alignItems="flex-end">
                {!isMine && <Avatar sx={{ width: 30, height: 30 }} src={message.senderAvatar} />}
                <Stack>
                    {showDetails && (
                        <Typography variant='caption' color={theme.palette.text.secondary} sx={{ mb: 0.5 }}>
                            {formatDate(message.sentTime)}
                        </Typography>
                    )}

                    <Box p={1} sx={{
                        backgroundColor: !isMine ? '#424242' : theme.palette.primary.main,
                        borderRadius: 1.5,
                        width: 'fit-content',
                    }}>
                        <Typography
                            variant='body2'
                            color={!isMine ? theme.palette.text : '#fff'}
                            sx={{
                                display: 'inline-block',
                                whiteSpace: 'pre-wrap',
                                wordBreak: 'break-all',
                                maxWidth: '50vw',
                            }}
                            onClick={() => setShowDetails(!showDetails)}
                        >
                            {message.content}
                        </Typography>
                    </Box>

                    {showDetails && !hasSeen &&  (
                        <Stack direction='row' spacing={1} sx={{ mt: 0.5 }}>
                            {message.viewerAvatars.map((avatar, index) => (
                                <Avatar key={index} sx={{ width: 20, height: 20 }} src={avatar} />
                            ))}
                        </Stack>
                    )}
                    {showDetails && hasSeen && isMine && (
                        <Typography variant='caption' color={theme.palette.text.secondary} sx={{ mt: 0.5 }}>
                            {message.status || 'sent'}
                        </Typography>
                    )}
                </Stack>
            </Stack>
        </Stack>
    );
}

export default TextMessage;