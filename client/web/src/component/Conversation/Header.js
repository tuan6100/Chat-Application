import React, { useState } from "react";
import {
    Avatar,
    Badge,
    Box,
    Divider,
    Fade,
    IconButton,
    Menu,
    MenuItem, Paper,
    Stack,
    Tooltip,
    Typography,
} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { CameraEnhance, Call, Search, MoreVert } from "@mui/icons-material";
import useConversationProperties from "../../hook/useConversationProperties";

const ConversationHeader = () => {
  const theme = useTheme();
  const { avatar, name, isOnline } = useConversationProperties();
  const [menuAnchor, setMenuAnchor] = useState(null);
  const openMenu = Boolean(menuAnchor);
  const handleMenuOpen = (event) => {
    setMenuAnchor(event.currentTarget);
  };
  const handleMenuClose = () => {
    setMenuAnchor(null);
  };

  return (
      <Paper
          component="form"
          sx={{
              display: "flex",
              alignItems: "center",
              p: 1,
              backgroundColor: "transparent",
              borderTop: "1px solid #E0E0E0",
              boxShadow: "0px -1px 5px rgba(0, 0, 0, 0.1)",
              width: '100%',
          }}
      >
      <Stack
        direction="row"
        spacing={2}
        alignItems="center"
        sx={{
          cursor: "pointer",
        }}
      >
        <Badge
          overlap="circular"
          anchorOrigin={{
            vertical: "bottom",
            horizontal: "right",
          }}
          badgeContent={isOnline ? <OnlineBadge /> : null}
        >
          <Avatar alt="User Avatar" src={avatar} sx={{ width: 60, height: 60 }} />
        </Badge>

        <Stack spacing={0.3}>
          <Typography variant="subtitle1" fontWeight="600">
            {name}
          </Typography>
          <Typography variant="caption" color="textSecondary">
            {isOnline ? "Online" : "Offline"}
          </Typography>
        </Stack>
      </Stack>

      <Box sx={{ flexGrow: 1 }} />

      <Stack direction="row" spacing={3} alignItems="center">
        <Tooltip title="Video Call">
          <IconButton>
            <CameraEnhance
                sx={{
                    color: theme.palette.primary.main
                }}
            />
          </IconButton>
        </Tooltip>

        <Tooltip title="Voice Call">
          <IconButton>
            <Call
                sx={{
                  color: theme.palette.primary.main
                }}
              />
          </IconButton>
        </Tooltip>

        <Tooltip title="SearchBarjs in Chat">
          <IconButton>
            <Search
                sx={{
                    color: theme.palette.primary.main
                }}
            />
          </IconButton>
        </Tooltip>

        <Divider orientation="vertical" flexItem />

        <Tooltip title="Options">
          <IconButton onClick={handleMenuOpen}>
            <MoreVert />
          </IconButton>
        </Tooltip>

        <Menu
          anchorEl={menuAnchor}
          open={openMenu}
          onClose={handleMenuClose}
          TransitionComponent={Fade}
          anchorOrigin={{
            vertical: "bottom",
            horizontal: "right",
          }}
          transformOrigin={{
            vertical: "top",
            horizontal: "right",
          }}
        >
          <MenuItem onClick={handleMenuClose}>Option 1</MenuItem>
          <MenuItem onClick={handleMenuClose}>Option 2</MenuItem>
          <MenuItem onClick={handleMenuClose}>Option 3</MenuItem>
        </Menu>
      </Stack>
      </Paper>
  );
};

const OnlineBadge = () => (
  <Box
    sx={{
      width: 12,
      height: 12,
      backgroundColor: "#44b700",
      borderRadius: "50%",
      border: "2px solid white",
    }}
  />
);

export default ConversationHeader;