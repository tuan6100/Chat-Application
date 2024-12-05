import { Link, Stack, Typography } from '@mui/material'
import { CaretLeft } from 'phosphor-react'
import React from 'react';
import { Link as RouterLink } from 'react-router';
import RenewPasswordForm from '../../section/auth/RenewPasswordForm';

const RenewPassword = () => {
  return (
    <>
    <Stack spacing={2} sx={{mb:5, position:'relative'}}>
    <Typography variant='h3' paragraph>Renew Password</Typography>
    <Typography sx={{color:'text.secondary', mb:5}}>Please set your new password</Typography>  
    </Stack>

    <RenewPasswordForm/>

    <Link component={RouterLink} to='/auth/login' color='inherit' variant='subtitle2' 
         sx={{mt:3, mx:'auto', alignItems:'center', display:'inline-flex'}}>
          <CaretLeft/>
          Return to sign in
         </Link>
    </>
  )
}

export default RenewPassword