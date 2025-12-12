import api from "../services/api";
import toast from "react-hot-toast";

export default function RecoverButton({ postId, onRecovered }) {
    const markRecovered = async () => {
        try {
            await api.post(`/posts/recover/${postId}`);
            toast.success("Item successfully marked as recovered!");
            if (onRecovered) onRecovered();
        } catch (err) {
            toast.error(err.response?.data || "Failed to mark recovered");
        }
    };

    return (
        <button
            onClick={markRecovered}
            className="px-3 py-1 rounded bg-green-600 text-white hover:bg-green-700"
        >
            Mark Recovered
        </button>
    );
}
