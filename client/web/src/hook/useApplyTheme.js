import { useEffect } from 'react';
import { defaultSettings } from '../config';

const useApplyTheme = () => {
    const settings = localStorage.getItem('settings');
    const themeMode = settings ? JSON.parse(settings).themeMode : defaultSettings.themeMode;
    console.info(themeMode);
    useEffect(() => {
        const themeClass = themeMode === 'dark' ? 'dark-mode' : 'light-mode';
        document.documentElement.classList.add(themeClass);
    }, [themeMode]);
};

export default useApplyTheme;
