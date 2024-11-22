import React from "react";
import { Box, Typography, Container, Paper } from "@mui/material";
import LoginForm from "../../section/auth/LoginForm";

const LoginWithEmail = () => {
    return (
        <Container maxWidth="sm" sx={{ mt: 8 }}>
            <Paper elevation={3} sx={{ padding: 4 }}>
                <Box textAlign="center" mb={3}>
                    <Typography variant="h4" gutterBottom>
                        Login
                    </Typography>
                    <Typography variant="body1" color="text.secondary">
                        Please login to your account
                    </Typography>
                </Box>
                <LoginForm />
            </Paper>
        </Container>
    );
};

export default LoginWithEmail;
