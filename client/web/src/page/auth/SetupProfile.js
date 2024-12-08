import React, {useState} from 'react';
import { TextField, Button, Avatar, Stack, MenuItem, Typography, Box, Alert , Link} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import useAuth from "../../hook/useAuth";
import {useNavigate} from "react-router";
import ScreenLoading from "../../component/ScreenLoading";
import "../../css/handWaving.css"

const username = localStorage.getItem('username');

const ProfileSchema = Yup.object().shape({
    birthday: Yup.date()
        .required('Birthday is required')
        .test('is-old-enough', 'You must be at least 10 years old.', (value) => {
            const today = new Date();
            const birthDate = new Date(value);
            const age = today.getFullYear() - birthDate.getFullYear();
            return age >= 10;
        }),
    bio: Yup.string().max(50, 'Bio must be at most 50 characters'),
});

const ProfileForm = () => {
    const [avatarPreview, setAvatarPreview] = useState(null);
    const [success, setSuccess] = useState('');
    const [serverError, setServerError] = useState('');

    const { control, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(ProfileSchema),
    });

    const {authFetch} = useAuth()
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

    const defaultAvatar = 'https://www.shutterstock.com/image-vector/vector-flat-illustration-grayscale-avatar-600nw-2281862025.jpg'

    const onSubmit = async (data) => {
        try {
            const payload = {
                avatar: avatarPreview || defaultAvatar,
                birthdate: data.birthday,
                gender: data.gender ? (data.gender === 'Male' ? 'M' : 'F') : null,
                bio: data.bio || null,
            };
            const response = await authFetch(`/api/account/me/update`, {
                method: 'PUT',
                body: JSON.stringify(payload),
                credentials: 'include',
            });
            if (!response.ok) {
                if (response.status === 401) {
                    throw new Error("You are not authorized to access this page.");
                }
                throw new Error('Failed to update profile. Please try again.');
            }
            setSuccess('Your profile updated successfully');
            setLoading(true);
            setTimeout(() => {
                setLoading(false);
                navigate("/app");
            }, 1000);
        } catch (error) {
            setServerError(error.message);
        }
    };

    // const resizeImage = (file, maxWidth, maxHeight) => {
    //     return new Promise((resolve, reject) => {
    //         const img = new Image();
    //         const reader = new FileReader();
    //         reader.onload = (e) => {
    //             img.src = e.target.result;
    //         };
    //         img.onload = () => {
    //             const canvas = document.createElement('canvas');
    //             const ctx = canvas.getContext('2d');
    //             let width = img.width;
    //             let height = img.height;
    //             if (width > height) {
    //                 if (width > maxWidth) {
    //                     height = Math.floor((height * maxWidth) / width);
    //                     width = maxWidth;
    //                 }
    //             } else {
    //                 if (height > maxHeight) {
    //                     width = Math.floor((width * maxHeight) / height);
    //                     height = maxHeight;
    //                 }
    //             }
    //             canvas.width = width;
    //             canvas.height = height;
    //             ctx.drawImage(img, 0, 0, width, height);
    //             canvas.toBlob((blob) => {
    //                 resolve(blob);
    //             }, 'image/jpeg', 1);
    //         };
    //         reader.onerror = (error) => reject(error);
    //         reader.readAsDataURL(file);
    //     });
    // };


    const handleAvatarChange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            try {
                // const resizedImage = await resizeImage(file, 512, 512);
                setAvatarPreview(URL.createObjectURL(file));
            } catch (error) {
                console.error('Error resizing image:', error);
            }
        }
    };

    const handleDrop = async (e) => {
        e.preventDefault();
        const file = e.dataTransfer.files[0];
        if (file) {
            try {
                // const resizedImage = await resizeImage(file, 512, 512);
                setAvatarPreview(URL.createObjectURL(file));
            } catch (error) {
                console.error('Error resizing image:', error);
            }
        }
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3} sx={{ mb: 5, textAlign: 'center' }}>
                <Typography variant="h4">Hi {username || "there "} <span className="wave"> 👋 </span> <br/> Setup your
                    profile here to continue</Typography>
                {serverError && <Alert severity="error">{serverError}</Alert>}
                {success && <Alert severity="success">{success}</Alert>}
                <Controller
                    name="avatar"
                    control={control}
                    render={({ field }) => (
                        <>
                            <input
                                accept="image/*"
                                style={{ display: 'none' }}
                                id="avatar-upload"
                                type="file"
                                onChange={(e) => {
                                    field.onChange(e.target.files[0]);
                                    handleAvatarChange(e);
                                }}
                            />
                            <label htmlFor="avatar-upload">
                                <Box
                                    onDrop={handleDrop}
                                    onDragOver={(e) => e.preventDefault()}
                                    sx={{
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        mt: 2,
                                        mx: 'auto',
                                        width: 150,
                                        height: 150,
                                        border: '2px dashed #ddd',
                                        borderRadius: '50%',
                                        cursor: 'pointer',
                                    }}
                                >
                                    {avatarPreview ? (
                                        <Avatar src={avatarPreview} sx={{ width: 140, height: 140 }} />
                                    ) : (
                                        <Typography>Click here to upload avatar or drop image</Typography>
                                    )}
                                </Box>
                            </label>
                            <Link
                                variant="h5"
                                justifyContent="center"
                                onClick={() => setAvatarPreview(defaultAvatar)}
                                sx={{
                                    mt:3,
                                    alignItems: 'center',
                                    display: 'inline-flex',
                                    textDecoration: 'none',
                                }}
                                >
                                Use default avatar
                            </Link>
                        </>
                    )}
                />
                <Controller
                    name="birthday"
                    control={control}
                    render={({ field }) => (
                        <TextField
                            {...field}
                            label="Birthday"
                            type="date"
                            InputLabelProps={{ shrink: true }}
                            error={!!errors.birthday}
                            helperText={errors.birthday?.message}
                        />
                    )}
                />
                <Controller
                    name="gender"
                    control={control}
                    render={({ field }) => (
                        <TextField
                            {...field}
                            label="Gender"
                            select
                            error={!!errors.gender}
                            helperText={errors.gender?.message}
                        >
                            <MenuItem value="">None</MenuItem>
                            <MenuItem value="Male">Male</MenuItem>
                            <MenuItem value="Female">Female</MenuItem>
                        </TextField>
                    )}
                />
                <Controller
                    name="bio"
                    control={control}
                    render={({ field }) => (
                        <TextField
                            {...field}
                            label="Bio"
                            multiline
                            rows={4}
                            error={!!errors.bio}
                            helperText={errors.bio?.message}
                        />
                    )}
                />
                <Button type="submit" variant="contained">Save Profile</Button>
            </Stack>

            {loading && <ScreenLoading />}
        </form>
    );
};

export default ProfileForm;
