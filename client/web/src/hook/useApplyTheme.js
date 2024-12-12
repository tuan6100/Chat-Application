import { useEffect } from 'react';
import { defaultSettings } from '../config';

const useApplyTheme = () => {
    useEffect(() => {
        const themeClass = defaultSettings.themeMode === 'dark' ? 'dark-mode' : 'light-mode';
        document.documentElement.classList.add(themeClass);
    }, []);
};

export default useApplyTheme;
