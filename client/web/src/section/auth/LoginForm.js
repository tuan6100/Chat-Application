import React, {useContext, useState} from "react";
import * as Yup from "yup";
import { useForm } from "react-hook-form";
import FormProvider from "../../component/hook-form/FormProvider";
import { yupResolver } from "@hookform/resolvers/yup";
import { Alert, Button, IconButton, InputAdornment, Link, Stack } from "@mui/material";
import TextField from "../../component/hook-form/HookTextField";
import { RiEyeCloseLine, RiEye2Fill } from "react-icons/ri";
// import { Link as RouterLink } from "react-router-dom";
import AuthContext  from "../../context/AuthContext";

const LoginForm = () => {
    const { setIsAuthenticated } = useContext(AuthContext);

    const [showPassword, setShowPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const loginSchema = Yup.object().shape({
        username: Yup.string().required("Username is required"),
        password: Yup.string().required("Password is required"),
    });

    const methods = useForm({
        resolver: yupResolver(loginSchema),
        defaultValues: {
            username: "",
            password: "",
        },
    });

    const { setError, handleSubmit, formState: { errors, isSubmitting } } = methods;

    const onSubmit = async (data) => {
        try {
            const response = await fetch("http://localhost:8000/api/auth/login/username", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data),
                credentials: 'include'
            });
            if (!response.ok) {
                const errorData = await response.json();
                console.log("Error data:", errorData);
                setErrorMessage(errorData.message || "Login failed");
                setError("username", {
                    type: "manual",
                    message: errorData.message || "Username or password incorrect",
                });
                setError("password", {
                    type: "manual",
                    message: errorData.message || "Username or password incorrect",
                });
                return;
            }
            console.log(response.headers.get("Authorization"));
            console.log(response.headers.get("X-Refresh-Token"));
            console.log(response.headers.get("Content-Type"));
            // const authHeader = response.headers.get("Authorization");
            // const accessToken = authHeader.substring(7, authHeader.length);
            // const refreshTokenHeader = response.headers.get("X-Refresh-Token");
            // const refreshToken = refreshTokenHeader.substring(7, refreshTokenHeader.length);
            //
            // if (accessToken && refreshToken) {
            //     localStorage.setItem("accessToken", accessToken);
            //     localStorage.setItem("refreshToken", refreshToken);
            //     console.log("Tokens saved:", { accessToken, refreshToken });
            //     setIsAuthenticated(true);
            //     setErrorMessage("");
            //     window.location.href = "/app";
            // } else {
            //     setErrorMessage("Failed to retrieve tokens.");
            // }
        } catch (error) {
            console.error("Error during login:", error);
            setErrorMessage(error.message || "Login failed");
            setError("afterSubmit", {
                type: "manual",
                message: error.message,
            });
        }
    };

    return (
        <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3}>
                {(errorMessage || errors.afterSubmit) && (
                    <Alert severity="error">{errorMessage || errors.afterSubmit.message}</Alert>
                )}

                <TextField
                    name="username"
                    label="Username"
                    error={!!errors.username}
                    helperText={errors.username?.message}
                />

                <TextField
                    name="password"
                    label="Password"
                    type={showPassword ? "text" : "password"}
                    error={!!errors.password}
                    helperText={errors.password?.message}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton
                                    aria-label="Show password"
                                    onClick={() => setShowPassword(!showPassword)}
                                >
                                    {showPassword ? <RiEye2Fill /> : <RiEyeCloseLine />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                />
            </Stack>

            <Stack alignItems={"flex-end"} sx={{ my: 2 }}>
                <Link to="/auth/reset-password">Forgot Password?</Link>
            </Stack>

            <Button
                fullWidth
                color="inherit"
                size="large"
                type="submit"
                variant="contained"
                disabled={isSubmitting}
            >
                {isSubmitting ? "Logging in..." : "Login"}
            </Button>
        </FormProvider>
    );
};

export default LoginForm;