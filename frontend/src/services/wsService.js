import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const wsService = {
    client: null,

    connect(token, userId, roomId) {
        if (this.client && this.client.connected) return;

        // Replace with your backend WS URL
        const socket = new SockJS("http://localhost:8080/ws");
        this.client = new Client({
            webSocketFactory: () => socket,
            debug: (str) => console.log("[STOMP]", str),
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        this.client.onConnect = () => {
            console.log("[STOMP] Connected");

            // Subscribe to chat
            if (roomId) {
                this.client.subscribe(`/topic/chat/${roomId}`, (msg) => {
                    console.log("Chat message:", JSON.parse(msg.body));
                });

                this.client.subscribe(`/topic/chat/${roomId}/meta`, (msg) => {
                    console.log("Chat meta:", JSON.parse(msg.body));
                });
            }

            // Subscribe to match requests
            if (userId) {
                this.client.subscribe(`/topic/match/request/${userId}`, (msg) => {
                    console.log("New match request:", msg.body);
                });
            }
        };

        this.client.onStompError = (err) => {
            console.error("Broker reported error: ", err);
        };

        this.client.activate();
    },

    subscribe(destination, callback) {
        if (!this.client) return null;
        return this.client.subscribe(destination, callback);
    },

    disconnect() {
        if (this.client) {
            this.client.deactivate();
            this.client = null;
            console.log("[STOMP] Disconnected");
        }
    },
};


export default wsService;