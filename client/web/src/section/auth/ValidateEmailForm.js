import React, { useState } from 'react';
import * as Yup from 'yup';
import { useForm } from 'react-hook-form';
import FormProvider from '../../component/hook-form/FormProvider';
import { yupResolver } from '@hookform/resolvers/yup';
import { TextField, Alert, Button, Stack, Link } from '@mui/material';
import {Link as RouterLink, useNavigate} from 'react-router';
import {CaretLeft, CaretRight} from "phosphor-react";

const ValidateEmailForm = () => {
    const [errorMessage, setErrorMessage] = useState('');
    const [showOptions, setShowOptions] = useState(false);
    const navigate = useNavigate();

    const ResetPasswordSchema = Yup.object().shape({
        email: Yup.string().required('Email is required').email('Email must be a valid email address'),
    });

    const defaultValues = {
        email: '',
    };

    const methods = useForm({
        resolver: yupResolver(ResetPasswordSchema),
        defaultValues,
    });

    const { register, reset, setError, handleSubmit, formState: { errors, isSubmitting } } = methods;

    const onSubmit = async (data) => {
        try {
            const response = await fetch('/api/auth/forgot-password/validate-account?email=' + encodeURIComponent(data.email));
            const responseData = await response.json();
            if (response.ok) {
                localStorage.setItem('email', data.email);
                localStorage.setItem('username', responseData.username);
                localStorage.setItem('avatar', responseData.avatar);
                navigate('/auth/validate-username');
            } else {
                if (responseData.message) {
                    setError('email', { type: 'manual', message: responseData.message });
                    setErrorMessage(responseData.message);
                    setShowOptions(true);
                } else {
                    setErrorMessage("An unexpected error occurred");
                }
            }
        } catch (error) {
            console.error("Error during validation:", error);
            reset();
            setError('afterSubmit', {
                type: 'manual',
                message: error.message || 'An unexpected error occurred',
            });
            setErrorMessage("Failed to validate email. Please try again or create a new account.");
            setShowOptions(true);
        }
    };


    return (
        <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3}>
                {!!errorMessage && <Alert severity='error'>{errorMessage}</Alert>}
                <TextField
                    {...register('email')}
                    label='Email'
                    error={!!errors.email}
                    helperText={errors.email?.message}
                />
                <Button
                    fullWidth
                    size='large'
                    type='submit'
                    variant='contained'
                    disabled={isSubmitting}
                >
                    {isSubmitting ? 'Validating your email...' : 'Validate'}
                </Button>
            </Stack>

            <Stack spacing={3} direction="row"  justifyContent="space-between" sx={{ mt: 3 }}>
                <Link
                    component={RouterLink}
                    to='/auth/login'
                    color='inherit'
                    variant='subtitle1'
                    sx={{
                        mt:3,
                        mx:"auto",
                        alignItems: 'center',
                        display: 'inline-flex',
                        textDecoration: 'none',
                    }}
                >
                    <CaretLeft />
                    Return to login page
                </Link>
                {showOptions && (
                    <Link
                        component={RouterLink}
                        to='/auth/register'
                        color='inherit'
                        variant='subtitle1'
                        sx={{
                            mt:3,
                            alignItems: 'center',
                            display: 'inline-flex',
                            textDecoration: 'none',
                        }}
                    >
                        Go to register page
                        <CaretRight />
                    </Link>
                    )}
                </Stack>

        </FormProvider>
    );
};

export default ValidateEmailForm;