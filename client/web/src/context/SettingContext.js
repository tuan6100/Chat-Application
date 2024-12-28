import {createContext, useState} from "react";
import { defaultSettings } from "../config";
import useLocalStorage from "../hook/useLocalStorage";
import getColorPresets, {
  defaultPreset,
  colorPresets,
} from "../utility/getColorPresets";

const initialState = {
  ...defaultSettings,
  onToggleMode: () => {},
  onChangeMode: () => {},
  onToggleLayout: () => {},
  onChangeLayout: () => {},
  onToggleContrast: () => {},
  onChangeContrast: () => {},
  onChangeColor: () => {},
  setColor: defaultPreset,
  colorOption: [],
  onToggleStretch: () => {},
  onResetSetting: () => {},
};

const SettingContext = createContext(initialState);

export const SettingProvider = ({ children }) => {
  const [settings, setSettings] = useLocalStorage("settings", {
    themeMode: initialState.themeMode,
    themeLayout: initialState.themeLayout,
    themeStretch: initialState.themeStretch,
    themeContrast: initialState.themeContrast,
    themeDirection: initialState.themeDirection,
    themeColorPresets: initialState.themeColorPresets,
  });


  const onToggleMode = () => {
    setSettings((prevSettings) => {
      const newThemeMode = prevSettings.themeMode === "dark" ? "light" : "dark";
      console.info('theme now is: ' + newThemeMode);
      return {
        ...prevSettings,
        themeMode: newThemeMode,
      };
    });
  };


  const onChangeMode = (event) => {
    setSettings({
      ...settings,
      themeMode: event.target.value,
    });
  };


  const onToggleLayout = () => {
    setSettings({
      ...settings,
      themeLayout:
          settings.themeLayout === "vertical" ? "horizontal" : "vertical",
    });
  };

  const onChangeLayout = (event) => {
    setSettings({
      ...settings,
      themeLayout: event.target.value,
    });
  };


  const onToggleContrast = () => {
    setSettings({
      ...settings,
      themeContrast: settings.themeContrast === "default" ? "bold" : "default",
    });
  };

  const onChangeContrast = (event) => {
    setSettings({
      ...settings,
      themeContrast: event.target.value,
    });
  };

  const onChangeColor = (event) => {
    setSettings({
      ...settings,
      themeColorPresets: event.target.value,
    });
  };

  const onToggleStretch = () => {
    setSettings({
      ...settings,
      themeStretch: !settings.themeStretch,
    });
  };

  const onResetSetting = () => {
    setSettings({
      themeMode: initialState.themeMode,
      themeLayout: initialState.themeLayout,
      themeStretch: initialState.themeStretch,
      themeContrast: initialState.themeContrast,
      themeDirection: initialState.themeDirection,
      themeColorPresets: initialState.themeColorPresets,
    });
  };

  const [enabledNotification, setEnableNotification] = useState(true);


  return (
      <SettingContext.Provider
          value={{
            ...settings,
            onToggleMode,
            onChangeMode,
            onToggleLayout,
            onChangeLayout,
            onChangeContrast,
            onToggleContrast,
            onToggleStretch,
            onChangeColor,
            setColor: getColorPresets(settings.themeColorPresets),
            colorOption: colorPresets.map((color) => ({
              name: color.name,
              value: color.main,
            })),
            onResetSetting,
            enabledNotification, setEnableNotification
          }}
      >
        {children}
      </SettingContext.Provider>
  );
};


export default SettingContext;