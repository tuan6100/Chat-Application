import { yupResolver } from '@hookform/resolvers/yup';
import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import * as Yup from 'yup';
import FormProvider from '../../component/hook-form/FormProvider';
import { Alert, Button, IconButton, InputAdornment, Stack } from '@mui/material';
import HookTextField from '../../component/hook-form/HookTextField';
import { Eye, EyeSlash } from 'phosphor-react';

const RegisterForm = () => {
    const [showPassword, setShowPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const registerSchema = Yup.object().shape({
        firstName: Yup.string().required('First Name is required'),
        lastName: Yup.string().required('Last Name is required'),
        email: Yup.string().required('Email is required').email('Email must be a valid email address'),
        password: Yup.string().required('Password is required'),
    });

    const defaultValues = {
        firstName: '',
        lastName: '',
        email: 'dulanjali@gmail.com',
        password: 'dula@123',
    };

    const methods = useForm({
        resolver: yupResolver(registerSchema),
        defaultValues,
    });

    const {
        reset,
        setError,
        handleSubmit,
        formState: { errors, isSubmitting, isSubmitSuccessful },
    } = methods;

    const onSubmit = async (data) => {
        try {
            const response = await fetch("http://localhost:8000/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
            });

            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || "Registration failed");
                setError("afterSubmit", {
                    type: "manual",
                    message: errorData.message || "Registration failed",
                });
                return;
            }

            const { message, headers } = await response.json();

            // Lưu access token và refresh token vào localStorage
            const accessToken = headers["authorization"]?.split(" ")[1];
            const refreshToken = headers["x-refresh-token"]?.split(" ")[1];

            if (accessToken && refreshToken) {
                localStorage.setItem("accessToken", accessToken);
                localStorage.setItem("refreshToken", refreshToken);
            }

            // Reset form and redirect to the app page
            reset();
            window.location.href = "/app"; // Redirect to the app/dashboard page after successful registration
        } catch (error) {
            console.error("Error during registration:", error);
            setErrorMessage(error.message);
            setError("afterSubmit", {
                type: "manual",
                message: error.message,
            });
        }
    };

    return (
        <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3}>
                {!!errorMessage && <Alert severity="error">{errorMessage}</Alert>}
                <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
                    <HookTextField name="userName" label="User Name" />
                </Stack>
                <HookTextField name="email" label="Email address" />
                <HookTextField
                    name="password"
                    label="Password"
                    type={showPassword ? 'text' : 'password'}
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
                <Button
                    fullWidth
                    color="inherit"
                    size="large"
                    type="submit"
                    variant="contained"
                    sx={{
                        bgcolor: 'text.primary',
                        color: (theme) =>
                            theme.palette.mode === 'light' ? 'common.white' : 'grey.800',
                        '&:hover': {
                            bgcolor: 'text.primary',
                            color: (theme) => (theme.palette.mode === 'light' ? 'common.white' : 'grey.800'),
                        },
                    }}
                    disabled={isSubmitting}
                >
                    {isSubmitting ? 'Creating Account...' : 'Create Account'}
                </Button>
            </Stack>
        </FormProvider>
    );
};

export default RegisterForm;
