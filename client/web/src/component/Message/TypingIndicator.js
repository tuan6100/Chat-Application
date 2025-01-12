import {Box} from "@mui/material";

const TypingIndicator = () => (
    <Box display="flex" alignItems="center">
            <span className="dot" />
            <span className="dot" />
            <span className="dot" />
            <style>
                    {`
                .dot {
                    height: 10px;
                    width: 10px;
                    margin: 0 5px;
                    background-color: #ccc;
                    border-radius: 50%;
                    display: inline-block;
                    animation: wave 1s infinite ease-in-out;
                }
                .dot:nth-child(1) { animation-delay: 0s; }
                .dot:nth-child(2) { animation-delay: 0.2s; }
                .dot:nth-child(3) { animation-delay: 0.4s; }

                @keyframes wave {
                    0%, 100% {
                        transform: translateY(0);
                    }
                    50% {
                        transform: translateY(-10px);
                    }
                }
            `}
            </style>
    </Box>
);

export default TypingIndicator;


