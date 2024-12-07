import {Container, Link, Paper, Stack, Typography} from '@mui/material'
import { CaretLeft } from 'phosphor-react'
import React from 'react';
import { Link as RouterLink } from 'react-router';
import RenewPasswordForm from '../../section/auth/RenewPasswordForm';

const RenewPassword = () => {
    return (
        <Container maxWidth="sm" sx={{mt: 8}}>
            <Paper elevation={3} sx={{p: 4}}>
                <Stack spacing={2} sx={{mb: 5, textAlign: "center"}}>
                    <Typography variant='h4'>Renew Password</Typography>
                    <Typography variant="body1" color="text.secondary">Please set your new password</Typography>
                    <Stack spacing={3} justifyContent="center">
                        <RenewPasswordForm/>
                        <Link component={RouterLink} to='/auth/login' color='inherit' variant='subtitle2'
                              underline="none"
                              sx={{mt: 3, mx: 'auto', alignItems: 'center', display: 'inline-flex'}}>
                            <CaretLeft/>
                            Return to sign in
                        </Link>
                    </Stack>
                </Stack>
            </Paper>
        </Container>
    );
};

export default RenewPassword