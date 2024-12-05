import React, { useEffect, useState } from "react";
import {Link as RouterLink, useNavigate} from "react-router";
import {Stack, Button, Typography, Alert, Avatar, Link} from "@mui/material";
import {CaretLeft, CaretRight} from "phosphor-react";

const ValidateUsername = () => {
    const [username, setUsername] = useState("");
    const [avatar, setAvatar] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [showRedirectLink, setShowRedirectLink] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const savedUsername = localStorage.getItem("username");
        const savedAvatar = localStorage.getItem("avatar");
        if (savedUsername) {
            setUsername(savedUsername);
            setAvatar(savedAvatar || "");
        } else {
            setErrorMessage("Username not found. Please validate your email again.");
            setShowRedirectLink(true)
        }
    }, []);

    const handleConfirm = () => {
        navigate("/auth/renew-password");
    };

    const handleReject = () => {
        localStorage.removeItem("username");
        localStorage.removeItem("avatar");
        localStorage.removeItem("email");
        navigate("/auth/register");
    };

    return (
        <Stack spacing={3} sx={{ maxWidth: 400, margin: "0 auto", mt: 5, textAlign: "center" }}>
            <Typography variant="h4">Is this your account?</Typography>
            {errorMessage ? (
                <Alert severity="error">{errorMessage}</Alert>
            ) : (
                <>
                    <Stack direction="row" alignItems="center" justifyContent="center" spacing={2} sx={{ mt: 2 }}>
                        <Avatar
                            alt={username}
                            src={avatar}
                            sx={{ width: 56, height: 56, border: "2px solid #ddd" }}
                        />
                        <Typography variant="h5">{username}</Typography>
                    </Stack>
                    <Stack direction="row" spacing={2} justifyContent="center" sx={{ mt: 4 }}>
                        <Button variant="contained" color="primary" onClick={handleConfirm}>
                            Yes, it's me
                        </Button>
                        <Button variant="outlined" color="secondary" onClick={handleReject}>
                            No, I don't have this account
                        </Button>
                    </Stack>
                </>
            )}
            {showRedirectLink && (
                <Link
                    component={RouterLink}
                    to='/auth/validate-email'
                    color='inherit'
                    variant='subtitle1'
                    sx={{
                        mt:3,
                        color: 'text.primary',
                        alignItems: 'center',
                        display: 'inline-flex',
                        textDecoration: 'none',
                    }}
                >
                    <CaretLeft />
                    Return to email validation page
                </Link>
            )}
        </Stack>
    );
};

export default ValidateUsername;
