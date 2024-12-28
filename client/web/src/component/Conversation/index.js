import Header from "./Header";
import Body from "./Body";
import Footer from "./Footer";
import { Box, Stack } from "@mui/material";
import useMediaQuery from "@mui/material/useMediaQuery";
import useSelected from "../../hook/useSelected";

const Conversation = ({ user }) => {
    const isMobile = useMediaQuery("(max-width: 600px)");
    const { isChatOpen } = useSelected();

    return (
        isChatOpen &&
        <Stack height="100%" maxHeight="100vh" width="auto">
            <Header user={user} />
            { !isMobile &&
                <Box className="scrollbar" width="100%" sx={{ flexGrow: 1, height: "100%", overflowY: "scroll" }}>
                    <Body user={user} />
                </Box>
            }
            <Footer user={user} />
        </Stack>
    );
};

export default Conversation;