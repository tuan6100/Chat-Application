import React, { useEffect, useState } from "react";
import {Link as RouterLink, useNavigate} from "react-router";
import {Stack, Button, Typography, Alert, Avatar, Link, Box} from "@mui/material";
import {CaretLeft} from "phosphor-react";
import ClearIcon from '@mui/icons-material/Clear';
import CheckIcon from '@mui/icons-material/Check';
import ScreenLoading from "../../component/ScreenLoading";

const ValidateUsername = () => {
    const [username, setUsername] = useState("");
    const [avatar, setAvatar] = useState("");
    const [errorMessage, setErrorMessage] = useState("");
    const [showRedirectLink, setShowRedirectLink] = useState(false);
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

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
        setLoading(true);
        setTimeout(() => {
            setLoading(false);
            navigate("/auth/register");
        }, 3000);
    }

    return (
        <Stack
            spacing={5}
            sx={{
                mt: 5,
                textAlign: "center",
                alignItems: "center",
                justifyContent: "center",
                width: "100%",
            }}
        >
            <Typography variant="h4" sx={{ textAlign: "center" }}>
                Is this your account?
            </Typography>
            {errorMessage ? (
                <Alert severity="error">{errorMessage}</Alert>
            ) : (
                <>
                    <Stack
                        direction="column"
                        alignItems="center"
                        justifyContent="center"
                        spacing={2}
                        sx={{ mt: 2 }}
                    >
                        <Box
                            sx={{
                                border: "2px solid #ddd",
                                borderRadius: 2,
                                padding: 2,
                                width: 150,
                                height: 150,
                                display: "flex",
                                flexDirection: "column",
                                alignItems: "center",
                            }}
                        >
                            <Typography variant="h5" sx={{ textAlign: "center", mb: 2 }}>
                                {username}
                            </Typography>
                            <Avatar
                                alt={username}
                                src={avatar}
                                sx={{
                                    width: 75,
                                    height: 75,
                                    border: "2px solid #ddd",
                                }}
                            />
                        </Box>
                    </Stack>

                    <Stack
                        direction={{ xs: "column", sm: "row" }}
                        justifyContent="center"
                        alignItems="center"
                        spacing={2}
                        sx={{
                            mt: 5,
                            width: "100%",
                        }}
                    >
                        <Button
                            onClick={handleConfirm}
                            sx={{
                                backgroundColor: "transparent",
                                color: "green",
                                border: "1px solid green",
                                borderRadius: 2,
                                textTransform: "none",
                                width: { xs: "40%", sm: "250px" },
                                height: "50px",
                                "&:hover": {
                                    backgroundColor: "rgba(0, 255, 0, 0.1)",
                                },
                            }}
                        >
                            <CheckIcon sx={{ mr: 1, color: "green" }} /> Yes, it's me
                        </Button>
                        <Button
                            onClick={handleReject}
                            sx={{
                                backgroundColor: "transparent",
                                color: "red",
                                border: "1px solid red",
                                borderRadius: 2,
                                textTransform: "none",
                                width: { xs: "40%", sm: "250px" },
                                height: "50px",
                                "&:hover": {
                                    backgroundColor: "rgba(255, 0, 0, 0.1)",
                                },
                            }}
                        >
                            <ClearIcon sx={{ mr: 2, color: "red" }} /> No, this account isn't mine
                        </Button>
                    </Stack>
                </>
            )}
            {loading && (
                    <Alert severity="info">
                        You have not registered an account yet. Please create your new one.
                    </Alert>
                ) &&
                <ScreenLoading />
            }

            {showRedirectLink && (
                <Link
                    component={RouterLink}
                    to="/auth/validate-email"
                    color="inherit"
                    variant="subtitle1"
                    sx={{
                        mt: 3,
                        color: "text.primary",
                        alignItems: "center",
                        display: "inline-flex",
                        textDecoration: "none",
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
