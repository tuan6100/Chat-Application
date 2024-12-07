import React, { useState} from 'react';
import { TextField, Button, Avatar, Stack, MenuItem, Typography, Box, Alert } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';


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

const ProfileForm = ({}) => {
    const [avatarPreview, setAvatarPreview] = useState(null);
    const [serverError, setServerError] = useState('');

    const { control, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(ProfileSchema),
    });

    const onSubmit = async (data) => {
        try {
            const payload = {
                avatar: avatarPreview || 'https://www.shutterstock.com/image-vector/vector-flat-illustration-grayscale-avatar-600nw-2281862025.jpg',
                birthdate: data.birthday,
                gender: data.gender || null,
                bio: data.bio || null,
            };

            const accessToken = localStorage.getItem('accessToken');
            const response = await fetch(`/api/account/me/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${accessToken}`,
                },
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                throw new Error('Failed to update profile. Please try again.');
            }

            console.log('Profile updated successfully!');
        } catch (error) {
            setServerError(error.message);
        }
    };

    const handleAvatarChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setAvatarPreview(URL.createObjectURL(file));
        }
    };

    const handleDrop = (e) => {
        e.preventDefault();
        const file = e.dataTransfer.files[0];
        if (file) {
            setAvatarPreview(URL.createObjectURL(file));
        }
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3} sx={{ mb: 5, textAlign: 'center' }}>
                <Typography variant="h4">Setup your profile here</Typography>
                {serverError && <Alert severity="error">{serverError}</Alert>}
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
        </form>
    );
};

export default ProfileForm;
