import React, {useEffect, useState} from "react";
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

const Header = ({ name, avatar, isOnline, lastOnlineTime }) => {


  const theme = useTheme();
  const [menuAnchor, setMenuAnchor] = useState(null);
  const openMenu = Boolean(menuAnchor);

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

  const handleMenuOpen = (event) => {
    setMenuAnchor(event.currentTarget);
  };
  const handleMenuClose = () => {
    setMenuAnchor(null);
  };

    const aboutOnlineTime = (sentDate) => {
        const currentDate = new Date();
        const timeDiff = currentDate.getTime() - new Date(sentDate).getTime();
        const seconds = Math.floor(timeDiff / 1000);
        const minutes = Math.floor(timeDiff / (1000 * 60));
        const hours = Math.floor(timeDiff / (1000 * 60 * 60));
        const days = Math.floor(timeDiff / (1000 * 60 * 60 * 24));

        if (days === 0) {
            if (minutes < 1) {
                return seconds === 1 ? "one second ago" : `${seconds} seconds ago`;
            } else if (minutes < 60) {
                return minutes === 1 ? "one minute ago" : `${minutes} minutes ago`;
            } else if (hours < 24) {
                return hours === 1 ? "one hour ago" : `${hours} hours ago`;
            }
        }
        if (days === 1) {
            return `yesterday at ${new Date(sentDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
        }
        const dayOfWeek = new Date(sentDate).toLocaleDateString('en-US', { weekday: 'long' });
        if (days <= 7) {
            return `${dayOfWeek} at ${new Date(sentDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
        }
        return new Date(sentDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
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
        position="absolute"
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
            {isOnline ? "Online now" : `Last seen ${aboutOnlineTime(lastOnlineTime)}`}
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


      </Stack>
      </Paper>
  );
};



export default Header;