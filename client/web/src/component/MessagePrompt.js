import React from 'react';
import { Box, Typography, useMediaQuery } from '@mui/material';
import { useTheme } from '@mui/material/styles';

const MessagePrompt = () => {
    const theme = useTheme();
    const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

    return (
        <Box sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: isMobile ? 'calc(100vh - 120px)' : '100vh',
            width: '100%',
            textAlign: 'center',
            color: theme.palette.text.secondary,
            position: 'fixed',
            left: isMobile ? 0 : 200,
            zIndex: 0,
        }}>
            <Typography variant={isMobile ? "h6" : "h4"} sx={{ px: 2 }}>
                Select a chat to start messaging
            </Typography>
        </Box>
    );
};

export default MessagePrompt;