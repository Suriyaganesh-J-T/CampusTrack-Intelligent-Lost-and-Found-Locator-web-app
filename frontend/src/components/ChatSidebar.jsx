import React, { useEffect, useState } from "react";
import api from "../services/api";
import { useNavigate } from "react-router-dom";

export default function ChatSidebar() {
    const [rooms, setRooms] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const loadRooms = async () => {
            try {
                const res = await api.get("/chat/my-rooms");
                setRooms(res.data || []);
            } catch (err) {
                console.error("Failed to load rooms", err);
            }
        };
        loadRooms();
    }, []);

    return (
        <div className="bg-[#111b21] text-white w-full p-4 rounded-md space-y-3 border border-gray-700">
            <h2 className="text-lg font-bold text-green-400">Chats</h2>

            {rooms.length === 0 && (
                <p className="text-gray-400 text-sm">No chats yet</p>
            )}

            {rooms.map((room) => {
                const partner = room.user1?.name || room.user2?.name;
                return (
                    <div
                        key={room.id}
                        onClick={() => navigate(`/chat/${room.id}`)}
                        className="cursor-pointer bg-[#202c33] p-3 rounded-md
                                   hover:bg-[#2a3942] transition"
                    >
                        <p className="font-semibold text-green-300">
                            {partner}
                        </p>
                        <p className="text-xs text-gray-300">
                            Room #{room.id}
                        </p>
                    </div>
                );
            })}
        </div>
    );
}
