import React, {useState} from 'react';
import {TextField, Button, Avatar, Stack, MenuItem, Typography, Box, Alert, Link, Tooltip} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as Yup from 'yup';
import useAuth from "../../hook/useAuth";
import {useNavigate} from "react-router";
import ScreenLoading from "../../component/ScreenLoading";
import "../../css/HandWaving.css"

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

    const {authFetch} = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);

    const defaultAvatar = 'https://www.shutterstock.com/image-vector/vector-flat-illustration-grayscale-avatar-600nw-2281862025.jpg'

    const onSubmit = async (data) => {
        try {
            const request = {
                avatar: avatarPreview || defaultAvatar,
                birthdate: data.birthday,
                gender: data.gender ? (data.gender === 'Male' ? 'M' : 'F') : null,
                bio: data.bio || null,
            };
            const response = await authFetch(`/api/account/me/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(request),
            });
            setSuccess('Your profile updated successfully');
            setLoading(true);
            if (localStorage.getItem('avatar')) {
                const data = await response.json();
                localStorage.setItem('avatar', data.avatar);
            }
            setTimeout(() => {
                setLoading(false);
                navigate("/app");
            }, 1000);
        } catch (error) {
            setServerError(error.message);
        }
    };


    const handleAvatarChange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            try {
                if (avatarPreview && avatarPreview !== defaultAvatar) {
                    await authFetch(`/api/file/delete?fileUrl=${encodeURIComponent(avatarPreview)}`, {
                        method: "DELETE",
                    });
                }
                const formData = new FormData();
                formData.append("file", file);
                const response = await authFetch(`/api/file/upload`, {
                    method: "POST",
                    body: formData,
                });
                if (!response.ok) {
                    throw new Error("Failed to upload avatar");
                }
                const fileUrl = await response.text();
                setAvatarPreview(fileUrl);
            } catch (error) {
                console.error("Error uploading avatar:", error.message);
            }
        }
    };

    const handleUseDefaultAvatar = async () => {
        if (avatarPreview && avatarPreview !== defaultAvatar) {
            try {
                await fetch(`/api/file/delete?fileUrl=${encodeURIComponent(avatarPreview)}`, {
                    method: "DELETE",
                });
            } catch (error) {
                console.error("Error deleting avatar:", error.message);
            }
        }
        setAvatarPreview(defaultAvatar);
    };



    const handleDrop = async (e) => {
        e.preventDefault();
        const url = e.dataTransfer.getData('text/uri-list');
        if (url && /\.(jpeg|jpg|png|gif)$/.test(url)) {
            console.info(url);
            setAvatarPreview(url);
        } else {
            console.info(url);
            console.warn("Dropped content is not a valid image file or URL.");
        }
    };

    const handleMouseEnter = () => {
        console.info("Mouse entered the drop area. Ready for paste.");
        document.addEventListener("paste", handlePaste);
    };

    const handleMouseLeave = () => {
        console.info("Mouse left the drop area.");
        document.removeEventListener("paste", handlePaste);
    };


    const handlePaste = async (e) => {
        const clipboardText = e.clipboardData.getData('text');
        if (clipboardText && /\.(jpeg|jpg|png|gif)$/.test(clipboardText)) {
            setAvatarPreview(clipboardText);
        } else {
            console.warn('Pasted content is not a valid image URL.');
        }
    };

    return (
        <form onSubmit={handleSubmit(onSubmit)}>
            <Stack spacing={3} sx={{ mb: 5, textAlign: 'center' }}>
                <Typography variant="h4">Hi {username || "there "} <span className="wave"> 👋 </span> <br/> Setup your
                    profile here to continue</Typography>
                {serverError && <Alert severity="error">{serverError}</Alert>}
                {success && <Alert severity="success">{success}</Alert>}
                <Tooltip title="" >
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
                                    <Tooltip title="Click to upload, or drop, or paste image link here" placement="right">
                                        <Box
                                            onDrop={handleDrop}
                                            onDragOver={(e) => e.preventDefault()}
                                            onMouseEnter={handleMouseEnter}
                                            onMouseLeave={handleMouseLeave}
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
                                                <Typography>Upload your avatar here</Typography>
                                            )}
                                        </Box>
                                    </Tooltip>
                                </label>
                                <Link
                                    variant="h5"
                                    justifyContent="center"
                                    onClick={handleUseDefaultAvatar}
                                    sx={{
                                        mt: 3,
                                        alignItems: 'center',
                                        display: 'inline-flex',
                                        textDecoration: 'none',
                                    }}
                                    underline="none"
                                >
                                    Or use default avatar
                                </Link>
                            </>
                        )}
                    />
                </Tooltip>

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
