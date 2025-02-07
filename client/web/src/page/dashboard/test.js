import React, {useEffect, useState} from 'react';
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
} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import useAuth from '../../hook/useAuth';
import { CameraEnhance, Call, Search, MoreVert, Straight, South } from '@mui/icons-material';
import SearchBar from '../SearchBar';
import useSearchResult from '../../hook/useSearchResult';
import useWebRTC from "../../hook/UseWebRTC";
import useWebSocket from "../../hook/useWebSocket";

const Header = ({ chatId, name, avatar, isOnline, lastOnlineTime, jumpToMessage, setjumpToMessage }) => {

    const theme = useTheme();
    const [menuAnchor, setMenuAnchor] = useState(null);
    const [searchBarOpen, setSearchBarOpen] = useState(false);
    const openMenu = Boolean(menuAnchor);
    const {authFetch} = useAuth();
    const { searchResults } = useSearchResult();
    const { localVideoRef, remoteVideoRef, sendSignal } = useWebRTC();
    const { publish } = useWebSocket();

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
            return `yesterday at ${new Date(sentDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
        }
        const dayOfWeek = new Date(sentDate).toLocaleDateString('en-US', { weekday: 'long' });
        if (days <= 7) {
            return `${dayOfWeek} at ${new Date(sentDate).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
        }
        return new Date(sentDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    };


    useEffect(() => {
        if (searchResults.length > 0) {
            setjumpToMessage(searchResults[searchResults.length - 1]);
        }
    }, [searchResults]);

    const handleJumpToNextMessage = () => {
        if (jumpToMessage > 0) {
            setjumpToMessage(jumpToMessage - 1);
        }
        else {
            setjumpToMessage(searchResults[searchResults.length - 1]);
        }
    }

    const handleJumpToPrevMessage = () => {
        if (jumpToMessage < searchResults.length - 1) {
            setjumpToMessage(jumpToMessage + 1);
        }
        else {
            setjumpToMessage(0);
        }
    }


    // const handleVideoCall = () => {
    //     const startCall = async () => {
    //         try {
    //             const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
    //             localVideoRef.current.srcObject = stream;
    //             stream.getTracks().forEach(track => peerConnectionRef.current.addTrack(track, stream));
    //             const offer = await peerConnectionRef.current.createOffer();
    //             await peerConnectionRef.current.setLocalDescription(offer);
    //             sendSignal('offer', offer);
    //             console.log("Video call initiated");
    //             const messageBody = {
    //                 randomId: `${new Date().getTime()}-${localStorage.getItem("accountId")}-${chatId}-${generateRandomId()}`,
    //                 accountId: localStorage.getItem("accountId"),
    //                 content: "Video call started. Click to join.",
    //                 callType: "VIDEO_CALL",
    //                 offer,
    //                 sentTime: new Date().getTime(),
    //                 type: "CALL",
    //                 chatId: chatId,
    //                 status: "sent",
    //             };
    //             setTimeout(() => {
    //                 publish(`/client/chat/${chatId}/message/send`, JSON.stringify(messageBody));
    //                 publish(`/chat/${chatId}/message/call`, JSON.stringify(messageBody));
    //             }, 1000);
    //         } catch (error) {
    //             console.error("Error initiating video call:", error);
    //         }
    //     };
    //     startCall();
    // };


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
                position='absolute'
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
                    badgeContent={isOnline ? <OnlineBadge /> : null}
                >
                    <Avatar alt='User Avatar' src={avatar} sx={{ width: 60, height: 60 }} />
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

            {/*<Box sx={{ flexGrow: 1 }} />*/}

            <Stack direction='row' spacing={3} alignItems='center'>
                <Tooltip title='Video Call'>
                    <IconButton >
                        <CameraEnhance
                            sx={{color: theme.palette.primary.main}}
                        />
                    </IconButton>
                </Tooltip>

                <Tooltip title='Voice Call'>
                    <IconButton>
                        <Call
                            sx={{color: theme.palette.primary.main}}
                        />
                    </IconButton>
                </Tooltip>

                <Box sx={{
                    backgroundColor: 'transparent',
                    border: searchBarOpen ? '1px solid #ccc' : 'none',
                    transition: 'border 0.5s ease-in-out'
                }}
                >
                    {searchBarOpen && (
                        <>
                            <SearchBar placeholder={"Find messages..."} endpoint={`/api/chat/${chatId}/message/search?content=`} />
                            <IconButton onClick={handleJumpToNextMessage}>
                                <Straight sx={{color: theme.palette.primary.main}}/>
                            </IconButton>
                            <IconButton onClick={handleJumpToPrevMessage}>
                                <South sx={{color: theme.palette.primary.main}}/>
                            </IconButton>
                        </>
                    )}
                    <Tooltip title="Find messages">
                        <IconButton onClick={() => setSearchBarOpen(!searchBarOpen)}>
                            <Search sx={{color: theme.palette.primary.main}}/>
                        </IconButton>
                    </Tooltip>
                </Box>

                <Divider orientation='vertical' flexItem />

                <Tooltip title='Options'>
                    <IconButton onClick={handleMenuOpen}>
                        <MoreVert />
                    </IconButton>
                </Tooltip>


            </Stack>
        </Paper>
    );
};



export default Header;