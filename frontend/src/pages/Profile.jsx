// src/pages/Profile.jsx
import React, { useState, useEffect } from "react";
import api from "../services/api";
import useAuth from "../hooks/useAuth";
import toast from "react-hot-toast";

export default function Profile() {
    const { userId } = useAuth();
    const [tab, setTab] = useState("info");
    const [user, setUser] = useState({});
    const [loading, setLoading] = useState(true);

    const [editForm, setEditForm] = useState({});
    const [passwordForm, setPasswordForm] = useState({ old: "", new: "", confirm: "" });
    const [imageFile, setImageFile] = useState(null);
    const [preview, setPreview] = useState(null);

    useEffect(() => {
        (async () => {
            try {
                const res = await api.get(`/users/${userId}`);
                setUser(res.data);
                setEditForm({
                    firstName: res.data.firstName,
                    lastName: res.data.lastName,
                    phone: res.data.phone || "",
                    bio: res.data.bio || ""
                });
            } catch (err) {
                console.error(err);
            }
            setLoading(false);
        })();
    }, [userId]);

    const updateProfile = async () => {
        try {
            const res = await api.put(`/users/${userId}`, editForm);
            setUser(res.data);
            toast.success("Profile updated");
        } catch {
            toast.error("Update failed");
        }
    };

    const changePassword = async () => {
        if (passwordForm.new !== passwordForm.confirm) {
            toast.error("Passwords do not match");
            return;
        }

        try {
            await api.post(`/users/change-password`, {
                oldPassword: passwordForm.old,
                newPassword: passwordForm.new
            });
            toast.success("Password updated");
            setPasswordForm({ old: "", new: "", confirm: "" });
        } catch {
            toast.error("Incorrect old password");
        }
    };

    const uploadImage = async () => {
        if (!imageFile) return toast.error("Select a file first");

        const fd = new FormData();
        fd.append("file", imageFile);

        try {
            const res = await api.post(`/users/${userId}/upload-image`, fd, {
                headers: { "Content-Type": "multipart/form-data" }
            });

            setUser(res.data);
            toast.success("Image updated");
        } catch {
            toast.error("Upload failed");
        }
    };

    if (loading) return <div className="p-6">Loading...</div>;

    return (
        <div className="max-w-4xl mx-auto bg-white p-8 rounded-xl shadow mt-6 border border-gray-200">
            <h1 className="text-3xl font-bold mb-6 text-slate-800">Profile</h1>

            {/* Tabs */}
            <div className="flex gap-6 border-b mb-6 text-sm">
                {["info", "edit", "password", "image"].map((t) => (
                    <button
                        key={t}
                        onClick={() => setTab(t)}
                        className={`pb-2 font-medium ${
                            tab === t
                                ? "text-blue-600 border-b-2 border-blue-600"
                                : "text-gray-500 hover:text-gray-700"
                        }`}
                    >
                        {t === "info" && "Profile Info"}
                        {t === "edit" && "Edit Profile"}
                        {t === "password" && "Change Password"}
                        {t === "image" && "Profile Image"}
                    </button>
                ))}
            </div>

            {/* ---- TAB CONTENT ---- */}

            {/* Profile info */}
            {tab === "info" && (
                <div className="space-y-3 text-gray-700">
                    <p><strong>Name:</strong> {user.firstName} {user.lastName}</p>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Role:</strong> {user.role}</p>
                    <p><strong>Phone:</strong> {user.phone || "Not added"}</p>
                    <p><strong>Bio:</strong> {user.bio || "No bio available"}</p>
                    <p><strong>Joined:</strong> {new Date(user.createdAt).toLocaleString()}</p>

                    <div className="mt-6">
                        {user.profileImage ? (
                            <img
                                src={`http://localhost:8080${user.profileImage}`}
                                alt="Profile"
                                className="w-40 h-40 rounded-full object-cover border"
                            />
                        ) : (
                            <div className="w-40 h-40 rounded-full bg-gray-200 flex items-center justify-center text-gray-500">
                                No Image
                            </div>
                        )}
                    </div>
                </div>
            )}

            {/* Edit profile */}
            {tab === "edit" && (
                <div className="space-y-4">
                    <input
                        className="input"
                        placeholder="First name"
                        value={editForm.firstName}
                        onChange={(e) => setEditForm({ ...editForm, firstName: e.target.value })}
                    />
                    <input
                        className="input"
                        placeholder="Last name"
                        value={editForm.lastName}
                        onChange={(e) => setEditForm({ ...editForm, lastName: e.target.value })}
                    />
                    <input
                        className="input"
                        placeholder="Phone"
                        value={editForm.phone}
                        onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })}
                    />
                    <textarea
                        className="input h-28"
                        placeholder="Bio"
                        value={editForm.bio}
                        onChange={(e) => setEditForm({ ...editForm, bio: e.target.value })}
                    />
                    <button onClick={updateProfile} className="btn-primary">Save Changes</button>
                </div>
            )}

            {/* Change password */}
            {tab === "password" && (
                <div className="space-y-4">
                    <input
                        type="password"
                        className="input"
                        placeholder="Old password"
                        value={passwordForm.old}
                        onChange={(e) => setPasswordForm({ ...passwordForm, old: e.target.value })}
                    />
                    <input
                        type="password"
                        className="input"
                        placeholder="New password"
                        value={passwordForm.new}
                        onChange={(e) => setPasswordForm({ ...passwordForm, new: e.target.value })}
                    />
                    <input
                        type="password"
                        className="input"
                        placeholder="Confirm password"
                        value={passwordForm.confirm}
                        onChange={(e) => setPasswordForm({ ...passwordForm, confirm: e.target.value })}
                    />
                    <button onClick={changePassword} className="btn-primary">Update Password</button>
                </div>
            )}

            {/* Profile image upload */}
            {tab === "image" && (
                <div className="space-y-4">
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => {
                            setImageFile(e.target.files[0]);
                            setPreview(URL.createObjectURL(e.target.files[0]));
                        }}
                    />

                    {preview && (
                        <img
                            src={preview}
                            alt="Preview"
                            className="w-40 h-40 rounded-full object-cover mt-4 border"
                        />
                    )}

                    <button onClick={uploadImage} className="btn-primary">Upload Image</button>
                </div>
            )}
        </div>
    );
}
