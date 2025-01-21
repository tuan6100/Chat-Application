import React from 'react';
import { Box, Popover, Typography } from "@mui/material";
import { useState } from "react";
import ReactionList from "./Menu/ReactionList";

const Reactions = ({ reactions, isMine, theme }) => {
    const [reactionListAnchorEl, setReactionListAnchorEl] = useState(null);

    const handleReactionListOpen = (event) => {
        setReactionListAnchorEl(event.currentTarget);
    };

    const handleReactionListClose = () => {
        setReactionListAnchorEl(null);
    };

    return (
        <>
            <Box
                sx={{
                    position: 'absolute',
                    bottom: -20,
                    right: 0,
                    display: 'flex',
                    gap: '4px',
                    padding: '4px',
                    backgroundColor: !isMine ? '#424242' : theme.palette.primary.main,
                    borderRadius: '10px'
                }}
                onClick={handleReactionListOpen}
            >
                {reactions && reactions.map((reaction, index) => (
                    <Typography key={index} variant="body2" sx={{ fontSize: '12px' }}>
                        {reaction.reaction}
                    </Typography>
                ))}
            </Box>

            <Popover
                open={Boolean(reactionListAnchorEl)}
                anchorEl={reactionListAnchorEl}
                onClose={handleReactionListClose}
                anchorOrigin={{
                    vertical: "top",
                    horizontal: !isMine ? "left" : "right",
                }}
                transformOrigin={{
                    vertical: "top",
                    horizontal: !isMine ? "left" : "right",
                }}
            >
                <ReactionList reactions={reactions} handleReactionClose={handleReactionListClose} />
            </Popover>
        </>
    );
};

export default Reactions;