import { useState } from "react";
import api from "../../services/api";
import toast from "react-hot-toast";

export default function EditProfileTab({ user, setUser }) {
    const [form, setForm] = useState({
        firstName: user.firstName,
        lastName: user.lastName,
        phone: user.phone || "",
        bio: user.bio || "",
    });

    const submit = () => {
        api.put(`/users/${user.userId}`, form)
            .then((res) => {
                toast.success("Profile updated");
                setUser(res.data);
            })
            .catch(() => toast.error("Failed to update profile"));
    };

    return (
        <div className="space-y-4">
            <input
                className="border p-2 w-full rounded"
                placeholder="First Name"
                value={form.firstName}
                onChange={(e) => setForm({ ...form, firstName: e.target.value })}
            />
            <input
                className="border p-2 w-full rounded"
                placeholder="Last Name"
                value={form.lastName}
                onChange={(e) => setForm({ ...form, lastName: e.target.value })}
            />
            <input
                className="border p-2 w-full rounded"
                placeholder="Phone Number"
                value={form.phone}
                onChange={(e) => setForm({ ...form, phone: e.target.value })}
            />
            <textarea
                className="border p-2 w-full rounded"
                placeholder="Bio"
                value={form.bio}
                rows={3}
                onChange={(e) => setForm({ ...form, bio: e.target.value })}
            ></textarea>

            <button
                onClick={submit}
                className="bg-sky-600 text-white px-4 py-2 rounded"
            >
                Save Changes
            </button>
        </div>
    );
}
