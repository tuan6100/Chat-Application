import { motion, AnimatePresence } from "framer-motion";
import {
  Stack,
  Divider,
  Typography,
  Box, IconButton,
} from "@mui/material";
// import useSettings from "../../../hook/useSettings";
// import {defaultSettings } from "../../../config";
import ScrollBar from "../../ScrollBar";
import SettingFullscreen from "./SettingFullscreen";
import SettingColorPresets from "./SettingColorPresets";
import useMediaQuery from "@mui/material/useMediaQuery";
import Iconify from "../../Iconify";



export default function CustomDrawer({ open, onClose }) {
  const transition = { duration: 0.5, ease: "easeInOut" };
  const isMobile = useMediaQuery("(max-width: 600px)");

  // const {
  //   themeMode,
  //   themeLayout,
  //   themeStretch,
  //   themeContrast,
  //   themeColorPresets,
  //   onResetSetting,
  // } = useSettings();
  //
  // const notDefault =
  //     themeMode !== defaultSettings.themeMode ||
  //     themeLayout !== defaultSettings.themeLayout ||
  //     themeStretch !== defaultSettings.themeStretch ||
  //     themeContrast !== defaultSettings.themeContrast ||
  //     themeColorPresets !== defaultSettings.themeColorPresets;

  return (
      <AnimatePresence>
        {open && (
            <motion.div
                initial={{ x: "100%" }}
                animate={{ x: 0 }}
                exit={{ x: "100%" }}
                transition={transition}
                style={{
                  position: "fixed",
                  top: 0,
                  right: 0,
                  width: isMobile ? "30%" : "400px",
                  height: "200vh",
                  backgroundColor: "transparent",
                  boxShadow: "0px 4px 12px rgba(0, 0, 0, 0.1)",
                  zIndex: 1300,
                }}
            >
              <Box sx={{ p: 0, height: "100%", display: "flex", flexDirection: "column" }}>
                <Stack direction="row" justifyContent="center" alignItems="center">
                  <Typography variant="h6" alignItems="center">Custom</Typography>
                  { !isMobile && (
                      <IconButton onClick={onClose} alignItems="right">
                    <Iconify icon="eva:close-fill" />
                  </IconButton>
                  )}
                </Stack>

                <Divider sx={{ my: 2 }} />
                <ScrollBar style={{ flex: 1, padding: "16px" }}>
                  <Divider sx={{ borderStyle: "dashed" }} />
                    <Stack spacing={1} sx={{ p: 1 }}>
                      <Stack spacing={3} justifyContent="center" alignItems="center">
                        <Typography variant="subtitle1">Colors</Typography>
                        <SettingColorPresets />
                      </Stack>
                      <SettingFullscreen />
                    </Stack>
                </ScrollBar>
              </Box>
            </motion.div>
        )}
      </AnimatePresence>
  );
}

