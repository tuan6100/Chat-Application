import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';
import { TextField as MuiTextField } from '@mui/material';

export default function TextField({ name, label, type, error, helperText, InputProps, ...other }) {
    const { control } = useFormContext();

    return (
        <Controller
            name={name}
            control={control}
            render={({ field }) => (
                <MuiTextField
                    {...field}
                    {...other}
                    label={label}
                    type={type}
                    error={!!error}
                    helperText={error ? error.message : helperText}
                    fullWidth
                    Input={InputProps}
                />
            )}
        />
    );
}
