import React, { useState, useEffect } from "react";
import { Tooltip, IconButton, Fade } from "@mui/material";
import { PaperPlaneTilt } from "phosphor-react";
import '../css/Tooltip.css';

const SendButton = ({ handleSendMessage }) => {
    const [tooltipText, setTooltipText] = useState("Send");
    const [moveUp, setMoveUp] = useState(false);

    useEffect(() => {
        const interval = setInterval(() => {
            setMoveUp(true);
            setTimeout(() => {
                setTooltipText((prev) => (prev === "Send" ? "Or Press Ctrl+Enter" : "Send"));
                setMoveUp(false);
            }, 1000); // Duration of the move-up animation
        }, 3000);

        return () => clearInterval(interval);
    }, []);

    return (
        <Tooltip
            title={
                <div className={`tooltip-text ${moveUp ? "move-up" : ""}`}>
                    {tooltipText}
                </div>
            }
            arrow
            TransitionComponent={Fade}
            TransitionProps={{ timeout: 300 }}
        >
            <IconButton
                color="primary"
                onClick={handleSendMessage}
            >
                <PaperPlaneTilt size={28} weight="fill" />
            </IconButton>
        </Tooltip>
    );
};

export default SendButton;