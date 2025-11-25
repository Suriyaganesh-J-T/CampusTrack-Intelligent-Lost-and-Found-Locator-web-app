import React, { useState } from "react";
import api from "../services/api";
import { useNavigate } from "react-router-dom";
import { useFormik } from "formik";
import * as yup from "yup";

const schema = yup.object({
    itemName: yup.string().required(),
    dateReported: yup.date().required()
});

const CATEGORIES = [
    "Electronics",
    "Documents",
    "Clothing",
    "Accessories",
    "Keys",
    "Other"
];

export default function FoundForm(){
    const navigate = useNavigate();
    const [preview, setPreview] = useState(null);
    const [file, setFile] = useState(null);

    const formik = useFormik({
        initialValues: { itemName:'', itemType:'', itemModel:'', place:'', dateReported:'', category:'Other', tags:'' },
        validationSchema: schema,
        onSubmit: async (values, { setSubmitting }) => {
            try {
                const formData = new FormData();
                formData.append("type", "FOUND");
                formData.append("itemName", values.itemName);
                formData.append("itemType", values.itemType || "");
                formData.append("itemModel", values.itemModel || "");
                formData.append("place", values.place || "");
                formData.append("dateReported", values.dateReported || "");
                formData.append("category", values.category || "Other");
                formData.append("tags", values.tags || "");
                if (file) formData.append("image", file);

                const res = await api.post("/posts", formData);
                alert("Reported successfully");
                navigate("/");
            } catch (err) {
                console.error("Submit error", err);
                alert(err?.response?.data || err?.message || "Submission failed");
            } finally {
                setSubmitting(false);
            }
        }
    });

    const onFileChange = (e) => {
        const f = e.target.files[0];
        setFile(f);
        setPreview(f ? URL.createObjectURL(f) : null);
    };

    return (
        <div className="max-w-2xl mx-auto p-4">
            <h2 className="text-xl font-semibold mb-4">Report Found Item</h2>
            <form onSubmit={formik.handleSubmit} className="bg-white p-6 rounded-lg shadow-sm">
                <label className="block mb-2">Item Name</label>
                <input name="itemName" className="w-full border rounded px-3 py-2 mb-3" onChange={formik.handleChange} value={formik.values.itemName} />
                <label className="block mb-2">Item Type</label>
                <input name="itemType" className="w-full border rounded px-3 py-2 mb-3" onChange={formik.handleChange} value={formik.values.itemType} />
                <label className="block mb-2">Model / Identifier</label>
                <input name="itemModel" className="w-full border rounded px-3 py-2 mb-3" onChange={formik.handleChange} value={formik.values.itemModel} />
                <label className="block mb-2">Place</label>
                <input name="place" className="w-full border rounded px-3 py-2 mb-3" onChange={formik.handleChange} value={formik.values.place} />
                <label className="block mb-2">Date</label>
                <input name="dateReported" type="date" className="w-full border rounded px-3 py-2 mb-3" onChange={formik.handleChange} value={formik.values.dateReported} />

                <label className="block mb-2">Category</label>
                <select name="category" className="w-full border rounded px-3 py-2 mb-3" onChange={formik.handleChange} value={formik.values.category}>
                    {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
                </select>

                <label className="block mb-2">Tags (comma separated)</label>
                <input name="tags" placeholder="e.g. wallet,black,leather" className="w-full border rounded px-3 py-2 mb-3" onChange={formik.handleChange} value={formik.values.tags} />

                <label className="block mb-2">Image (optional)</label>
                <input type="file" accept="image/*" onChange={onFileChange} className="mb-3" />
                {preview && <img src={preview} className="rounded-md mb-3 max-w-xs" alt="preview" />}

                <div className="flex items-center gap-3">
                    <button type="submit" disabled={formik.isSubmitting} className="px-4 py-2 bg-emerald-600 text-white rounded">Submit</button>
                    <button type="button" onClick={() => navigate('/')} className="px-4 py-2 border rounded">Cancel</button>
                </div>
            </form>
        </div>
    );
}
