import React, { useState, useEffect } from 'react';
import { Container, Stack, Typography } from '@mui/material';
import {Outlet, useLocation} from 'react-router';
import '../../css/TypingEffect.css';

const MainLayout = () => {
    const [isTypingComplete, setIsTypingComplete] = useState(false);
    const location = useLocation();

    useEffect(() => {
        const timer = setTimeout(() => {
            setIsTypingComplete(true);
        }, 4000);

        return () => clearTimeout(timer);
    }, [location]);

    return (
        <Container
            sx={{
                mt: 2,
                maxWidth: 'sm',
                height: '100vh',
                backgroundColor: 'transparent',
                boxShadow: 'none',
                border: 'none',
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
            }}
        >
            <Stack spacing={5} alignItems="top" sx={{ mt: 1 }}>
                <Stack direction="column" alignItems="center">
                    <img
                        style={{ height: 80, width: 80, marginTop: '-200px' }}
                        src="/Chat-Application/logo192.png"
                        alt="App Logo"
                    />
                    <Typography
                        variant="h5"
                        sx={{
                            mt: 2,
                            textAlign: 'center',
                            fontWeight: 'bold',
                            fontSize: '1.5rem',
                        }}
                    >
                        <span className={`typing-text ${isTypingComplete ? 'typing-complete' : ''}`}>
                            Opensource Messaging App For Everyone
                        </span>
                    </Typography>
                </Stack>
                <Outlet />
            </Stack>
        </Container>
    );
};

export default MainLayout;