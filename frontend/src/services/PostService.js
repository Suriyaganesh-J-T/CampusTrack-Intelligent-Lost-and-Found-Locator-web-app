import api from "./api";

export async function createPost(formData) {
    // do not set Content-Type header here
    const res = await api.post("/posts", formData);
    return res.data;
}

export async function fetchAll() {
    const res = await api.get("/posts");
    return res.data;
}
