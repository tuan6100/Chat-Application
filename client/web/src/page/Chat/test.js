import React, { useRef, useState } from 'react';
import {
    Avatar,
    Badge,
    Box,
    Divider,
    IconButton,
    Paper,
    Stack,
    Tooltip,
    Typography,
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import { CameraEnhance, Call, Search, MoreVert, Straight, South } from '@mui/icons-material';
import { useNavigate } from 'react-router';
import useWebRTC from "../../hook/UseWebRTC";
import useWebSocket from "../../hook/useWebSocket";
import { generateRandomId } from "./Footer";

const Header = ({ chatId, name, avatar, isOnline, lastOnlineTime, jumpToMessage, setJumpToMessage }) => {
    const theme = useTheme();
    const [menuAnchor, setMenuAnchor] = useState(null);
    const [searchBarOpen, setSearchBarOpen] = useState(false);
    const openMenu = Boolean(menuAnchor);
    const { localVideoRef, peerConnectionRef, sendSignal } = useWebRTC();
    const { publish } = useWebSocket();
    const navigate = useNavigate();

    const handleMenuOpen = (event) => {
        setMenuAnchor(event.currentTarget);
    };
    const handleMenuClose = () => {
        setMenuAnchor(null);
    };

    const handleVideoCall = () => {
        const startCall = async () => {
            try {
                const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
                if (localVideoRef.current) {
                    localVideoRef.current.srcObject = stream;
                }
                stream.getTracks().forEach(track => peerConnectionRef.current.addTrack(track, stream));
                const offer = await peerConnectionRef.current.createOffer();
                await peerConnectionRef.current.setLocalDescription(offer);
                sendSignal('offer', offer);
                console.log("Video call initiated");
                const messageBody = {
                    randomId: `${new Date().getTime()}-${localStorage.getItem("accountId")}-${chatId}-${generateRandomId()}`,
                    senderId: localStorage.getItem("accountId"),
                    content: "Video call started. Click to join.",
                    sentTime: new Date().getTime(),
                    type: "VIDEO_CALL",
                    status: "sent",
                    chatId: chatId,
                    offer: offer,
                };
                setTimeout(() => {
                    publish(`/client/chat/${chatId}/message/send`, JSON.stringify(messageBody));
                    publish(`/chat/${chatId}/message/call`, JSON.stringify(messageBody));
                }, 1000);
                navigate(`/me/call/${chatId}`);
            } catch (error) {
                console.error("Error initiating video call:", error);
            }
        };
        startCall();
    };

    return (
        <Paper
            component='form'
            sx={{
                display: 'flex',
                alignItems: 'center',
                p: 1,
                backgroundColor: 'transparent',
                borderTop: '1px solid #3F3C3CFF',
                boxShadow: '0px -1px 5px rgba(0, 0, 0, 0.1)',
                width: '100%',
            }}
        >
            <Stack
                direction='row'
                position='relative'
                spacing={2}
                alignItems='center'
                sx={{
                    cursor: 'pointer',
                }}
            >
                <Badge
                    overlap='circular'
                    anchorOrigin={{
                        vertical: 'bottom',
                        horizontal: 'right',
                    }}
                    badgeContent={isOnline ? <Box sx={{ width: 12, height: 12, backgroundColor: '#44b700', borderRadius: '50%', border: '2px solid white' }} /> : null}
                >
                    <Avatar alt='User Avatar' src={avatar} sx={{ width: 60, height: 60 }} />
                </Badge>

                <Stack spacing={0.3}>
                    <Typography variant='subtitle1' fontWeight='600'>
                        {name}
                    </Typography>
                    <Typography variant='caption' color='textSecondary'>
                        {isOnline ? 'Online now' : `Last seen ${new Date(lastOnlineTime).toLocaleString()}`}
                    </Typography>
                </Stack>
            </Stack>

            <Stack
                direction='row'
                spacing={3}
                alignItems='center'
                sx={{ ml: 'auto' }}
            >
                <Tooltip title='Video Call'>
                    <IconButton onClick={handleVideoCall}>
                        <CameraEnhance sx={{ color: theme.palette.primary.main }} />
                    </IconButton>
                </Tooltip>

                <Tooltip title='Voice Call'>
                    <IconButton>
                        <Call sx={{ color: theme.palette.primary.main }} />
                    </IconButton>
                </Tooltip>

                <Divider orientation='vertical' flexItem />

                <Tooltip title='Options'>
                    <IconButton onClick={handleMenuOpen}>
                        <MoreVert />
                    </IconButton>
                </Tooltip>
            </Stack>

            <video ref={localVideoRef} style={{ display: 'none' }} />
        </Paper>
    );
};

export default Header;