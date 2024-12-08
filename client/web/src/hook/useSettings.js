import { useContext } from 'react';
import { SettingContext } from '../context/SettingContext';


const useSettings = () => useContext(SettingContext);

export default useSettings;