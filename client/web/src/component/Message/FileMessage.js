import { Box, Stack, Typography, IconButton } from "@mui/material";
import { useTheme } from "@mui/material/styles";
import { Download } from "@mui/icons-material";

const FileMessage = ({ message }) => {
    const theme = useTheme();
    const isMine = message.senderId === localStorage.getItem('accountId');

    const handleDownload = () => {
        window.open(message.content, "_blank");
    };

    const fileName = message.content.substring(message.content.lastIndexOf("/") + 1);

    return (
        <Stack direction='row' justifyContent={!isMine ? 'start' : 'end'}>
            <Box
                p={1.5}
                sx={{
                    backgroundColor: !isMine ? theme.palette.background.default : theme.palette.primary.light,
                    borderRadius: 1.5,
                    width: 'max-content',
                    display: 'flex',
                    alignItems: 'center',
                }}
            >
                <Typography variant="body2" color={!isMine ? theme.palette.text.primary : '#fff'}>
                    {fileName}
                </Typography>
                <IconButton onClick={handleDownload} sx={{ ml: 1, color: theme.palette.primary.main }}>
                    <Download />
                </IconButton>
            </Box>
        </Stack>
    );
};

export default FileMessage;
