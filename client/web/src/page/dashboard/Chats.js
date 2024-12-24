import {alpha, useTheme} from "@mui/material/styles";
import {Avatar, Box, IconButton, List, ListItem, ListItemAvatar, ListItemText, Stack, Typography} from "@mui/material";
import {Search} from "../../component/Search";
import { Menu as MenuIcon } from '@mui/icons-material';
import useMediaQuery from "@mui/material/useMediaQuery";
import useSidebar from "../../hook/useSideBar";
import '../../css/SideBar.css';
import useSearchResult from "../../hook/useSearchResult";
import useSelectedUser from "../../hook/useSelectedUser";
import useFriendsList from "../../hook/useFriendsList";
import {useEffect} from "react";
import useAuth from "../../hook/useAuth";

const Chats = () => {
    const { isSidebarOpen, setIsSidebarOpen } = useSidebar();
    const theme = useTheme();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const { searchResults, startedSearch } = useSearchResult();
    const { setSelectedUser } = useSelectedUser();
    const { friendsList, setFriendsList } = useFriendsList();
    const anyResult = searchResults.length > 0;
    const {authFetch} = useAuth();

    useEffect(() => {
        const getFriendsList = async () => {
            try {
                const response = await authFetch('/api/account/me/friends');
                const data = await response.json();
                setFriendsList(data);
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };
        getFriendsList();
    }, [authFetch, setFriendsList]);

    const haveAnyFriend = friendsList.length > 0;

    return (
        <Box className="chat-box" sx={{
            position: "absolute",
            left: isSidebarOpen ? 100 : 0,
            width: isMobile ? (isSidebarOpen ? 'calc(100% - 100px)' : '100%') : 400,
            maxWidth: isMobile ? '100%' : 400,
            backgroundColor: theme.palette.mode === 'light' ? "#F8FAFF" : theme.palette.background.paper,
            boxShadow: '0px 0px 2px rgba(0,0,0,0.25)',
            transition: "left 0.5s ease-in-out, width 0.5s ease-in-out"
        }}>
            <Stack p={3} spacing={2} sx={{ height: "100vh" }}>
                <Stack direction="row" alignItems='center' justifyContent='space-between'>
                    <Typography variant='h5'>
                        Chats
                    </Typography>
                    <IconButton onClick={() => setIsSidebarOpen(!isSidebarOpen)}>
                        <MenuIcon />
                    </IconButton>
                </Stack>

                <Box sx={{ width: "100%", height: 50 }}>
                    <Search
                        placeholder="Find your friends..."
                        onSearch={(value) => console.log("Searching for:", value)}
                    />
                </Box>

                <Box sx={{
                    flexGrow: 1,
                    overflowY: 'auto',
                    mt: 2
                }}>
                    <List sx={{
                        width: '100%',
                        bgcolor: 'background.paper',
                        borderRadius: 2,
                        boxShadow: "0px 1px 3px rgba(0, 0, 0, 0.1)",
                    }}>
                        {(startedSearch ? searchResults : friendsList)
                            .filter(result => result.accountId !== Number(localStorage.getItem('accountId')))
                            .map((result) => (
                                <ListItem
                                    key={result.accountId}
                                    sx={{
                                        borderRadius: 2,
                                        mb: 1,
                                        '&:hover': {
                                            backgroundColor: alpha(theme.palette.primary.light, 0.1),
                                        }
                                    }}
                                    button
                                    onClick={() => setSelectedUser(result)}
                                >
                                    <ListItemAvatar>
                                        <Avatar
                                            src={result.avatar}
                                            sx={{ width: 50, height: 50 }}
                                        />
                                    </ListItemAvatar>

                                    <ListItemText
                                        primary={result.username}
                                        secondary={result.lastMessage || "No recent messages"}
                                        primaryTypographyProps={{
                                            fontWeight: 'bold',
                                            fontSize: '1rem',
                                        }}
                                        secondaryTypographyProps={{
                                            color: 'text.secondary',
                                            fontSize: '0.85rem',
                                        }}
                                    />
                                </ListItem>
                            ))}
                    </List>
                </Box>
            </Stack>

            {(!haveAnyFriend && !anyResult) && (
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    textAlign: 'center',
                    color: theme.palette.text.secondary,
                }}>
                    <Typography variant="h6" align="center">
                        Let's make friends and start a new conversation now!
                    </Typography>
                </Box>
            )}
        </Box>
    );
}


export default Chats;