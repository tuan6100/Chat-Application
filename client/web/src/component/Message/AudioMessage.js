import { Box, Stack } from "@mui/material";
import { useTheme } from "@mui/material/styles";

const AudioMessage = ({ message }) => {
    const theme = useTheme();
    const isMine = message.senderId === localStorage.getItem('accountId');

    return (
        <Stack direction='row' justifyContent={!isMine ? 'start' : 'end'}>
            <Box
                p={1}
                sx={{
                    backgroundColor: !isMine ? theme.palette.background.default : theme.palette.primary.light,
                    borderRadius: 1.5,
                    width: 'max-content'
                }}
            >
                <audio controls>
                    <source src={message.content} type="audio/webm" />
                    Your browser does not support the audio element.
                </audio>
            </Box>
        </Stack>
    );
};

export default AudioMessage;
