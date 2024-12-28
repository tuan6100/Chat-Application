import React, { useEffect } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const Notification = ({ userId }) => {
    const channels = [
        `/client/notification/friend/${userId}`,
        `/client/notification/message/${userId}`,
        `/client/notification/group/${userId}`
    ];

    useEffect(() => {
        const socket = new SockJS(`${process.env.REACT_APP_API_BASE_URL}/ws`);
        const stompClient = new Client({ webSocketFactory: () => socket });
        stompClient.onConnect = () => {
            channels.forEach((channel) => {
                stompClient.subscribe(channel, (message) => {
                    const newNotification = JSON.parse(message.body);
                    toast.dark(newNotification, {
                        position: "top-center",
                        autoClose: 5000,
                        hideProgressBar: false,
                        closeOnClick: true,
                        pauseOnHover: true,
                        draggable: true,
                        progress: undefined,
                        theme: "colored",
                    });
                });
            });
        };

        stompClient.activate();

        return () => {
            if (stompClient) {
                stompClient.deactivate();
            }
        };
    }, [userId]);

    return <ToastContainer limit={5} />;
};

export default Notification;