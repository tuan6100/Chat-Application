import { yupResolver } from '@hookform/resolvers/yup';
import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import * as Yup from 'yup';
import FormProvider from '../../component/hook-form/FormProvider';
import {TextField, Alert, Button, IconButton, InputAdornment, Stack, Tooltip} from '@mui/material';
import { RiEyeCloseLine, RiEye2Fill } from 'react-icons/ri';

const RegisterForm = () => {
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const registerSchema = Yup.object().shape({
        username: Yup.string().required('Username is required'),
        email: Yup.string().required('Email is required').email('Email must be a valid email address'),
        password: Yup.string()
            .required('Password is required')
            .min(6, 'Password must be at least 6 characters long'),
        confirmPassword: Yup.string()
            .required('Confirm password is required')
            .test('passwords-match', 'Passwords must match', function(value){
                return this.parent.password === value
            }),
    });

    const methods = useForm({
        resolver: yupResolver(registerSchema),
        defaultValues: {
            username: '',
            email: '',
            password: '',
            confirmPassword: '',
        },
    });

    const { register, setError, handleSubmit, formState: { errors, isSubmitting } } = methods;

    const onSubmit = async (data) => {
        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data),
                credentials: 'include',
            });
            if (!response.ok) {
                const errorData = await response.json();
                setErrorMessage(errorData.message || 'Registration failed');
                if (errorData.message) {
                    setError('email', { type: 'manual', message: errorData.message });
                    setError('password', { type: 'manual', message: errorData.message });
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
                setErrorMessage('');
                window.location.href = '/auth/setup-profile';
            } else {
                setErrorMessage('Failed to retrieve tokens.');
            }
        } catch (error) {
            console.error('Error during registration:', error);
            setErrorMessage(error.message || 'Registration failed');
        }
    };


    return (
        <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3}>
                {!!errors.afterSubmit && <Alert severity='error'>{errors.afterSubmit.message}</Alert>}
                {!!errorMessage && <Alert severity='error'>{errorMessage}</Alert>}
                <TextField
                    {...register('username')}
                    label='User Name'/>

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
                                    {showPassword ? <RiEye2Fill/> : <RiEyeCloseLine/>}
                                </IconButton>
                            </Tooltip>
                        </InputAdornment>
                        ),
                    }}
                />

                <TextField
                    {...register('confirmPassword')}
                    label='Confirm Password'
                    type={showConfirmPassword ? 'text' : 'password'}
                    error={!!errors.confirmPassword}
                    helperText={errors.confirmPassword?.message}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position='end'>
                                <Tooltip title={showConfirmPassword ? 'Hide confirm password' : 'Show confirm password'}>
                                    <IconButton
                                        aria-label={showConfirmPassword ? 'hide the password' : 'display the password'}
                                        onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                    >
                                        {showConfirmPassword ? <RiEye2Fill /> : <RiEyeCloseLine />}
                                    </IconButton>
                                </Tooltip>
                            </InputAdornment>
                        ),
                    }}
                />

                <Button
                    fullWidth
                    size='large'
                    type='submit'
                    variant='contained'
                    disabled={isSubmitting}
                >
                    {isSubmitting ? 'Creating Account...' : 'Create Account'}
                </Button>
            </Stack>
        </FormProvider>
    );
};

export default RegisterForm;