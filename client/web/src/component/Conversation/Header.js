import React, {useState} from "react";
import {
  Avatar,
  Badge,
  Box,
  Divider,
  Fade,
  IconButton,
  Menu,
  MenuItem,
  Stack,
  Tooltip,
  Typography,
} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import {
  VideoCameraFront,
  Call,
  Search,
  MoreVert,
} from "@mui/icons-material";
import { useSearchParams } from "react-router";
import useConversationProperties from "../../hook/useConversationProperties";

const ConversationHeader = () => {

  const [searchParams, setSearchParams] = useSearchParams();
  const theme = useTheme();
  const {avatar, name, isOnline} = useConversationProperties();
  const [menuAnchor, setMenuAnchor] = useState(null);
  const openMenu = Boolean(menuAnchor);
  const handleMenuOpen = (event) => {
    setMenuAnchor(event.currentTarget);
  };
  const handleMenuClose = () => {
    setMenuAnchor(null);
  };

  return (
      <Box
          p={2}
          width="100%"
          sx={{
            backgroundColor:
                theme.palette.mode === "light" ? "#F0F2F5" : theme.palette.background.paper,
            boxShadow: "0px 1px 5px rgba(0, 0, 0, 0.1)",
            borderBottom: `1px solid ${theme.palette.divider}`,
          }}
      >
        <Stack
            direction="row"
            justifyContent="space-between"
            alignItems="center"
            sx={{ width: "100%" }}
        >
          {/* User Info Section */}
          <Stack
              direction="row"
              spacing={2}
              alignItems="center"
              onClick={() => {
                searchParams.set("open", "true");
                setSearchParams(searchParams);
              }}
              sx={{
                cursor: "pointer",
                "&:hover": {
                  backgroundColor: theme.palette.action.hover,
                  borderRadius: "8px",
                  p: 1,
                },
              }}
          >
            <Badge
                overlap="circular"
                anchorOrigin={{
                  vertical: "bottom",
                  horizontal: "right",
                }}
                badgeContent={<OnlineBadge />}
            >
              <Avatar
                  alt="User Avatar"
                  src="https://source.unsplash.com/random/40x40"
                  sx={{ width: 48, height: 48 }}
              />
            </Badge>
            <Stack spacing={0.3}>
              <Typography variant="subtitle1" fontWeight="600">
                name
              </Typography>
              <Typography variant="caption" color="textSecondary">
                Online
              </Typography>
            </Stack>
          </Stack>

          {/* Action Buttons */}
          <Stack direction="row" spacing={2} alignItems="center">
            <Tooltip title="Video Call">
              <IconButton>
                <VideoCameraFront />
              </IconButton>
            </Tooltip>

            <Tooltip title="Voice Call">
              <IconButton>
                <Call />
              </IconButton>
            </Tooltip>

            <Tooltip title="Search in Chat">
              <IconButton>
                <Search />
              </IconButton>
            </Tooltip>

            <Divider orientation="vertical" flexItem />

            <Tooltip title="Options">
              <IconButton onClick={handleMenuOpen}>
                <MoreVert />
              </IconButton>
            </Tooltip>

            {/* Dropdown Menu */}
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
            </Menu>
          </Stack>
        </Stack>
      </Box>
  );
};

// Online Badge Component
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
