import React, { useState } from "react";
import toast from "react-hot-toast";
import api from "../services/api";

export default function LostForm() {
    const [form, setForm] = useState({
        itemName: "",
        type: "LOST",
        category: "Documents",
        place: "",
        dateReported: "",
        tags: "",
        description: "",
        imageFile: null,
    });

    const submit = async (e) => {
        e.preventDefault();
        try {
            const fd = new FormData();
            Object.entries(form).forEach(([k, v]) => {
                if (k === "imageFile" && v) fd.append("image", v);
                else fd.append(k, v ?? "");
            });
            await api.post("/posts", fd);
            toast.success("Lost item reported");
            setForm({ ...form, itemName: "", place: "", dateReported: "", tags: "", description: "", imageFile: null });
        } catch (err) {
            console.error(err);
            toast.error(err.response?.data || "Failed to submit");
        }
    };

    return (
        <div className="max-w-3xl mx-auto p-6 bg-white rounded-xl shadow border">
            <h2 className="text-2xl font-bold mb-4">Report Lost Item</h2>
            <form onSubmit={submit} className="space-y-3">
                <input className="w-full border rounded p-2" placeholder="Item name (Wallet, Phone...)" value={form.itemName} onChange={(e) => setForm({ ...form, itemName: e.target.value })} />
                <input className="w-full border rounded p-2" placeholder="Where was it last seen?" value={form.place} onChange={(e) => setForm({ ...form, place: e.target.value })} />
                <input className="w-full border rounded p-2" placeholder="Date (yyyy-mm-dd)" value={form.dateReported} onChange={(e) => setForm({ ...form, dateReported: e.target.value })} />
                <input className="w-full border rounded p-2" placeholder="Tags (comma separated)" value={form.tags} onChange={(e) => setForm({ ...form, tags: e.target.value })} />
                <textarea className="w-full border rounded p-2" placeholder="Description" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} />
                <input type="file" accept="image/*" onChange={(e) => setForm({ ...form, imageFile: e.target.files[0] })} />
                <button type="submit" className="px-4 py-2 rounded bg-red-600 text-white">Submit Lost Item Report</button>
            </form>
        </div>
    );
}
