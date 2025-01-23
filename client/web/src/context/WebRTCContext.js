import { createContext, useEffect, useRef, useState } from "react";

const WebRTCContext = createContext(undefined);

export const WebRTCProvider = ({ children }) => {

    const WS_BASE_URL = process.env.REACT_APP_WS_BASE_URL;
    const localVideoRef = useRef(null);
    const remoteVideoRef = useRef(null);
    const peerConnectionRef = useRef(null);
    const socketRef = useRef(null);
    const [participants, setParticipants] = useState([]);
    // TODO: Change to Map with chatId as key in the future
    const configuration = {
        iceServers: [
            { urls: 'stun:stun.l.google.com:19302' },
            {
                urls: 'turn:160.191.50.248:3478?transport=udp',
                username: 'tuan',
                credential: '20226100',
            },
            {
                urls: 'turn:160.191.50.248:3478?transport=tcp',
                username: 'tuan',
                credential: '20226100',
            },
        ],
    };


    const sendSignal = (type, chatId, payload) => {
        const message = {
            type,
            chat: chatId,
            signal: { type, ...payload },
        };
        socketRef.current.send(JSON.stringify(message));
    };

    const handleOffer = async (data, chatId) => {
        if (!peerConnectionRef.current) {
            peerConnectionRef.current = new RTCPeerConnection(configuration);
            peerConnectionRef.current.ontrack = (event) => {
                console.log("ontrack event received:", event);
                console.log("Tracks received:", event.streams[0].getTracks());
            };

            peerConnectionRef.current.onicecandidate = (event) => {
                if (event.candidate) {
                    sendSignal("candidate", chatId, event.candidate);
                }
            };
        }
        const desc = new RTCSessionDescription(data.offer);
        await peerConnectionRef.current.setRemoteDescription(desc);
        const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        localVideoRef.current.srcObject = stream;
        stream.getTracks().forEach((track) => peerConnectionRef.current.addTrack(track, stream));
        const answer = await peerConnectionRef.current.createAnswer();
        await peerConnectionRef.current.setLocalDescription(answer);
        sendSignal("answer", chatId, answer);
    };


    const handleAnswer = async (data) => {
        const desc = new RTCSessionDescription(data.answer);
        await peerConnectionRef.current.setRemoteDescription(desc);
    };

    const handleCandidate = async (data) => {
        if (peerConnectionRef.current) {
            const candidate = new RTCIceCandidate(data.candidate);
            await peerConnectionRef.current.addIceCandidate(candidate);
        }
    };

    useEffect(() => {
        socketRef.current = new WebSocket(`${WS_BASE_URL}/ws`);
        socketRef.current.onmessage = (message) => {
            const data = JSON.parse(message.data);
            if (data.type === "offer") {
                handleOffer(data);
            } else if (data.type === "answer") {
                handleAnswer(data);
            } else if (data.type === "candidate") {
                handleCandidate(data);
            }
        };
        return () => socketRef.current.close();
    }, []);


    const initializeMedia = async () => {
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
            if (localVideoRef.current) {
                localVideoRef.current.srcObject = stream;
            }
            return stream;
        } catch (error) {
            console.error("Error initializing media devices:", error);
            throw error;
        }
    };



    return (
        <WebRTCContext.Provider value={{
            localVideoRef, remoteVideoRef, peerConnectionRef, configuration,
            sendSignal,
            handleOffer, handleAnswer, handleCandidate,
            initializeMedia,
            participants, setParticipants
        }}>
            {children}
        </WebRTCContext.Provider>
    );
};

export default WebRTCContext;