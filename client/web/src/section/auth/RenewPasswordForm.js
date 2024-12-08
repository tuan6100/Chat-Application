import React, {useContext, useEffect, useState} from 'react';
import * as Yup from 'yup';
import { useForm } from 'react-hook-form';
import FormProvider from '../../component/hook-form/FormProvider'
import { yupResolver } from '@hookform/resolvers/yup';
import {TextField, Alert, Button, IconButton, InputAdornment, Stack, Tooltip} from '@mui/material';
import {RiEye2Fill, RiEyeCloseLine} from "react-icons/ri";
import AuthContext from "../../context/AuthContext";
import {Link} from "react-router";
import {CaretLeft} from "phosphor-react";

const RenewPasswordForm = () => {

    const { setIsAuthenticated } = useContext(AuthContext);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');


  const NewPasswordSchema = Yup.object().shape({
      password: Yup.string()
          .required('Password is required')
          .min(6, 'Password must be at least 6 characters long'),
      confirmPassword: Yup.string()
          .required('Confirm password is required')
          .test('passwords-match', 'Passwords must match', function(value){
              return this.parent.password === value
          }),
  });

  const defaultValues = {
    newPassword:'',
    password:''
  };

  const methods = useForm({
    resolver: yupResolver(NewPasswordSchema),
    defaultValues
  });

  const [showRedirectLink, setShowRedirectLink] = useState(false);
    useEffect(() => {
        const savedUsername = localStorage.getItem("username");
        if (!savedUsername) {
            setErrorMessage("Username not found. Please validate your email again.");
            setShowRedirectLink(true)
        }}, []);

  const { register, setError, handleSubmit, formState:{errors}}
   = methods;

    const onSubmit = async (formData) => {
        try {
            const email = localStorage.getItem('email');
            if (!email) {
                throw new Error('Email is not available. Please validate your email again.');
            }
            const data = {
                email: email,
                newPassword: formData.password,
            };
            const response = await fetch('/api/auth//forgot-password/renew-password', {
                method: 'PUT',
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
                setIsAuthenticated(true);
                setErrorMessage('');
                localStorage.removeItem('email');
                localStorage.removeItem('username');
                localStorage.removeItem('avatar');
                window.location.href = '/app';
            } else {
                setErrorMessage('Failed to retrieve tokens.');
            }
        } catch (error) {
            console.error('Error during registration:', error);
            setErrorMessage(error.message || 'Registration failed');
        }
    };

    return (
        <>
            {showRedirectLink ? (
                <Stack spacing={3} textAlign="center">
                    <Alert severity="error">{errorMessage}</Alert>
                    <Link
                        component={Link}
                        to="/auth/validate-email"
                        color="inherit"
                        variant="subtitle1"
                        sx={{
                            mt: 3,
                            color: "text.primary",
                            textDecoration: "none",
                        }}
                    >
                        <CaretLeft />
                        Return to email validation page
                    </Link>
                </Stack>
            ) : (
                <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
                    <Stack spacing={3}>
                        {!!errors.afterSubmit && <Alert severity='error'>{errors.afterSubmit.message}</Alert>}
                        {!!errorMessage && <Alert severity='error'>{errorMessage}</Alert>}

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

                        <Button fullWidth size='large' type='submit' variant='contained'>Submit</Button>
                    </Stack>
                </FormProvider>
            )}
        </>
    );
};

export default RenewPasswordForm