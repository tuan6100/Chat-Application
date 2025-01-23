import {useTheme} from '@mui/material/styles';
import {useState} from 'react';
import useMediaQuery from '@mui/material/useMediaQuery';
import {Avatar, Box, IconButton, Stack, Typography} from '@mui/material';
import {Call, CallEnd} from '@mui/icons-material';
import IconList from '../Menu/IconList';
import {formatDate} from './TextMessage';
import Reactions from '../Reactions';
import useWebRTC from "../../hook/UseWebRTC";
import {useNavigate} from "react-router";


const CallMessage = ({ message, scrollToMessage, highlightMessageId }) => {

    const theme = useTheme();
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));
    const [showDetails, setShowDetails] = useState(false);
    const isMobile = useMediaQuery('(max-width:600px)');
    const hasSeen = (message.viewerAvatars === undefined) || (message.viewerAvatars.length === 0);
    const isHighlighted = message.messageId === highlightMessageId;
    const {localVideoRef, remoteVideoRef, peerConnectionRef, sendSignal, initializeMedia, setParticipants, configuration} = useWebRTC();
    const navigate = useNavigate();


    const handleJoinCall = async (chatId, offer) => {
        const startCall = async () => {
            try {
                const stream = await initializeMedia();
                if (!peerConnectionRef.current) {
                    peerConnectionRef.current = new RTCPeerConnection(configuration);
                    peerConnectionRef.current.ontrack = (event) => {
                        if (remoteVideoRef.current) {
                            remoteVideoRef.current.srcObject = event.streams[0];
                        } else {
                            console.error("remoteVideoRef.current is null");
                        }
                    };
                    peerConnectionRef.current.onicecandidate = (event) => {
                        if (event.candidate) {
                            sendSignal('candidate', event.candidate);
                        }
                    };
                }
                const desc = new RTCSessionDescription({ type: offer.type, sdp: offer.sdp });
                await peerConnectionRef.current.setRemoteDescription(desc);
                stream.getTracks().forEach(track => peerConnectionRef.current.addTrack(track, stream));
                const answer = await peerConnectionRef.current.createAnswer();
                await peerConnectionRef.current.setLocalDescription(answer);
                sendSignal('answer', chatId, answer);
                setParticipants(prevParticipants => [
                    ...prevParticipants,
                    {
                        id: localStorage.getItem("accountId"),
                        name: localStorage.getItem("name"),
                        avatar: localStorage.getItem("avatar"),
                    }
                ]);
                navigate(`/me/call/${chatId}`);
                console.log("Joined call successfully.");
            } catch (error) {
                console.error('Error joining video call:', error);
                if (peerConnectionRef.current) {
                    peerConnectionRef.current.close();
                    peerConnectionRef.current = null;
                }
            }
        }
        startCall();
    };



    return (
        <Stack direction='row' spacing={3} justifyContent={!isMine ? 'start' : 'end'} sx={{width: '100%'}}>
            <Stack
                direction='row'
                spacing={1}
                alignItems='flex-end'
                justifyContent='flex-end'
                sx={{'&:hover .message-actions': {opacity: 1}}}
            >
                {!isMine && <Avatar sx={{width: 30, height: 30}} src={message.senderAvatar}/>}

                {isMine && <IconList message={message}/>}
                <Stack direction='column' alignItems={!isMine ? 'flex-start' : 'flex-end'}>
                    {showDetails && (
                        <Typography variant='caption' color={theme.palette.text.secondary} sx={{mb: 0.5}}>
                            {formatDate(message.sentTime)}
                        </Typography>
                    )}

                    {message.type === 'FILE_FORWARDED' && (
                        <Typography variant='caption' color={theme.palette.text.secondary} sx={{mb: 0.5}}>
                            {isMine ? 'You' : message.senderUsername} forwarded a message:
                        </Typography>
                    )}

                    {message.replyToMessageId && message.replyToMessageContent && (
                        <Box
                            onClick={() => scrollToMessage(message.replyToMessageId)}
                            sx={{
                                position: 'relative',
                                width: 'fit-content',
                                backgroundColor: 'rgba(45,43,43,0.9)',
                                borderRadius: '15px',
                                padding: '8px',
                                cursor: 'pointer',
                                zIndex: 1,
                            }}
                        >
                            <Typography variant='body2' color='text.secondary'>
                                {message.replyToMessageContent}
                            </Typography>
                        </Box>
                    )}

                    <Box
                        p={1}
                        sx={{
                            backgroundColor: '#424242',
                            borderRadius: '15px',
                            width: '200px',
                            marginTop: '-7px',
                            zIndex: 2,
                            transform: isHighlighted ? 'scale(1.2)' : 'none',
                            transition: 'transform 0.5s ease-in-out',
                            position: 'relative'
                        }}
                    >
                        <Stack direction='column'
                               justifyContent='flex-start'
                               spacing={2}
                               sx={{cursor: 'pointer'}}
                               onClick={() => window.open(message.content, '_blank')}
                        >
                            <Typography variant='caption' color={theme.palette.text.primary}>
                                {message.content}
                            </Typography>

                            {message.status === 'ended'? (
                                <>
                                    <Typography variant='caption' color={theme.palette.text.primary}>
                                        {message.content}
                                    </Typography>
                                </>
                            ) : (
                                <>
                                    <Stack direction='row' justifyContent='space-between'>
                                        <IconButton onClick={() => handleJoinCall(message.chatId, message.offer)}>
                                            <Call sx={{color: '#20b410'}}/>
                                        </IconButton>
                                        <IconButton>
                                            <CallEnd sx={{color: '#f44336'}}/>
                                        </IconButton>
                                    </Stack>
                                </>
                            )}
                        </Stack>
                        {message.reactions && message.reactions.length > 0 && (
                            <Reactions reactions={message.reactions}/>
                        )}
                    </Box>

                    {showDetails && !hasSeen && (
                        <Stack
                            direction='row'
                            spacing={1}
                            sx={{mt: 0.5, animation: 'toggle-in 0.5s ease-in-out'}}
                        >
                            {message.viewerAvatars.map((avatar, index) => (
                                <Avatar key={index} sx={{width: 20, height: 20}} src={avatar}/>
                            ))}
                        </Stack>
                    )}
                    {showDetails && hasSeen && isMine && (
                        <Typography
                            variant='caption'
                            color={theme.palette.text.secondary}
                            sx={{mt: 0.5, animation: 'toggle-in 0.5s ease-in-out'}}
                        >
                            {message.status || 'sent'}
                        </Typography>
                    )}
                </Stack>
                {!isMine && <IconList message={message}/>}
            </Stack>
        </Stack>
    );
}

export default CallMessage;