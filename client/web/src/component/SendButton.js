import React, { useState, useEffect } from "react";
import { Tooltip, IconButton, Fade } from "@mui/material";
import { PaperPlaneTilt } from "phosphor-react";
import '../css/Tooltip.css';

const SendButton = ({ handleSendMessage }) => {
    const [tooltipText, setTooltipText] = useState("Click To Send");
    const [moveUp, setMoveUp] = useState(false);

    useEffect(() => {
        const interval = setInterval(() => {
            setMoveUp(true);
            setTimeout(() => {
                setTooltipText((prev) => (prev === "Click to send" ? "Or press Ctrl+Enter" : "Click to send"));
                setMoveUp(false);
            }, 1000);
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