import React, { useContext, useState } from "react";
import * as Yup from "yup";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { Link } from "react-router-dom";
import AuthContext from "../../context/AuthContext";
import FormProvider from "../../component/hook-form/FormProvider";
import { TextField, Alert, Button, IconButton, InputAdornment, Stack, Tooltip } from "@mui/material";
import { RiEyeCloseLine, RiEye2Fill } from "react-icons/ri";

const LoginForm = () => {
    const { setIsAuthenticated } = useContext(AuthContext);

    const [showPassword, setShowPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const loginSchema = Yup.object().shape({
        email: Yup.string().required("Email is required").email("Invalid email format"),
        password: Yup.string().required("Password is required"),
    });

    const methods = useForm({
        resolver: yupResolver(loginSchema),
        defaultValues: {
            email: "",
            password: "",
        },
    });

    const { register, setError, handleSubmit, formState: { errors, isSubmitting } } = methods;

    const onSubmit = async (data) => {
        try {
            const response = await fetch("http://localhost:8000/api/auth/login/email", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
                credentials: "include",
            });

            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || "Login failed");
                if (errorData.message) {
                    setError("email", { type: "manual", message: errorData.message });
                    setError("password", { type: "manual", message: errorData.message });
                }
                return;
            }

            const authHeader = response.headers.get("Authorization");
            const refreshTokenHeader = response.headers.get("X-Refresh-Token");

            if (authHeader && refreshTokenHeader) {
                const accessToken = authHeader.split(" ")[1];
                const refreshToken = refreshTokenHeader.split(" ")[1];
                localStorage.setItem("accessToken", accessToken);
                localStorage.setItem("refreshToken", refreshToken);
                setIsAuthenticated(true);
                setErrorMessage("");
                window.location.href = "/app";
            } else {
                setErrorMessage("Failed to retrieve tokens.");
            }
        } catch (error) {
            console.error("Error during login:", error);
            setErrorMessage(error.message || "Login failed");
        }
    };

    return (
        <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3}>
                {(errorMessage || errors.email || errors.password) && (
                    <Alert severity="error">
                        {errorMessage || errors.email?.message || errors.password?.message}
                    </Alert>
                )}

                <TextField
                    {...register("email")}
                    label="Email"
                    error={!!errors.email}
                    helperText={errors.email?.message}
                />

                <TextField
                    {...register("password")}
                    label="Password"
                    type={showPassword ? "text" : "password"}
                    error={!!errors.password}
                    helperText={errors.password?.message}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <Tooltip title={showPassword ? "Hide password" : "Show password"}>
                                    <IconButton
                                        aria-label={showPassword ? "hide the password" : "display the password"}
                                        onClick={() => setShowPassword(!showPassword)}
                                    >
                                        {showPassword ? <RiEye2Fill /> : <RiEyeCloseLine />}
                                    </IconButton>
                                </Tooltip>
                            </InputAdornment>
                        ),
                    }}
                />
            </Stack>

            <Stack alignItems="flex-end" sx={{ my: 2 }}>
                <Link to="/auth/reset-password" style={{ textDecoration: "none", color: "aquamarine" }}>
                    Forgot Password?
                </Link>
            </Stack>

            <Button
                fullWidth
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
