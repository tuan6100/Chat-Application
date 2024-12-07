import {Container, Paper, Stack, Typography} from '@mui/material'
import React from 'react';
import ValidateEmailForm from '../../section/auth/ValidateEmailForm';

const ValidateEmail = () => {
  return (
      <Container maxWidth="sm" sx={{ mt: 8 }}>
          <Paper elevation={0} sx={{ p: 4 }}>
                <Stack spacing={2} sx={{ mb: 5, textAlign: "center" }}>
                    <Typography variant='h4' >Validate Email</Typography>
                    <Typography variant="body1" color="text.secondary">Please enter the email address associated with your account</Typography>
                </Stack>
                <ValidateEmailForm/>
          </Paper>
    </Container>
  )
}

export default ValidateEmail