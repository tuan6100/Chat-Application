import {useEffect, useState} from "react";
import { useNavigate } from "react-router";
import useSidebar from "../../hook/useSideBar";
import {alpha, useTheme} from "@mui/material/styles";
import useMediaQuery from "@mui/material/useMediaQuery";
import useFriendsList from "../../hook/useFriendsList";
import useSearchResult from "../../hook/useSearchResult";
import useSelected from "../../hook/useSelected";
import useAuth from "../../hook/useAuth";
import useConversationProperties from "../../hook/useConversationProperties";
import {
    Avatar,
    Badge,
    Box,
    IconButton,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    Stack,
    Typography
} from "@mui/material";
import Search from "../../component/SearchBar";
import { Menu as MenuIcon } from '@mui/icons-material';


const StyledBadge = (props) => {
    return (
        <Badge
            overlap="circular"
            anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            variant="dot"
            sx={{
                "& .MuiBadge-dot": {
                    backgroundColor: props.isOnline ? "#44b700" : "gray",
                    width: "15px",
                    height: "15px",
                    borderRadius: "50%",
                    border: "2px solid white"
                }
            }}
        >
            {props.children}
        </Badge>
    );
};

const Chats = () => {
    const { isSidebarOpen, setIsSidebarOpen } = useSidebar();
    const theme = useTheme();
    const isMobile = useMediaQuery("(max-width: 600px)");
    const { searchResults, startedSearch } = useSearchResult();
    const { setSelectedUser } = useSelected();
    const { friendsList, setFriendsList } = useFriendsList();
    const anyResult = searchResults.length > 0;
    const { authFetch } = useAuth();
    const [selectedUser, setSelectedUserState] = useState(null);
    const navigate = useNavigate();
    const {setAvatar, setName, setIsOnline, setLastOnlineTime} = useConversationProperties();

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
        const intervalId = setInterval(getFriendsList, 60000);
        return () => clearInterval(intervalId);
    }, [authFetch, setFriendsList]);

    const handleUserClick = (user) => {
        setSelectedUser(user.accountId);
        setSelectedUserState(user);
        setAvatar(user.avatar);
        setName(user.username);
        setIsOnline(user.isOnline);
        setLastOnlineTime(user.lastOnlineTime);
        if (isMobile) {
            navigate(`/me/conversation/${user.accountId}`);
        }
    };

    const haveAnyFriend = friendsList.length > 0;

    return (
        <Box className="chat-box" sx={{
            position: "absolute",
            left: isSidebarOpen ? 100 : 0,
            width: isMobile ? (isSidebarOpen ? 'calc(100% - 100px)' : '100%') : 400,
            maxWidth: isMobile ? '100%' : 400,
            backgroundColor: theme.palette.mode === 'light' ? "#F8FAFF" : theme.palette.background.paper,
            boxShadow: '0px 0px 2px rgba(0,0,0,0.25)',
            transition: "left 0.5s ease-in-out, width 0.5s ease-in-out",
            zIndex: 1
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
                                        width: '100%',
                                        '&:hover': {
                                            backgroundColor: alpha(theme.palette.primary.light, 0.1),
                                        }
                                    }}
                                    button
                                    onClick={() => handleUserClick(result)}
                                >
                                    <ListItemAvatar>
                                        <StyledBadge isOnline={result.isOnline}>
                                            <Avatar
                                                src={result.avatar}
                                                sx={{ width: 50, height: 50 }}
                                            />
                                        </StyledBadge>
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

            {!isMobile && selectedUser && (
                <Box sx={{
                    position: 'absolute',
                    right: 0,
                    width: 'calc(100% - 400px)',
                    height: '100%',
                    transition: 'width 0.5s ease-in-out',
                }}>
                </Box>
            )}
        </Box>
    );
}

export default Chats;