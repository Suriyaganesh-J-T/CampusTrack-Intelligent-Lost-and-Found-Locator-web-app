import React, { useEffect, useState } from "react";
import { wsService } from "../services/wsService"; // make sure path is correct

const Dashboard = ({ userId, roomId }) => {
    const [messages, setMessages] = useState([]);
    const [newRequests, setNewRequests] = useState(0);
    const token = localStorage.getItem("token");

    useEffect(() => {
        if (!token || !userId || !roomId) return;

        // Connect to WebSocket with STOMP
        wsService.connect(token, userId, roomId);

        // Subscribe to chat messages
        const chatSubscription = wsService.subscribe(`/topic/chat/${roomId}`, (msg) => {
            const body = JSON.parse(msg.body);
            setMessages((prev) => [...prev, body]);
        });

        // Subscribe to chat meta info
        const metaSubscription = wsService.subscribe(`/topic/chat/${roomId}/meta`, (msg) => {
            console.log("Chat meta:", JSON.parse(msg.body));
        });

        // Subscribe to match requests
        const matchSubscription = wsService.subscribe(`/topic/match/request/${userId}`, (msg) => {
            setNewRequests((prev) => prev + 1);
        });

        // Cleanup on unmount
        return () => {
            chatSubscription.unsubscribe();
            metaSubscription.unsubscribe();
            matchSubscription.unsubscribe();
            wsService.disconnect();
        };
    }, [userId, roomId, token]);

    return (
        <div>
            <h2>Chat Room {roomId}</h2>
            <ul>
                {messages.map((m, index) => (
                    <li key={index}>{m.content}</li>
                ))}
            </ul>

            <h2>New Match Requests: {newRequests}</h2>
        </div>
    );
};

export default Dashboard;
