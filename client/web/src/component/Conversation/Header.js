import React, {useEffect, useRef, useState} from 'react';
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
    TextField
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import useAuth from '../../hook/useAuth';
import { CameraEnhance, Call, Search, MoreVert, Straight, South } from '@mui/icons-material';
import useWebRTC from "../../hook/UseWebRTC";
import useWebSocket from "../../hook/useWebSocket";
import  {generateRandomId} from "./Footer";
import {useNavigate} from "react-router";

const Header = ({ chatId, name, avatar, isOnline, lastOnlineTime, jumpToMessage, setJumpToMessage  }) => {

    const theme = useTheme();
    const [menuAnchor, setMenuAnchor] = useState(null);
    const [searchBarOpen, setSearchBarOpen] = useState(false);
    const openMenu = Boolean(menuAnchor);
    const {authFetch} = useAuth();
    const [searchResults, setSearchResults] = useState([]);
    const {localVideoRef, peerConnectionRef, remoteVideoRef,
        sendSignal, configuration, initializeMedia, setParticipants} = useWebRTC();
    const {publish} = useWebSocket();
    const navigate = useNavigate();

    const OnlineBadge = () => (
        <Box
            sx={{
                width: 12,
                height: 12,
                backgroundColor: '#44b700',
                borderRadius: '50%',
                border: '2px solid white',
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
                return seconds === 1 ? 'one second ago' : `${seconds} seconds ago`;
            } else if (minutes < 60) {
                return minutes === 1 ? 'one minute ago' : `${minutes} minutes ago`;
            } else if (hours < 24) {
                return hours === 1 ? 'one hour ago' : `${hours} hours ago`;
            }
        }
        if (days === 1) {
            return `yesterday at ${new Date(sentDate).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'})}`;
        }
        const dayOfWeek = new Date(sentDate).toLocaleDateString('en-US', {weekday: 'long'});
        if (days <= 7) {
            return `${dayOfWeek} at ${new Date(sentDate).toLocaleTimeString([], {hour: '2-digit', minute: '2-digit'})}`;
        }
        return new Date(sentDate).toLocaleDateString('en-US', {month: 'short', day: 'numeric', year: 'numeric'});
    };

    const handleSearchMessage = () => {
        return async (e) => {
            const value = e.target.value;
            if (value.length > 1) {
                try {
                    const response = await authFetch(`/api/chat/${chatId}/messages/search?content=${value}`);
                    const data = await response.json();
                    setSearchResults(data);
                } catch (error) {
                    console.error("Error fetching data:", error);
                }
            } else {
                setSearchResults([]);
            }
        }
    }


    useEffect(() => {
        if (searchResults.length > 0) {
            setJumpToMessage (searchResults[searchResults.length - 1]);
        }
    }, [searchResults]);

    const handleJumpToNextMessage = () => {
        const currentIndex = searchResults.indexOf(jumpToMessage);
        if (currentIndex > 0) {
            setJumpToMessage(searchResults[currentIndex - 1]);
        } else {
            setJumpToMessage(searchResults[searchResults.length - 1]);
        }
    }

    const handleJumpToPrevMessage = () => {
        const currentIndex = searchResults.indexOf(jumpToMessage);
        if (currentIndex < searchResults.length - 1) {
            setJumpToMessage(searchResults[currentIndex + 1]);
        } else {
            setJumpToMessage(searchResults[0]);
        }
    }


    const handleVideoCall = () => {
        const startCall = async () => {
            try {
                const stream = await initializeMedia();
                if (!peerConnectionRef.current) {
                    peerConnectionRef.current = new RTCPeerConnection(configuration);
                    peerConnectionRef.current.ontrack = (event) => {
                        remoteVideoRef.current.srcObject = event.streams[0];
                    };
                    peerConnectionRef.current.onicecandidate = (event) => {
                        if (event.candidate) {
                            sendSignal('candidate', event.candidate);
                        }
                    };
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
                    status: "calling",
                    chatId: chatId,
                    offer: offer,
                };
                setTimeout(() => {
                    publish(`/client/chat/${chatId}/message/send`, JSON.stringify(messageBody));
                }, 500);
                setTimeout(() => {
                    publish(`/chat/${chatId}/message/call`, JSON.stringify(messageBody));
                }, 1000);
                setParticipants(prevParticipants => [
                    ...prevParticipants,
                    {
                        id: localStorage.getItem("accountId"),
                        name: localStorage.getItem("name"),
                        avatar: localStorage.getItem("avatar"),
                    }
                ]);
                navigate(`/me/call/${chatId}`);
            } catch (error) {
                console.error("Error initiating video call:", error);
                if (peerConnectionRef.current) {
                    peerConnectionRef.current.close();
                    peerConnectionRef.current = null;
                }
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
                    badgeContent={isOnline ? <OnlineBadge/> : null}
                >
                    <Avatar alt='User Avatar' src={avatar} sx={{width: 60, height: 60}}/>
                </Badge>

                <Stack spacing={0.3}>
                    <Typography variant='subtitle1' fontWeight='600'>
                        {name}
                    </Typography>
                    <Typography variant='caption' color='textSecondary'>
                        {isOnline ? 'Online now' : `Last seen ${aboutOnlineTime(lastOnlineTime)}`}
                    </Typography>
                </Stack>
            </Stack>

            <Stack
                direction='row'
                spacing={3}
                alignItems='center'
                sx={{ml: 'auto'}}
            >
                <Tooltip title='Video Call'>
                    <IconButton onClick={handleVideoCall}>
                        <CameraEnhance sx={{color: theme.palette.primary.main}}/>
                    </IconButton>
                </Tooltip>

                <Tooltip title='Voice Call'>
                    <IconButton>
                        <Call sx={{color: theme.palette.primary.main}}/>
                    </IconButton>
                </Tooltip>

                {searchBarOpen && (
                    <TextField
                        placeholder="Search messages"
                        variant="outlined"
                        size="small"
                        sx={{ width: 200 }}
                        onChange={handleSearchMessage()}

                    />
                )}
                {searchBarOpen && (
                    <>
                        <IconButton onClick={handleJumpToNextMessage}>
                            <Straight sx={{ color: theme.palette.primary.main }} />
                        </IconButton>
                        <IconButton onClick={handleJumpToPrevMessage}>
                            <South sx={{ color: theme.palette.primary.main }} />
                        </IconButton>
                    </>
                )}
                <Tooltip title="Find messages">
                    <IconButton onClick={() => setSearchBarOpen(!searchBarOpen)}>
                        <Search sx={{ color: theme.palette.primary.main }} />
                    </IconButton>
                </Tooltip>


                <Divider orientation='vertical' flexItem/>

                <Tooltip title='Options'>
                    <IconButton onClick={handleMenuOpen}>
                        <MoreVert/>
                    </IconButton>
                </Tooltip>
            </Stack>
        </Paper>
    );
};

export default Header;