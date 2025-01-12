import React from 'react';
import { FormProvider as RHFFormProvider } from 'react-hook-form';

const FormProvider = ({children, onSubmit, methods}) => {
    return (
        <RHFFormProvider {...methods}>
            <form onSubmit={onSubmit}>
                {children}
            </form>
        </RHFFormProvider>
    );
};

export default FormProvider;
