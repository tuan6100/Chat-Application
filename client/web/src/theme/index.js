import PropTypes from "prop-types";
import {useEffect, useMemo, useState} from "react";
import { CssBaseline } from "@mui/material";
import {
  createTheme,
  ThemeProvider as MUIThemeProvider,
  StyledEngineProvider,
} from "@mui/material/styles";

import useSettings from "../hook/useSettings.js";

import palette from "./palette";
import typography from "./typography";
import breakpoints from "./breakpoints";
import shadows, { customShadows } from "./shadows";
import {defaultSettings} from "../config";
import ScreenLoading from "../component/ScreenLoading";


ThemeProvider.propTypes = {
  children: PropTypes.node,
};

export default function ThemeProvider({ children }) {
  const { themeMode, themeDirection } = useSettings();

  const isLight = themeMode === "light";

  const themeOptions = useMemo(
    () => ({
      palette: isLight ? palette.light : palette.dark,
      typography,
      breakpoints,
      shape: { borderRadius: 8 },
      direction: themeDirection,
      shadows: isLight ? shadows.light : shadows.dark,
      customShadows: isLight ? customShadows.light : customShadows.dark,
    }),
    [isLight, themeDirection]
  );

  const theme = createTheme(themeOptions);


    const [isThemeReady, setThemeReady] = useState(false);

    useEffect(() => {
        const savedTheme = localStorage.getItem('themeMode') || defaultSettings.themeMode;
        document.documentElement.classList.add(savedTheme === 'dark' ? 'dark-mode' : 'light-mode');
        setThemeReady(true);
    }, []);

    if (!isThemeReady) return <ScreenLoading />;

  return (
    <StyledEngineProvider injectFirst>
      <MUIThemeProvider theme={theme}>
        <CssBaseline />
        {children}
      </MUIThemeProvider>
    </StyledEngineProvider>
  );
}
