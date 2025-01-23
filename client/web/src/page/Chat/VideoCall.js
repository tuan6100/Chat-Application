import React, { useState, useEffect } from 'react';
import { Box, Button, IconButton, Stack, Typography, Avatar } from '@mui/material';
import { Mic, MicOff, Videocam, VideocamOff, ScreenShare, StopScreenShare, CallEnd, ArrowBack } from '@mui/icons-material';
import useWebRTC from "../../hook/UseWebRTC";
import {useNavigate, useParams} from "react-router";
import useWebSocket from "../../hook/useWebSocket";

const VideoCallPage = () => {

    const [isMuted, setIsMuted] = useState(false);
    const [isCameraOff, setIsCameraOff] = useState(false);
    const [isScreenSharing, setIsScreenSharing] = useState(false);
    const {localVideoRef, remoteVideoRef, peerConnectionRef,
           configuration, sendSignal, initializeMedia, participants, setParticipants} = useWebRTC();
    const navigate = useNavigate();
    const {publish} = useWebSocket();
    const { param } = useParams();
    const chatId = parseInt(param);


    useEffect(() => {
        const setupCall = async () => {
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
            } catch (error) {
                console.error("Error setting up call:", error);
                if (peerConnectionRef.current) {
                    peerConnectionRef.current.close();
                    peerConnectionRef.current = null;
                }
            }
        };
        setupCall();
        return () => {
            if (localVideoRef.current && localVideoRef.current.srcObject) {
                localVideoRef.current.srcObject.getTracks().forEach(track => track.stop());
            }
            if (peerConnectionRef.current) {
                peerConnectionRef.current.close();
                peerConnectionRef.current = null;
            }
        };
    }, []);


    const toggleMute = () => {
        if (localVideoRef.current && localVideoRef.current.srcObject) {
            const audioTrack = localVideoRef.current.srcObject.getAudioTracks()[0];
            audioTrack.enabled = !audioTrack.enabled;
            setIsMuted(!audioTrack.enabled);
        } else {
            console.error("No audio stream available to toggle mute.");
        }
    };


    const toggleCamera = () => {
        if (localVideoRef.current && localVideoRef.current.srcObject) {
            const videoTrack = localVideoRef.current.srcObject.getVideoTracks()[0];
            if (videoTrack) {
                videoTrack.enabled = !videoTrack.enabled;
                setIsCameraOff(!videoTrack.enabled);
            }
        } else {
            console.error("No video stream available to toggle camera.");
        }
    };


    const toggleScreenShare = async () => {
        if (!isScreenSharing) {
            try {
                const stream = await navigator.mediaDevices.getDisplayMedia({ video: true });
                const screenTrack = stream.getVideoTracks()[0];
                const sender = peerConnectionRef.current
                    .getSenders()
                    .find((s) => s.track.kind === 'video');
                await sender.replaceTrack(screenTrack);
                screenTrack.onended = () => {
                    toggleScreenShare();
                };
                setIsScreenSharing(true);
            } catch (error) {
                console.error("Error sharing screen:", error);
            }
        } else {
            const videoTrack = localVideoRef.current.srcObject.getVideoTracks()[0];
            const sender = peerConnectionRef.current
                .getSenders()
                .find((s) => s.track.kind === 'video');
            await sender.replaceTrack(videoTrack);
            setIsScreenSharing(false);
        }
    };


    const onLeaveCall = async () => {
        if (localVideoRef.current && localVideoRef.current.srcObject) {
            await localVideoRef.current.srcObject.getTracks().forEach(track => track.stop());
        }
        if (peerConnectionRef.current) {
            peerConnectionRef.current.close();
            peerConnectionRef.current = null;
        }
        const currentUserId = localStorage.getItem("accountId");
        setParticipants(prevParticipants => prevParticipants.filter(participant => participant.id !== currentUserId));
        navigate('/me/chats');
    }


    return (
        <Box sx={{ height: '100vh', width: '100vw', position: 'relative', backgroundColor: '#1c1c1c' }}>
            <Stack direction="row" sx={{ height: '100%' }}>
                <Stack direction="row" justifyContent="space-between" sx={{ height: '100%', flex: 1, p: 2 }}>
                    <video
                        ref={localVideoRef}
                        autoPlay
                        muted
                        style={{
                            height: '40%',
                            width: '30%',
                            borderRadius: '8px',
                            backgroundColor: 'black',
                        }}
                    />
                    <video
                        ref={remoteVideoRef}
                        autoPlay
                        style={{
                            height: '85%',
                            width: '65%',
                            borderRadius: '8px',
                            backgroundColor: 'black',
                        }}
                    />
                </Stack>
                <Box
                    sx={{
                        width: '20%',
                        height: '100%',
                        backgroundColor: '#2c2c2c',
                        borderLeft: '1px solid rgba(255, 255, 255, 0.2)',
                        padding: 2,
                        overflowY: 'auto',
                    }}
                >
                    <Typography variant="h6" color="white" gutterBottom>
                        Participants
                    </Typography>
                    <Stack spacing={2}>
                        {participants.map((participant, index) => (
                            <Stack
                                key={index}
                                direction="row"
                                alignItems="center"
                                spacing={2}
                                sx={{
                                    backgroundColor: '#1c1c1c',
                                    padding: 1,
                                    borderRadius: '8px',
                                }}
                            >
                                <Avatar
                                    src={participant.avatar}
                                    alt={participant.name}
                                    sx={{ width: 40, height: 40 }}
                                />
                                <Typography variant="body1" color="white">
                                    {participant.name}
                                </Typography>
                            </Stack>
                        ))}
                    </Stack>
                </Box>
            </Stack>
            <Box
                sx={{
                    position: 'absolute',
                    bottom: 0,
                    left: 0,
                    width: '100%',
                    p: 2,
                    backgroundColor: 'rgba(0, 0, 0, 0.7)',
                }}
            >
                <Stack direction="row" justifyContent="center" alignItems="center" spacing={2}>
                    <Button
                        variant="outlined"
                        color="inherit"
                        startIcon={<ArrowBack />}
                        onClick={() => navigate('/me/chats')}
                    >
                        Back to Chat
                    </Button>

                    <IconButton onClick={toggleMute} color="inherit">
                        {isMuted ? <MicOff color="error" /> : <Mic />}
                    </IconButton>

                    <IconButton onClick={toggleCamera} color="inherit">
                        {isCameraOff ? <VideocamOff color="error" /> : <Videocam />}
                    </IconButton>

                    <IconButton onClick={toggleScreenShare} color="inherit">
                        {isScreenSharing ? <StopScreenShare color="error" /> : <ScreenShare />}
                    </IconButton>

                    <Button
                        variant="contained"
                        color="error"
                        startIcon={<CallEnd />}
                        onClick={onLeaveCall}
                    >
                        Leave Call
                    </Button>
                </Stack>
            </Box>
        </Box>
    );
};

export default VideoCallPage;
