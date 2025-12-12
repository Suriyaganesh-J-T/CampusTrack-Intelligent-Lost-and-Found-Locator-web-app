import { useState } from "react";
import api from "../../services/api";
import toast from "react-hot-toast";

export default function UploadImageTab({ user, setUser }) {
    const [file, setFile] = useState(null);
    const [preview, setPreview] = useState(user.profileImage);

    const upload = async () => {
        if (!file) return toast.error("Select an image first");

        const formData = new FormData();
        formData.append("file", file);

        api.post(`/users/${user.userId}/upload-image`, formData)
            .then((res) => {
                toast.success("Image updated");
                setUser(res.data);
                setPreview(res.data.profileImage);
            })
            .catch(() => toast.error("Upload failed"));
    };

    return (
        <div className="space-y-4">
            {preview && (
                <img src={preview} className="w-28 h-28 rounded-full border" />
            )}

            <input
                type="file"
                accept="image/*"
                onChange={(e) => {
                    setFile(e.target.files[0]);
                    setPreview(URL.createObjectURL(e.target.files[0]));
                }}
            />

            <button
                onClick={upload}
                className="bg-sky-600 text-white px-4 py-2 rounded"
            >
                Upload
            </button>
        </div>
    );
}
