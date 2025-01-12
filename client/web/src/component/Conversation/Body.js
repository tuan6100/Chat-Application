import {Avatar, Box, Stack} from "@mui/material";
import TextMessage from "../Message/TextMessage";
import ImageMessage from "../Message/ImageMessage";
import VideoMessage from "../Message/VideoMessage";
import AudioMessage from "../Message/AudioMessage";
import FileMessage from "../Message/FileMessage";
import LinkMessage from "../Message/LinkMessage";
import useMessage from "../../hook/useMessage";
import TypingIndicator from "../Message/TypingIndicator";

const Body = ({ messages }) => {

    const {typingUsers} = useMessage();

    return (
        <Box p={3}>
            <Stack spacing={3}>
                {messages.map((message) => {
                    switch (message.type) {
                        case 'IMAGE':
                            return <ImageMessage messageKey={message.messageId} message={message} />;
                        case 'VIDEO':
                            return <VideoMessage messageKey={message.messageId} message={message} />;
                        case 'AUDIO':
                            return <AudioMessage messageKey={message.messageId} message={message} />;
                        case 'LINK':
                            return <LinkMessage messageKey={message.messageId} message={message} />;
                        case 'FILE':
                            return <FileMessage messageKey={message.messageId} message={message} />;
                        default:
                            return <TextMessage messageKey={message.messageId} message={message} />;
                    }
                })}
                {Object.entries(typingUsers).map(([senderId, { senderAvatar, typing }]) =>
                        typing && (
                            <Stack key={senderId} direction="row" spacing={1} alignItems="flex-end">
                                <Avatar sx={{ width: 30, height: 30 }} src={senderAvatar} />
                                <Box p={1}
                                    sx={{
                                        display: 'flex',
                                        justifyContent: 'start',
                                        mt: 1,
                                        border: '1px solid',
                                        borderRadius: 20,
                                        borderColor: 'grey.500',
                                        backgroundColor: 'transparent',
                                        p: 1,
                                    }}
                                >
                                    <TypingIndicator />
                                </Box>
                            </Stack>
                        )
                )}
            </Stack>
        </Box>
    );
};

export default Body;