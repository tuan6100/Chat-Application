import { Box, Stack, Typography, Link } from "@mui/material";
import { useTheme } from "@mui/material/styles";

const LinkMessage = ({ message }) => {
    const theme = useTheme();
    const isMine = message.senderId === localStorage.getItem('accountId');

    return (
        <Stack direction='row' justifyContent={!isMine ? 'start' : 'end'}>
            <Box
                p={1.5}
                sx={{
                    backgroundColor: !isMine ? theme.palette.background.default : theme.palette.primary.light,
                    borderRadius: 1.5,
                    width: 'max-content'
                }}
            >
                <Typography variant="body2" color={theme.palette.primary.main}>
                    <Link href={message.content} target="_blank" rel="noopener noreferrer">
                        {message.content}
                    </Link>
                </Typography>
            </Box>
        </Stack>
    );
};

export default LinkMessage;
