import React, { useState } from 'react';
import * as Yup from 'yup';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { Link } from 'react-router';
import useAuth from "../../hook/useAuth";
import FormProvider from '../../component/FormProvider';
import { TextField, Alert, Button, IconButton, InputAdornment, Stack, Tooltip } from '@mui/material';
import { RiEyeCloseLine, RiEye2Fill } from 'react-icons/ri';

const LoginForm = () => {

    const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

    const { setIsAuthenticated } = useAuth();

    const [showPassword, setShowPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const loginSchema = Yup.object().shape({
        email: Yup.string().required('Email is required').email('Invalid email format'),
        password: Yup.string().required('Password is required'),
    });

    const methods = useForm({
        resolver: yupResolver(loginSchema),
        defaultValues: {
            email: '',
            password: '',
        },
    });

    const { register, setError, handleSubmit, formState: { errors, isSubmitting } } = methods;

    const onSubmit = async (data) => {
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(data),
                credentials: 'include',
            });
            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || 'Login failed');
                if (errorData.field === 'email') {
                    setError('email', { type: 'manual', message: errorData.message });
                } else if (errorData.field === 'password') {
                    setError('password', { type: 'manual', message: errorData.message });
                } else {
                    setErrorMessage(errorData.message || 'Login failed');
                }
                return;
            }

            const authHeader = response.headers.get('Authorization');
            const refreshTokenHeader = response.headers.get('X-Refresh-Token');
            if (authHeader && refreshTokenHeader) {
                const accessToken = authHeader.split(' ')[1];
                const refreshToken = refreshTokenHeader.split(' ')[1];
                localStorage.setItem('accessToken', accessToken);
                localStorage.setItem('refreshToken', refreshToken);
                setIsAuthenticated(true);

                const newResponse = await fetch(`${API_BASE_URL}/api/account/me`, {
                    headers: {
                        'Authorization': `Bearer ${accessToken}`,
                    },
                    credentials: 'include',
                });
                if (!newResponse.ok) {
                    const newErrorData = await newResponse.json();
                    setErrorMessage(newErrorData);
                    return;
                }
                const newData = await newResponse.json();
                localStorage.setItem('accountId', newData.accountId);
                localStorage.setItem('username', newData.username);
                localStorage.setItem('avatar', newData.avatar);
                localStorage.setItem('email', newData.email);
                setErrorMessage('');
                window.location.href = '/me';
            } else {
                setErrorMessage('Failed to retrieve tokens.');
            }
            const accountId = localStorage.getItem('accountId');
            const responseOnline = await fetch(`${API_BASE_URL}/api/account/me/online?accountId=${accountId}`, {
                method: 'POST',
                credentials: 'include',
            });
            if (!responseOnline.ok) {
                console.error('Failed to mark user online');
            }
        } catch (error) {
            console.error('Error during login:', error);
            setErrorMessage(error.message || 'Login failed');
        }
    };

    return (
        <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3}>
                {!!errors.afterSubmit && <Alert severity='error'>{errors.afterSubmit.message}</Alert>}
                {!!errorMessage && <Alert severity='error'>{errorMessage}</Alert>}
                <TextField
                    {...register('email')}
                    label='Email'
                    error={!!errors.email}
                    helperText={errors.email?.message}
                />

                <TextField
                    {...register('password')}
                    label='Password'
                    type={showPassword ? 'text' : 'password'}
                    error={!!errors.password}
                    helperText={errors.password?.message}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position='end'>
                                <Tooltip title={showPassword ? 'Hide password' : 'Show password'}>
                                    <IconButton
                                        aria-label={showPassword ? 'hide the password' : 'display the password'}
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

            <Stack alignItems='flex-end' sx={{ my: 2 }}>
                <Link  to='/auth/validate-email' variant="subtitle2" style={{ textDecoration: 'aliceblue', color: 'aliceblue' }}>
                    Forgot Password?
                </Link>
            </Stack>

            <Button
                fullWidth
                size='large'
                type='submit'
                variant='contained'
                disabled={isSubmitting}
            >
                {isSubmitting ? 'Logging in...' : 'Log in'}
            </Button>
        </FormProvider>
    );
};

export default LoginForm;