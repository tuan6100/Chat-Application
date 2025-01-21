import { useEffect, useState, useRef } from 'react';
import {Avatar, Box, IconButton, Stack, Typography} from '@mui/material';
import { useTheme } from '@mui/material/styles';
import {Play, Pause} from 'phosphor-react';
import useMediaQuery from '@mui/material/useMediaQuery';
import IconList from '../Menu/IconList';
import Reactions from '../Reactions';
import {formatDate} from './TextMessage';
// import { LiveAudioVisualizer } from 'react-audio-visualize';
import AudioSpectrum from 'react-audio-spectrum';

const AudioMessage = ({ message, scrollToMessage, highlightMessageId }) => {

    const theme = useTheme();
    const isMine = (message.senderId.toString() === localStorage.getItem('accountId'));
    const [showDetails, setShowDetails] = useState(false);
    const isMobile = useMediaQuery('(max-width:600px)');
    const hasSeen = (message.viewerAvatars === undefined) || (message.viewerAvatars.length === 0);
    const isHighlighted = message.messageId === highlightMessageId;
    const [audioDuration, setAudioDuration] = useState(null);
    const [isPlaying, setIsPlaying] = useState(false);
    const audioRef = useRef(null);
    const canvasRef = useRef(null);
    const [currentTime, setCurrentTime] = useState(0);

    // useEffect(() => {
    //     if (audioRef.current) {
    //         const audioContext = new (window.AudioContext || window.webkitAudioContext)();
    //         const source = audioContext.createMediaElementSource(audioRef.current);
    //         const destination = audioContext.createMediaStreamDestination();
    //         source.connect(destination);
    //         const stream = destination.stream;
    //         const recorder = new MediaRecorder(stream);
    //         setMediaRecorder(recorder);
    //     }
    // }, [audioRef]);


    useEffect(() => {
        const audio = audioRef.current;
        const handleLoadedMetadata = () => {
            setAudioDuration(audio.duration);
        };
        const handleTimeUpdate = () => {
            setCurrentTime(audio.currentTime);
        };
        const handleEnded = () => {
            setIsPlaying(false);
        };
        audio.addEventListener('loadedmetadata', handleLoadedMetadata);
        audio.addEventListener('timeupdate', handleTimeUpdate);
        audio.addEventListener('ended', handleEnded);
        return () => {
            audio.removeEventListener('loadedmetadata', handleLoadedMetadata);
            audio.removeEventListener('timeupdate', handleTimeUpdate);
            audio.removeEventListener('ended', handleEnded);
        };
    }, []);

    const handlePlayPause = () => {
        const audio = audioRef.current;
        if (isPlaying) {
            audio.pause();
        } else {
            audio.play();
        }
        setIsPlaying(!isPlaying);
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
                        <Stack direction='column' spacing={2}>
                            <audio id='audio-element' ref={audioRef} src={message.content} />
                            <Box display='flex' alignItems='center' justifyContent='space-between'>
                                <IconButton onClick={handlePlayPause}>
                                    {isPlaying ? <Pause /> : <Play />}
                                </IconButton>
                                <Typography variant='caption'>
                                    {currentTime === 0
                                        ? `${Math.floor(audioDuration / 60) || 0}:${Math.floor(audioDuration % 60)?.toString().padStart(2, '0') || '00'}`
                                        : `${Math.floor((audioDuration - currentTime) / 60) || 0}:${Math.floor((audioDuration - currentTime) % 60)?.toString().padStart(2, '0') || '00'}`}
                                </Typography>
                            </Box>
                            <AudioSpectrum
                                id="audio-canvas"
                                height={100}
                                width={200}
                                audioId={'audio-element'}
                                capColor={'red'}
                                capHeight={2}
                                meterWidth={2}
                                meterCount={512}
                                meterColor={[
                                    {stop: 0, color: '#f00'},
                                    {stop: 0.5, color: '#0CD7FD'},
                                    {stop: 1, color: 'red'}
                                ]}
                                gap={4}
                            />
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

export default AudioMessage;
