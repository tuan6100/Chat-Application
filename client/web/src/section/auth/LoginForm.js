import React , { useState } from 'react';
import * as Yup from 'yup';
import { useForm } from 'react-hook-form';
import FormProvider from '../../component/hook-form/FormProvider'
import { yupResolver } from '@hookform/resolvers/yup';
import { Alert, Button, IconButton, InputAdornment, Link, Stack } from '@mui/material';
import  TextField  from '../../component/hook-form/TextField';
import { Eye, EyeSlash } from 'phosphor-react';
import { Link as RouterLink } from 'react-router-dom';

const LoginForm = () => {

  const [showPassword, setShowPassword] = useState(false);

  const loginSchema = Yup.object().shape({
    email:Yup.string().required('Email is required').email('Email must be a valid email address'),
    password:Yup.string().required('Password is required')
  });

  const defaultValues = {
    //fetch
  };

  const methods = useForm({
    resolver: yupResolver(loginSchema),
    defaultValues
  });

  const {reset, setError, handleSubmit, formState:{errors, isSubmitting, isSubmitSuccessful}}
   = methods;

   const onSubmit = async (data) =>{
        try {
            //submit data to backend
        } catch (error) {
            console.log(error);
            reset();
            setError('afterSubmit',{
                ...error,
                message: error.message
            })
        }
   }

  return (
    <FormProvider methods={methods} onSubmit={handleSubmit(onSubmit)}>
        <Stack spacing={3}>
            {!!errors.afterSubmit && <Alert severity='error'>{errors.afterSubmit.message}</Alert>}
        
        <TextField name='email' label='Email address'/>
        <TextField name='password' label='Password' type={showPassword ? 'text' : 'password'}
        InputProps={{endAdornment:(
            <InputAdornment>
            <IconButton onClick={()=>{
                setShowPassword(!showPassword);
            }}>
                {showPassword ? <Eye/>: <EyeSlash/>}
            </IconButton>
            </InputAdornment>
        )}}/>
        </Stack>
        <Stack alignItems={'flex-end'} sx={{my:2}}>
            <Link component={RouterLink} to='/auth/reset-password'
             variant='body2' color='inherit' underline='always'>Forgot Password?</Link>
        </Stack>
        <Button fullWidth color='inherit' size='large' type='submit' variant='contained'
        sx={{bgcolor:'text.primary', color:(theme)=> theme.palette.mode === 'light' ?
         'common.white':'grey.800',
         '&:hover':{
            bgcolor:'text.primary',
            color:(theme)=> theme.palette.mode === 'light' ? 'common.white':'grey.800',
         }}}>Login</Button>
    </FormProvider>
  )
}

export default LoginForm