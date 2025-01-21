import React, { useState } from 'react';
import { Chip, IconButton, Stack, Typography, MenuItem , Avatar } from "@mui/material";
import { Close } from "@mui/icons-material";
import { useTheme } from "@mui/material/styles";
const ReactionList = ({ reactions, handleReactionClose }) => {

    const [selectedReaction, setSelectedReaction] = useState('ALL');
    const theme = useTheme();

    const handleReactionClick = (reaction) => {
        setSelectedReaction(reaction);
    };

    const filterReactions = () => {
        const reactionMap = new Map();
        reactions.forEach((reaction) => {
            if (reactionMap.has(reaction.reaction)) {
                reactionMap.get(reaction.reaction).count += 1;
            } else {
                reactionMap.set(reaction.reaction, { count: 1 });
            }
        });
        return Array.from(reactionMap.entries()).map(([reaction, { count }]) => ({
            reaction,
            count,
        }));
    };

    const filteredReactions = selectedReaction === 'ALL'
        ? reactions
        : reactions.filter(reaction => reaction.reaction === selectedReaction);

    return (
        <Stack
            direction="column"
            spacing={2}
            sx={{
                p: 2,
                border: "1px solid #ccc",
                borderRadius: 2,
                backgroundColor: "#fff",
                width: 400,
                boxShadow: "0 4px 6px rgba(0, 0, 0, 0.1)",
            }}
        >
            <Stack direction="row" justifyContent="space-between">
                <Typography variant="h6">Reactions</Typography>
                <IconButton
                    onClick={handleReactionClose}
                    sx={{
                        cursor: "pointer",
                        "&:hover": { color: theme.palette.primary.main },
                    }}
                >
                    <Close />
                </IconButton>
            </Stack>

            {filteredReactions.map((reaction) => (
                <MenuItem key={reaction.accountId}>
                    <Stack direction="row" justifyContent="space-between" width="100%">
                        <Stack direction="row" spacing={1} alignItems="center">
                            <Avatar src={reaction.avatar} alt={reaction.username} />
                            <Typography>{reaction.username}</Typography>
                        </Stack>
                        <Typography>{reaction.reaction}</Typography>
                    </Stack>
                </MenuItem>
            ))}

            <Stack direction="row" spacing={1} justifyContent="center">
                <Chip
                    color={selectedReaction === 'ALL' ? "primary" : "default"}
                    label="ALL"
                    onClick={() => handleReactionClick('ALL')}
                />
                {filterReactions().map(({ reaction, count }) => (
                    <Chip
                        key={reaction}
                        color={selectedReaction === reaction ? "primary" : "default"}
                        label={`${reaction} (${count})`}
                        onClick={() => handleReactionClick(reaction)}
                    />
                ))}
            </Stack>
        </Stack>
    );
};

export default ReactionList;