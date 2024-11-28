import { yupResolver } from '@hookform/resolvers/yup';
import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import * as Yup from 'yup';
import FormProvider from '../../component/hook-form/FormProvider';
import { Alert, Button, IconButton, InputAdornment, Stack } from '@mui/material';
import HookTextField from '../../component/hook-form/HookTextField';
import { RiEyeCloseLine, RiEye2Fill } from "react-icons/ri";

const RegisterForm = () => {
    const [showPassword, setShowPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const registerSchema = Yup.object().shape({
        username: Yup.string().required('Username is required'),
        email: Yup.string().required('Email is required').email('Email must be a valid email address'),
        password: Yup.string()
            .required('Password is required')
            .min(6, 'Password must be at least 6 characters long'),
        confirmPassword: Yup.string()
            .oneOf([Yup.ref('password'), null], 'Passwords must match')
            .required('Confirm password is required'),
    });

    const methods = useForm({
        resolver: yupResolver(registerSchema),
        defaultValues: {
            username: "",
            email: "",
            password: "",
            confirmPassword: "",
        },
    });

    const { setError, handleSubmit, formState: { errors, isSubmitting } } = methods;

    const onSubmit = async (data) => {
        try {
            const response = await fetch("http://localhost:8000/api/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(data),
                credentials: 'include',
            });

            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || "Registration failed");
                return;
            }

            const authHeader = response.headers.get("Authorization");
            const refreshTokenHeader = response.headers.get("X-Refresh-Token");

            if (authHeader && refreshTokenHeader) {
                const accessToken = authHeader.split(" ")[1];
                const refreshToken = refreshTokenHeader.split(" ")[1];

                localStorage.setItem("accessToken", accessToken);
                localStorage.setItem("refreshToken", refreshToken);

                setErrorMessage("");
                window.location.href = "/app";
            } else {
                setErrorMessage("Failed to retrieve tokens.");
            }
        } catch (error) {
            console.error("Error during registration:", error);
            setErrorMessage(error.message || "Registration failed");
        }
    };



    return (
        <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3}>
                {!!errorMessage && <Alert severity="error">{errorMessage}</Alert>}
                <HookTextField name="username" label="User Name" />
                <HookTextField name="email" label="Email address" />
                <HookTextField
                    name="password"
                    label="Password"
                    type={showPassword ? 'text' : 'password'}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton onClick={() => setShowPassword(!showPassword)}>
                                    {showPassword ? <RiEye2Fill /> : <RiEyeCloseLine />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                />
                <HookTextField
                    name="confirmPassword"
                    label="Confirm Password"
                    type={showPassword ? 'text' : 'password'}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton onClick={() => setShowPassword(!showPassword)}>
                                    {showPassword ? <RiEye2Fill /> : <RiEyeCloseLine />}
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                />
                <Button
                    fullWidth
                    size="large"
                    type="submit"
                    variant="contained"
                    disabled={isSubmitting}
                >
                    {isSubmitting ? 'Creating Account...' : 'Create Account'}
                </Button>
            </Stack>
        </FormProvider>
    );
};

export default RegisterForm;
