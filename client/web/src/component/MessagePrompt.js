import React from 'react';
import { Box, Typography } from '@mui/material';
import { useTheme } from '@mui/material/styles';

const MessagePrompt = () => {
    const theme = useTheme();

    return (
        <Box sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height:  '100vh',
            width: '100%',
            textAlign: 'center',
            color: theme.palette.text.secondary,
            position: 'fixed',
            left:200,
            zIndex: 2,
        }}>
            <Typography variant="h6" sx={{ px: 2 }}>
                Select a chat to start messaging
            </Typography>
        </Box>
    );
};

export default MessagePrompt;