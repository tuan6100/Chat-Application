import { Box, Stack } from "@mui/material";
import { useTheme } from "@mui/material/styles";

const VideoMessage = ({ message }) => {
    const theme = useTheme();
    const isMine = message.senderId === localStorage.getItem('accountId');

    return (
        <Stack direction='row' justifyContent={!isMine ? 'start' : 'end'}>
            <Box
                sx={{
                    backgroundColor: !isMine ? theme.palette.background.default : theme.palette.primary.light,
                    borderRadius: 1.5,
                    overflow: "hidden",
                    maxWidth: "50%",
                    position: "relative",
                }}
            >
                <video
                    controls
                    src={message.content}
                    style={{
                        width: "100%",
                        height: "auto",
                        borderRadius: 8,
                    }}
                />
            </Box>
        </Stack>
    );
};

export default VideoMessage;
