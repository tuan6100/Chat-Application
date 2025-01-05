import {Box, Stack} from "@mui/material";
import TextMessage from "../Message/TextMessage";
import ImageMessage from "../Message/ImageMessage";
import VideoMessage from "../Message/VideoMessage";
import AudioMessage from "../Message/AudioMessage";
import FileMessage from "../Message/FileMessage";
import LinkMessage from "../Message/LinkMessage";

const ConversationBody = ({messages}) => {
    return (
        <Box p={3}>
            <Stack spacing={3}>
                {messages.map((message)=>{
                    switch (message.type) {
                        case 'IMAGE':
                            return <ImageMessage  message={message} />;
                        case 'VIDEO':
                            return <VideoMessage  message={message} />;
                        case 'AUDIO':
                            return <AudioMessage  message={message} />;
                        case 'LINK':
                            return <LinkMessage  message={message} />;
                        case 'FILE':
                            return <FileMessage  message={message} />;
                        default:
                            return <TextMessage  message={message} />;
                    }
                })}
            </Stack>
        </Box>
    )
}

export default ConversationBody;
