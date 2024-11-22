import React, { useState } from "react";
import * as Yup from "yup";
import { useForm } from "react-hook-form";
import FormProvider from "../../component/hook-form/FormProvider";
import { yupResolver } from "@hookform/resolvers/yup";
import { Alert, Button, IconButton, InputAdornment, Link, Stack } from "@mui/material";
import TextField from "../../component/hook-form/HookTextField";
import { Eye, EyeSlash } from "phosphor-react";
import { Link as RouterLink } from "react-router-dom";

const LoginForm = () => {
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

    const {
        reset,
        setError,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = methods;

    const onSubmit = async (data) => {
        try {
            const response = await fetch("http://localhost:8000/api/auth/login/username", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            });

            if (!response.ok) {
                const errorData = await response.json();
                console.log("Error data:", errorData);
                // Cập nhật thông báo lỗi từ backend
                setErrorMessage(errorData.message || "Login failed");
                // Không reset form, chỉ set lỗi cho trường cụ thể nếu cần
                setError("username", {
                    type: "manual",
                    message: errorData.message || "Username or password incorrect",
                });
                setError("password", {
                    type: "manual",
                    message: errorData.message || "Username or password incorrect",
                });
                throw new Error(errorData.message || "Login failed");
            }

            const result = await response.json();
            console.log("Login successful:", result);
            setErrorMessage("");  // Reset thông báo lỗi khi đăng nhập thành công
        } catch (error) {
            console.error("Error during login:", error);
            setErrorMessage(error.message);  // Hiển thị lỗi cho người dùng
            reset();  // Không reset form, chỉ reset lỗi
            setError("afterSubmit", {
                type: "manual",
                message: error.message,
            });
        }
    };

    return (
        <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3}>
                {/* Hiển thị thông báo lỗi nếu có */}
                {(errorMessage || errors.afterSubmit) && (
                    <Alert severity="error">{errorMessage || errors.afterSubmit.message}</Alert>
                )}

                {/* Tạo trường nhập Username */}
                <TextField
                    name="username"
                    label="Username"
                    error={!!errors.username}
                    helperText={errors.username?.message}
                />

                {/* Tạo trường nhập Password */}
                <TextField
                    name="password"
                    label="Password"
                    type={showPassword ? "text" : "password"}
                    error={!!errors.password}
                    helperText={errors.password?.message}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton onClick={() => setShowPassword(!showPassword)}>
                                    {showPassword ? <Eye /> : <EyeSlash />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                />
            </Stack>

            {/* Link quên mật khẩu */}
            <Stack alignItems={"flex-end"} sx={{ my: 2 }}>
                <Link
                    component={RouterLink}
                    to="/auth/reset-password"
                    variant="body2"
                    color="inherit"
                    underline="always"
                >
                    Forgot Password?
                </Link>
            </Stack>

            {/* Nút đăng nhập */}
            <Button
                fullWidth
                color="inherit"
                size="large"
                type="submit"
                variant="contained"
                sx={{
                    bgcolor: "text.primary",
                    color: (theme) => (theme.palette.mode === "light" ? "common.white" : "grey.800"),
                    "&:hover": {
                        bgcolor: "text.primary",
                        color: (theme) => (theme.palette.mode === "light" ? "common.white" : "grey.800"),
                    },
                }}
                disabled={isSubmitting}
            >
                {isSubmitting ? "Logging in..." : "Login"}
            </Button>
        </FormProvider>
    );
};

export default LoginForm;
