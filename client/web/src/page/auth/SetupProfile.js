import React, { useState} from 'react';
import { TextField, Button, Avatar, Stack, MenuItem, Typography, Box, Alert } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import { faker } from '@faker-js/faker';


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
                avatar: avatarPreview || 'data:image/jpeg;base64,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',
                birthdate: data.birthday,
                gender: data.gender || null,
                bio: data.bio || null,
            };

            const userId = parseInt(localStorage.getItem('account-id'));
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
