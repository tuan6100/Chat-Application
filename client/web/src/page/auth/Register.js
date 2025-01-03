import {Link, Paper, Stack, Typography} from '@mui/material';
import { Link as RouterLink } from 'react-router';
import React from 'react'
import RegisterForm from '../../section/auth/RegisterForm';

const Register = () => {
  return (
    <Stack spacing={2} sx={{mb:5, position:'relative', textAlign: "center"}}>
        <Paper elevation={3} sx={{ p: 4 }}>
            <Typography variant='h4'>
                Get Started With Chat
            </Typography>
            <Stack spacing={0.5} sx={{mb:5, position:'relative', textAlign: "center"}}>
                <Typography variant='body2'>Allready have an account?</Typography>
                <Link component={RouterLink} to='/auth/login' variant='subtitle2' color="info" underline="none">
                    Sign in
                </Link>
            </Stack>
            <RegisterForm/>
        </Paper>

    {/*    <Typography component={'div'} sx={{color:'text.secondary', mt:3, typography:'caption'*/}
    {/*,textAlign:'center'}}>{'By signining up, I agree to '}*/}
    {/*<Link underline='always' color='text.primary'>Terms of service</Link>{' and '}*/}
    {/*<Link underline='always' color='text.primary'>Privacy policy</Link>*/}
    {/*</Typography>*/}
    </Stack>
  )
}

export default Register