// src/services/wsService.js
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

let globalClient = null;
let connectPromise = null;

// Track active subscriptions by destination
const activeSubs = new Map();
// pending subscriptions for when connecting
let pendingSubs = [];

const wsService = {
    client: null,

    connect(token) {
        if (globalClient && globalClient.active) {
            this.client = globalClient;
            return Promise.resolve(globalClient);
        }

        if (connectPromise) return connectPromise;

        connectPromise = new Promise((resolve) => {
            const socket = new SockJS("http://localhost:8080/ws");

            const client = new Client({
                webSocketFactory: () => socket,
                reconnectDelay: 4000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
                debug: (msg) => console.log("[WS]", msg),
                connectHeaders: token ? { Authorization: `Bearer ${token}` } : {}
            });

            client.onConnect = () => {
                console.log("WS CONNECTED");

                this.client = client;
                globalClient = client;

                // Flush pending subscriptions
                pendingSubs.forEach((sub) => {
                    this._subscribeNow(sub.dest, sub.cb);
                });
                pendingSubs = [];

                resolve(client);
                connectPromise = null;
            };

            client.onStompError = (err) => console.error("STOMP ERROR", err);

            client.activate();
        });

        return connectPromise;
    },

    /**
     * Internal: always subscribe only once per destination
     */
    _subscribeNow(dest, cb) {
        // Already subscribed? Return same unsub
        if (activeSubs.has(dest)) {
            return activeSubs.get(dest);
        }

        const sub = this.client.subscribe(dest, cb);
        const unsubFn = () => {
            try {
                sub.unsubscribe();
                activeSubs.delete(dest);
            } catch {}
        };

        activeSubs.set(dest, unsubFn);
        return unsubFn;
    },

    /**
     * Subscribe safely, prevent duplication
     */
    async subscribe(dest, cb) {
        if (this.client && this.client.active) {
            return this._subscribeNow(dest, cb);
        }

        // Queue and resolve later
        return new Promise((resolve) => {
            pendingSubs.push({
                dest,
                cb,
                resolveUnsub: resolve
            });
        });
    },

    send(dest, body) {
        if (!this.client || !this.client.active) {
            console.warn("WS NOT ACTIVE → Cannot send");
            return;
        }

        this.client.publish({
            destination: dest,
            body: JSON.stringify(body)
        });
    },

    disconnect() {
        if (globalClient) {
            try {
                globalClient.deactivate();
            } catch {}
        }
        globalClient = null;
        this.client = null;
        connectPromise = null;

        activeSubs.clear();
        pendingSubs = [];

        console.log("WS Disconnected");
    }
};

export default wsService;
